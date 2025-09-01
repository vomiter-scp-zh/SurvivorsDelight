package com.vomiter.survivorsdelight.core.food.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DecayingPieBlockEntity extends SDDecayingBlockEntity {
    public DecayingPieBlockEntity(BlockPos pos, BlockState state) {
        super(SDDecayingBlockEntityRegistry.PIE_DECAYING.get(), pos, state);
    }
}