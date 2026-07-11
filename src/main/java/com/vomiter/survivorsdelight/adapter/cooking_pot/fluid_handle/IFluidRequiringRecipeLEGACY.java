package com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface IFluidRequiringRecipeLEGACY {
    @Nullable SizedFluidIngredient sdtfc$getFluidIngredient();
    int sdtfc$getRequiredFluidAmount();
    void sdtfc$setFluidRequirement(@Nullable SizedFluidIngredient ing, int amount);
}
