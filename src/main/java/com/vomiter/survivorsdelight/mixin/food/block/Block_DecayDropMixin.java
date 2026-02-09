package com.vomiter.survivorsdelight.mixin.food.block;

import com.vomiter.survivorsdelight.common.food.block.SDDecayingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class Block_DecayDropMixin{
    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void sdtfc$sdtfc$modifyDecayDropInject1(BlockState p_49875_, ServerLevel p_49876_, BlockPos p_49877_, BlockEntity blockEntity, Entity p_49879_, ItemStack p_49880_, CallbackInfoReturnable<List<ItemStack>> cir){
        cir.setReturnValue(SDDecayingBlockEntity.modifyDecayDrop(blockEntity, cir.getReturnValue()));
    }

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void sdtfc$sdtfc$modifyDecayDropInject2(BlockState p_49870_, ServerLevel p_49871_, BlockPos p_49872_, BlockEntity blockEntity, CallbackInfoReturnable<List<ItemStack>> cir){
        cir.setReturnValue(SDDecayingBlockEntity.modifyDecayDrop(blockEntity, cir.getReturnValue()));
    }

}
