package com.vomiter.survivorsdelight.mixin.food.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.item.DrinkableItem;
import vectorwing.farmersdelight.common.item.MelonJuiceItem;

@Mixin(value = MelonJuiceItem.class, remap = false)
public abstract class MelonJuiceItemMixin extends DrinkableItem {

    MelonJuiceItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyConstant(method = "affectConsumer", constant = @Constant(floatValue = 2.0f))
    private float adjustHeal(float original){
        return 4.0f;
    }

    @Inject(method = "affectConsumer", at = @At("HEAD"))
    private void sdtfc$addNutrient(ItemStack stack, Level level, LivingEntity consumer, CallbackInfo ci){
        consumer.eat(consumer.level(), stack);
    }

}
