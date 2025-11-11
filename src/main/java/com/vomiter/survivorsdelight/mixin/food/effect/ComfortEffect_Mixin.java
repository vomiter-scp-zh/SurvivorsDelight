package com.vomiter.survivorsdelight.mixin.food.effect;

import com.vomiter.survivorsabilities.core.SAAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.effect.ComfortEffect;

import java.util.UUID;

@Mixin(ComfortEffect.class)
public class ComfortEffect_Mixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addAttributes(CallbackInfo ci){
        var self = (ComfortEffect)(Object)this;
        UUID SD_COMFORT_UUID = UUID.fromString("a97ff93f-389d-4e03-81e3-58724ba0f915");
        self.addAttributeModifier(SAAttributes.RESILIENCE.get(), SD_COMFORT_UUID.toString() ,3, AttributeModifier.Operation.ADDITION);
    }

    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    private void disableVanilla(LivingEntity entity, int amplifier, CallbackInfo ci){
        ci.cancel();
    }

}
