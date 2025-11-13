package com.vomiter.survivorsdelight.core.food.block;

import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DecayingPieBlockEntity extends SDDecayingBlockEntity {
    public DecayingPieBlockEntity(BlockPos pos, BlockState state) {
        super(SDBlockEntityTypes.PIE_DECAYING.get(), pos, state);
    }
}