package com.vomiter.survivorsdelight.core.food.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DecayingFeastBlockEntity extends SDDecayingBlockEntity {
    public DecayingFeastBlockEntity(BlockPos pos, BlockState state) {
        super(SDDecayingBlockEntityRegistry.FEAST_DECAYING.get(), pos, state);
    }
}