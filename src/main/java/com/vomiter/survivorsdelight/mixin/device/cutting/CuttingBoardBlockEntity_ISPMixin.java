package com.vomiter.survivorsdelight.mixin.device.cutting;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.registry.recipe.SDCuttingRecipe;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(CuttingBoardBlockEntity.class)
public abstract class CuttingBoardBlockEntity_ISPMixin {

    @Shadow public abstract ItemStack getStoredItem();
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract ItemStack removeItem();
    @Shadow public abstract void playProcessingSound(@org.jetbrains.annotations.Nullable SoundEvent sound, ItemStack tool, ItemStack boardItem);

    @Shadow protected abstract Optional<RecipeHolder<CuttingBoardRecipe>> getMatchingRecipe(ItemStack toolStack, @org.jetbrains.annotations.Nullable Player player);

    @Inject(method = "processStoredItemUsingTool", at = @At("HEAD"), cancellable = true, remap = false)
    private void sd$processWithISP(ItemStack toolStack, @Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingBoardBlockEntity self = (CuttingBoardBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) {
            return;
        }

        // ★ client 端不跑 ISP，交給原版 + server 同步
        if (level.isClientSide()) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        var optRecipe = getMatchingRecipe(toolStack, player);
        if (optRecipe.isEmpty()) {
            return; // 讓原版流程處理其他配方
        }
        if (!(optRecipe.get().value() instanceof SDCuttingRecipe recipe)) {
            return; // 非 SDCuttingRecipe -> 原版處理
        }

        int fortune = EnchantmentHelper.getTagEnchantmentLevel(
                SDUtils.getEnchantHolder(level, Enchantments.FORTUNE),
                toolStack
        );
        double fortuneBonus = Configuration.CUTTING_BOARD_FORTUNE_BONUS.get() * (double) fortune;

        List<ItemStack> out = new ArrayList<>();

        for (SDCuttingRecipe.Output r : recipe.getOutputs()) {
            // 1) 先用 ISP 算出實際要掉的東西（套好 modifier）
            ItemStack stack = r.getISPResult(level).getStack(getStoredItem());
            if (stack.isEmpty()) {
                continue;
            }

            // 2) 決定這個 output 的 base chance
            float baseChance = 1.0f;
            if (r instanceof SDCuttingRecipe.StackOutput stackOutput) {
                baseChance = stackOutput.chance();
            }
            // ProviderOutput 就當成 1.0f

            float totalChance = (float) (baseChance + fortuneBonus);
            // 安全一點夾在 [0, 1] 之間
            if (totalChance <= 0f) {
                continue;
            }
            if (totalChance > 1f) {
                totalChance = 1f;
            }

            float roll = level.random.nextFloat();
            // 3) 如果沒過機率就跳過這個 output
            if (roll >= totalChance) {
                continue;
            }

            out.add(stack);
        }

        // 這個 log 可以暫時留著看看
        SurvivorsDelight.LOGGER.info("[Cutting ISP] side=server, pos={}, outputs(before filter)={}, kept={}",
                self.getBlockPos(), recipe.getOutputs().size(), out.size());

        Direction dir = self.getBlockState().getValue(CuttingBoardBlock.FACING).getCounterClockWise();
        for (ItemStack resultStack : out) {
            ItemUtils.spawnItemEntity(
                    serverLevel,
                    resultStack.copy(),
                    self.getBlockPos().getX() + 0.5D + dir.getStepX() * 0.2D,
                    self.getBlockPos().getY() + 0.2D,
                    self.getBlockPos().getZ() + 0.5D + dir.getStepZ() * 0.2D,
                    dir.getStepX() * 0.2F, 0.0F, dir.getStepZ() * 0.2F
            );
        }

        toolStack.hurtAndBreak(1, serverLevel, player, (item) -> {});

        this.playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, this.getStoredItem());
        this.removeItem();

        if (player instanceof ServerPlayer sp) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger(sp);
        }

        // 攔截原本的 processStoredItemUsingTool
        cir.setReturnValue(true);
    }
}
