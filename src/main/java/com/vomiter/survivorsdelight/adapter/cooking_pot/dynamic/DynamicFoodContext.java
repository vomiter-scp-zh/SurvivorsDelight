package com.vomiter.survivorsdelight.adapter.cooking_pot.dynamic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public record DynamicFoodContext<R>(
        List<ItemStack> inputs,
        FluidStack inputFluid,
        ItemStack stack, //can be the input or the result, depending on the phase
        R recipe,
        ResourceLocation recipeId,
        Phase phase,
        Level level
) {
    public enum Phase {
        INDIVIDUAL,
        TOTAL
    }
}