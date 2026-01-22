package com.vomiter.survivorsdelight.content.device.cooking_pot.wrap;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public class CookingPotFluidRecipeWrapper extends RecipeWrapper implements IFluidAccess {
    private final FluidStack tank;
    public CookingPotFluidRecipeWrapper(IItemHandler items, FluidStack tankSnapshot) {
        super((IItemHandlerModifiable) items);
        this.tank = tankSnapshot.copy();
    }
    @Override public FluidStack getFluidInTank() { return tank; }
}