package com.vomiter.survivorsdelight.mixin.recipe.cutting;

import com.vomiter.survivorsdelight.data.recipe.SDCuttingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(CuttingBoardBlockEntity.class)
public abstract class CuttingBoardBlockEntity_ISPMixin {

    // ---- 直接沿用原本會用到的方法與欄位 ----
    @Shadow public abstract ItemStack getStoredItem();
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract void playProcessingSound(String soundEventID, ItemStack tool, ItemStack boardItem);
    @Shadow public abstract ItemStack removeItem();
    @Shadow private native Optional<CuttingBoardRecipe> getMatchingRecipe(RecipeWrapper wrapper, ItemStack toolStack, @Nullable Player player);

    @Shadow @Final private ItemStackHandler inventory;

    @Inject(method = "processStoredItemUsingTool", at = @At("HEAD"), cancellable = true, remap = false)
    private void sd$processWithISP(ItemStack toolStack, @Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingBoardBlockEntity self = (CuttingBoardBlockEntity)(Object)this;
        Level level = self.getLevel();
        if (level == null) { cir.setReturnValue(false); return; }
        var optRecipe = getMatchingRecipe(new RecipeWrapper(inventory), toolStack, player);
        if(optRecipe.isEmpty()) return;
        if(!(optRecipe.get() instanceof SDCuttingRecipe recipe)) return;

        int fortune = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, toolStack);
        List<ItemStack> out = new ArrayList<>();
        for (ChanceResult r : recipe.getVanillaResults()) {
            ItemStack s = r.rollOutput(level.random, fortune);
            if (!s.isEmpty()) out.add(s);
        }

        for (ItemStackProvider isp : recipe.getProviders()) {
            ItemStack s = isp.getStack(getStoredItem());
            if (!s.isEmpty()) out.add(s);
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

        if (player != null) {
            toolStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        } else if (toolStack.hurt(1, level.random, null)) {
            toolStack.setCount(0);
        }

        this.playProcessingSound(recipe.getSoundEventID(), toolStack, this.getStoredItem());
        this.removeItem();
        if (player instanceof ServerPlayer sp) {
            vectorwing.farmersdelight.common.registry.ModAdvancements.CUTTING_BOARD.trigger(sp);
        }

        cir.setReturnValue(true);


}}
