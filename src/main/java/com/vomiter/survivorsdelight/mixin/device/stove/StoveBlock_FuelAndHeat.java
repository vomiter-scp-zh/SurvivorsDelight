package com.vomiter.survivorsdelight.mixin.device.stove;

import com.vomiter.survivorsdelight.adapter.stove.StoveAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;

@Mixin(value = AbstractStoveBlock.class, remap = false)
public class StoveBlock_FuelAndHeat{
    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
    private void addFuel(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
            ) {
        boolean success = StoveAdapter.addFuel(level, pos, player, hand);
        if(success) cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }

    /*
    @Inject(method = "tryToPlaceFoodItem", at = @At("HEAD"), remap = false, cancellable = true)
    private void addFood(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack heldItem = player.getItemInHand(hand);
        AbstractStoveBlockEntity stove = (AbstractStoveBlockEntity) level.getBlockEntity(pos);
        IStoveBlockEntity iStove = (IStoveBlockEntity)stove;
        assert iStove != null;
        if(iStove.sdtfc$addItem(heldItem, stove.getNextEmptySlot(), iStove, player)) cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }

     */
}
