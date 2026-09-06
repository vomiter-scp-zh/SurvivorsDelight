package com.vomiter.survivorsdelight.adapter.cooking_pot.dynamic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record DynamicFoodContext<R>(
        List<ItemStack> inputs,
        List<FluidStack> inputFluids,
        ItemStack stack, //can be the input or the result, depending on the phase
        R recipe,
        Phase phase,
        Level level
) {
    public DynamicFoodContext(
            List<ItemStack> inputs,
            FluidStack inputFluid,
            ItemStack stack, //can be the input or the result, depending on the phase
            R recipe,
            Phase phase,
            Level level
    ){
        this(
                inputs,
                List.of(inputFluid),
                stack,
                recipe,
                phase,
                level
        );

    }

    public enum Phase {
        INDIVIDUAL,
        TOTAL
    }
}