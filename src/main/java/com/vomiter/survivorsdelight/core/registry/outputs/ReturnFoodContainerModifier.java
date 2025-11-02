package com.vomiter.survivorsdelight.core.registry.outputs;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.minecraft.world.item.ItemStack;

public enum ReturnFoodContainerModifier implements ItemStackModifier.SingleInstance<ReturnFoodContainerModifier> {
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input) {
        return input.getCraftingRemainingItem();
    }

    @Override
    public boolean dependsOnInput() {
        return true;
    }

    @Override
    public ReturnFoodContainerModifier instance() {
        return INSTANCE;
    }
}
