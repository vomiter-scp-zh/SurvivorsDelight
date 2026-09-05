package com.vomiter.survivorsdelight.mixin.recipe.cooking;

import com.vomiter.survivorsdelight.adapter.cooking_pot.dynamic.ICookingPotRecipeBalanceFactor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotRecipe.class)
public class CookingPotRecipe_BalanceFactorDuckMixin implements ICookingPotRecipeBalanceFactor {
    @Unique
    private float sdtfc$balanceFactor = 0.04f;

    @Override
    public void sdtfc$setBalanceFactor(float f) {
        sdtfc$balanceFactor = f;
    }

    @Override
    public float sdtfc$getBalanceFactor() {
        return sdtfc$balanceFactor;
    }
}
