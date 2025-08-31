package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.HeatSourceBlockEntity;
import com.vomiter.survivorsdelight.data.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HeatHelper {
    public static float getTemperature(BlockPos pos, LevelReader level){
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof HeatSourceBlockEntity heatSourceBlockEntity){
            return heatSourceBlockEntity.sdtfc$getTemperature();
        }
        if(blockState.is(ModBlockTags.STATIC_HEAT_500)){
            return 500f;
        } else if (blockState.is(ModBlockTags.STATIC_HEAT_1500)) {
            return 1500f;
        } else if (blockState.is(ModBlockTags.STATIC_HEAT_250)){
            return 250f;
        }
        return 0;
    }
}
