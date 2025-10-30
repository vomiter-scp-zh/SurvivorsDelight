package com.vomiter.survivorsdelight.mixin.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.FoodValues;

@Mixin(value = FoodValues.class, remap = false)
public abstract class FoodValuesMixin {

    @Mutable @Shadow @Final public static FoodProperties APPLE_CIDER;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void sd$tweakFoodValues(CallbackInfo ci) {
        APPLE_CIDER = (new FoodProperties.Builder())
                .alwaysEat().effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1), 1.0F).build();
    }
}
