package com.vomiter.survivorsdelight.core.foodBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FeastDecayingBlockEntity extends FDDecayingBlockEntity {
    public FeastDecayingBlockEntity(BlockPos pos, BlockState state) {
        super(FDDecayingBlockEntityRegistry.FEAST_DECAYING.get(), pos, state);
    }
}