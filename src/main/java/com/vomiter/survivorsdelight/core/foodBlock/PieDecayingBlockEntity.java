package com.vomiter.survivorsdelight.core.foodBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PieDecayingBlockEntity extends FDDecayingBlockEntity {
    public PieDecayingBlockEntity(BlockPos pos, BlockState state) {
        super(FDDecayingBlockEntityRegistry.PIE_DECAYING.get(), pos, state);
    }
}