package com.vomiter.survivorsdelight.mixin.farming.farmland;

import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class Block_CropExtraSeedDropMixin{
    /*
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private static void addSeeds(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfoReturnable<List<ItemStack>> cir){
        List<ItemStack> drops = cir.getReturnValue();
        drops.forEach(d -> {
            if(d.is(Tags.Items.SEEDS)) d.grow(1);
        });
        cir.setReturnValue(drops);
    }
     */
}
