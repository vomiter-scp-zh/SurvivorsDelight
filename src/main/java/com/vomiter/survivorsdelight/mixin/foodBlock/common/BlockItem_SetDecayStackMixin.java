package com.vomiter.survivorsdelight.mixin.foodBlock.common;

import com.vomiter.survivorsdelight.core.foodBlock.FDDecayingBlockEntity;
import com.vomiter.survivorsdelight.core.foodBlock.FDIDecayingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItem_SetDecayStackMixin {

    @Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("TAIL"))
    private void sdtfc$afterPlaced(BlockPos pos, Level level, Player player,
                                   ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof FDIDecayingBlock)) return;

        if (level.getBlockEntity(pos) instanceof FDDecayingBlockEntity decay) {
            decay.setStack(stack.copy());
            decay.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
