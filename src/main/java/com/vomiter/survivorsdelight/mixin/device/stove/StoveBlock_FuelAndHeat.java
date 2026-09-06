package com.vomiter.survivorsdelight.mixin.device.stove;

import com.vomiter.survivorsdelight.adapter.stove.IStoveBlockEntity;
import com.vomiter.survivorsdelight.adapter.stove.StoveAdapter;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.data.Fuel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(value = AbstractStoveBlock.class, remap = false)
public class StoveBlock_FuelAndHeat{
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true, remap = true)
    private void addFuel(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        boolean result = StoveAdapter.addFuel(level, pos, player, hand);
        if (result) cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide));
    }

    @Inject(method = "tryToPlaceFoodItem", at = @At("HEAD"), cancellable = true, remap = true)
    private void addFood(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir){
        StoveBlockEntity stove = (StoveBlockEntity) level.getBlockEntity(pos);
        IStoveBlockEntity iStove = (IStoveBlockEntity)stove;
        assert iStove != null;
        if(StoveAdapter.addItem(heldItem, stove.getNextEmptySlot(), stove, player)) cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide));
    }




}