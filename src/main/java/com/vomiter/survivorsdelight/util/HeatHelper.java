package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.HeatSourceBlockEntity;
import com.vomiter.survivorsdelight.data.tags.SDBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.tag.ModTags;

public class HeatHelper {
    public static float getTemperature(BlockPos pos, LevelReader level){
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof HeatSourceBlockEntity heatSourceBlockEntity){
            return heatSourceBlockEntity.sdtfc$getTemperature();
        }
        if(blockState.is(SDBlockTags.STATIC_HEAT_500)){
            return 500f;
        } else if (blockState.is(SDBlockTags.STATIC_HEAT_1500)) {
            return 1500f;
        } else if (blockState.is(SDBlockTags.STATIC_HEAT_250)){
            return 250f;
        }
        return 0;
    }

    public static float getTargetTemperature(BlockPos pos, LevelReader level, boolean requiresDirectHeat){
        if (level == null) return 0f;
        BlockPos below = pos.below();
        float heatBelow = HeatHelper.getTemperature(below, level);
        if(heatBelow > 0) return heatBelow;
        if(!requiresDirectHeat && level.getBlockState(below).is(ModTags.HEAT_CONDUCTORS)){
            return HeatHelper.getTemperature(below.below(), level);
        }
        return 0;
    }
}
