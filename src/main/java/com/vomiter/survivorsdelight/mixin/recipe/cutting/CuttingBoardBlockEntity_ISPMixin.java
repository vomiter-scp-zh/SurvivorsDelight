package com.vomiter.survivorsdelight.mixin.recipe.cutting;

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
        CuttingBoardBlockEntity self = (CuttingBoardBlockEntity)(Object)this;
        Level level = self.getLevel();
        if (level == null) { cir.setReturnValue(false); return; }
        var optRecipe = getMatchingRecipe(toolStack, player);
        if(optRecipe.isEmpty()) return;
        if(!(optRecipe.get().value() instanceof SDCuttingRecipe recipe)) return;
        int fortune = EnchantmentHelper.getTagEnchantmentLevel(SDUtils.getEnchantHolder(level, Enchantments.FORTUNE), toolStack);
        double fortuneBonus = Configuration.CUTTING_BOARD_FORTUNE_BONUS.get() * (double)fortune;
        List<ItemStack> out = new ArrayList<>();
        for (SDCuttingRecipe.Output r : recipe.getOutputs()) {
            if(r instanceof SDCuttingRecipe.StackOutput stackOutput) {
                ItemStack s = stackOutput.getISPResult(level).getStack(getStoredItem());
                if(level.random.nextFloat() > fortuneBonus + stackOutput.chance()){
                    s.shrink(1);
                }
                if (!s.isEmpty()) out.add(s);
            }
        }

        Direction dir = self.getBlockState().getValue(CuttingBoardBlock.FACING).getCounterClockWise();
        for (ItemStack resultStack : out) {
            ItemUtils.spawnItemEntity(
                    level,
                    resultStack.copy(),
                    self.getBlockPos().getX() + 0.5D + dir.getStepX() * 0.2D,
                    self.getBlockPos().getY() + 0.2D,
                    self.getBlockPos().getZ() + 0.5D + dir.getStepZ() * 0.2D,
                    dir.getStepX() * 0.2F, 0.0F, dir.getStepZ() * 0.2F
            );
        }

        toolStack.hurtAndBreak(1, (ServerLevel)level, player, (item) -> {});

        this.playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, this.getStoredItem());
        this.removeItem();
        if (player instanceof ServerPlayer sp) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger(sp);
        }
        cir.setReturnValue(true);


}}
