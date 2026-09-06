package com.vomiter.survivorsdelight.mixin.food.effect;

import com.vomiter.survivorsdelight.util.SDThreadLocals;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TFCFoodData.class, remap = false)
public class TFCFoodDataEatMixin {
    @Inject(method = "eat(Lnet/dries007/tfc/common/capabilities/food/IFood;)V", at = @At("HEAD"))
    private void sdtfc$eat(IFood food, CallbackInfo ci){
        SDThreadLocals.shouldApplyEating.remove();
    }
}
