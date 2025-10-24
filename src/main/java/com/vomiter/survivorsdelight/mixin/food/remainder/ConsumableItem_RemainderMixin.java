package com.vomiter.survivorsdelight.mixin.food.remainder;

import com.vomiter.survivorsdelight.core.food.IConsumableRemainder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.ConsumableItem;

@Mixin(ConsumableItem.class)
public abstract class ConsumableItem_RemainderMixin implements IConsumableRemainder {

}
