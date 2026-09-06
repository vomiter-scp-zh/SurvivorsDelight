package com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;


public class CookingPotFluidRecipeWrapper extends RecipeWrapper {
    private final FluidStack tank;
    public CookingPotFluidRecipeWrapper(IItemHandler items, FluidStack tankSnapshot) {
        super(items);
        this.tank = tankSnapshot.copy();
    }
    public FluidStack getFluidInTank() { return tank; }
}