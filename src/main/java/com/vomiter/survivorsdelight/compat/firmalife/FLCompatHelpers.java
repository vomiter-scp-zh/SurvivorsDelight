package com.vomiter.survivorsdelight.compat.firmalife;

import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FLCompatHelpers {
    public static FoodTrait getShelvedFoodTrait(BlockEntity be)
    {
        if (be.getLevel() != null)
        {
            final float temp = Climate.getAverageTemperature(be.getLevel(), be.getBlockPos());
            if (temp < FLConfig.SERVER.cellarLevel3Temperature.get())
            {
                return FLFoodTraits.SHELVED_3;
            }
            if (temp < FLConfig.SERVER.cellarLevel2Temperature.get())
            {
                return FLFoodTraits.SHELVED_2;
            }
        }
        return FLFoodTraits.SHELVED;
    }

    public static FoodTrait[] getPossibleShelvedFoodTraits(){
        return new FoodTrait[]{FLFoodTraits.SHELVED, FLFoodTraits.SHELVED_2, FLFoodTraits.SHELVED_3};
    }

}
