package com.vomiter.survivorsdelight.mixin.food.remainder;

import com.vomiter.survivorsdelight.core.food.IConsumableRemainder;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.item.ConsumableItem;

@Mixin(ConsumableItem.class)
public abstract class ConsumableItem_RemainderMixin implements IConsumableRemainder {
}
