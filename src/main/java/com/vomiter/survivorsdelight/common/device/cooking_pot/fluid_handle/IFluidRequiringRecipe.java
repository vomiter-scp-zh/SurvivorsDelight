package com.vomiter.survivorsdelight.common.device.cooking_pot.fluid_handle;

import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import org.jetbrains.annotations.Nullable;

public interface IFluidRequiringRecipe {
    @Nullable FluidStackIngredient sdtfc$getFluidIngredient();
    int sdtfc$getRequiredFluidAmount();
    void sdtfc$setFluidRequirement(@Nullable FluidStackIngredient ing, int amount);
}
