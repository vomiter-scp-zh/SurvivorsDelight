package com.vomiter.survivorsdelight.mixin.food;

import net.dries007.tfc.common.capabilities.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public interface FoodDataAccessor {
    @Accessor("water")
    public void sdtfc$setWater(float f);
}
