package com.vomiter.survivorsdelight.common.device.cooking_pot.wrap;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class CookingPotFluidRecipeWrapper extends RecipeWrapper implements IFluidAccess {
    private final FluidStack tank;
    public CookingPotFluidRecipeWrapper(IItemHandler items, FluidStack tankSnapshot) {
        super((IItemHandlerModifiable) items);
        this.tank = tankSnapshot.copy();
    }
    @Override public FluidStack getFluidInTank() { return tank; }
}