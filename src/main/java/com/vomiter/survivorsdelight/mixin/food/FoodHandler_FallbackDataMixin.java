package com.vomiter.survivorsdelight.mixin.food;

import com.vomiter.survivorsdelight.data.food.SDFallbackFoodData;
import net.dries007.tfc.common.capabilities.food.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FoodCapability.class, remap = false)
public class FoodHandler_FallbackDataMixin {

    @Inject(method = "get", at = @At("RETURN"))
    private static void injectFallback(ItemStack stack, CallbackInfoReturnable<IFood> cir) {
        final IFood ret = cir.getReturnValue();
        if (!(ret instanceof FoodHandler.Dynamic dyn)) return;

        final FoodData cur = dyn.getData();
        if (!sdtfc$isEmptyData(cur)) return;

        final FoodData fb = SDFallbackFoodData.get(stack.getItem());
        if (fb == null) return;
        dyn.setFood(fb);
    }

    @Unique
    private static boolean sdtfc$isEmptyData(FoodData d) {
        if (d == null) return true;
        if (d.hunger() != 0) return false;
        if (d.saturation() != 0f) return false;
        if ((int) d.water() != 0) return false;
        if (d.decayModifier() != 0f) return false;
        for (Nutrient n : Nutrient.VALUES) {
            if (d.nutrient(n) != 0f) return false;
        }
        return true;
    }
}
