package com.vomiter.survivorsdelight.compat.emi;

import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

public interface ICookingPotEMIRecipeDuck {
    @Nullable SizedFluidIngredient sdtfc$getFluidIngredient();
    int sdtfc$getRequiredFluidAmount();
    void sdtfc$setFluidRequirement(@Nullable SizedFluidIngredient ing, int amount);
}
