package com.vomiter.survivorsdelight.util;

import net.minecraft.world.item.ItemStack;

public class FoodItemContainerApply {
    // NBT keys
    private static final String NBT_SOUP_BOWL = "bowl";
    private static final String NBT_CONTAINER = "Container";

    public static ItemStack applySoup(ItemStack mealStack, ItemStack containerStack){
        mealStack.getOrCreateTag().put(NBT_SOUP_BOWL, containerStack.split(1).serializeNBT());
        return mealStack;
    }

    public static ItemStack applyGeneral(ItemStack mealStack, ItemStack containerStack){
        mealStack.getOrCreateTag().put(NBT_CONTAINER, containerStack.split(1).serializeNBT());
        return mealStack;
    }

}
