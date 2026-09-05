package com.vomiter.survivorsdelight.mixin.food.block;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vomiter.survivorsdelight.SDConfig;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.common.food.block.DecayFoodTransfer;
import com.vomiter.survivorsdelight.common.food.block.DecayingPieBlockEntity;
import com.vomiter.survivorsdelight.util.SDThreadLocals;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.PieBlock;

@Mixin(value = PieBlock.class, remap = false)
public abstract class PieBlock_SliceMixin{

    @Shadow public abstract ItemStack getPieSliceItem();

    @Unique
    private ItemStack cachedStack = ItemStack.EMPTY;

    @Inject(method = "use", at = @At("HEAD"), remap = true)
    private void sdtfc$cachePie(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        if (!cachedStack.isEmpty()) return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DecayingPieBlockEntity decay)) return;
        cachedStack = decay.getStack();
    }

    @Shadow
    public abstract int getMaxBites();

    @WrapMethod(method = "consumeBite")
    private InteractionResult sdtfc$modifyAddedEffects(
            Level level, BlockPos pos, BlockState state, Player player, Operation<InteractionResult> original
    ){
        try {
            SDThreadLocals.finishUsedItem.set(getPieSliceItem());
            SDThreadLocals.shouldApplyEating.set(true);
            return original.call(level, pos, state, player);
        } finally {
            SDThreadLocals.finishUsedItem.remove();
            SDThreadLocals.shouldApplyEating.remove();
        }
    }

    @WrapMethod(method = "getPieSliceItem")
    private ItemStack sdtfc$getPieSliceItem(Operation<ItemStack> original){
        float factor;
        if (SDConfig.REBALANCING_FEAST) factor = 1f / (float) getMaxBites();
        else factor = 1f;

        SurvivorsDelight.LOGGER.info("cached = {}", cachedStack);
        return DecayFoodTransfer.copyFoodState(cachedStack, original.call(), true, factor);
    }
}
