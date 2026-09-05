package com.vomiter.survivorsdelight.mixin.device.stove;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;

@Mixin(value = AbstractStoveBlockEntity.class, remap = false)
public interface StoveBlockEntity_Accessor {

    @Accessor("cookingTime")
    int[] getCookingTime();
}
