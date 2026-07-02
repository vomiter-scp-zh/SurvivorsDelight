package com.vomiter.survivorsdelight.compat.emi;

import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import org.jetbrains.annotations.Nullable;

public interface ICookingPotEMIRecipeDuck {
    @Nullable FluidStackIngredient sdtfc$getFluidIngredient();
    int sdtfc$getRequiredFluidAmount();
    void sdtfc$setFluidRequirement(@Nullable FluidStackIngredient ing, int amount);
}
