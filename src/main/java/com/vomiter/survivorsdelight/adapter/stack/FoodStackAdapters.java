package com.vomiter.survivorsdelight.adapter.stack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class FoodStackAdapters {
    private FoodStackAdapters() {}

    public static boolean stackableExceptCreationDate(@NotNull ItemStack a, @NotNull ItemStack b) {
        return FoodCapability.areStacksStackableExceptCreationDate(a, b);
    }

    public static @NotNull ItemStack mergeInto(@NotNull ItemStack dst, @NotNull ItemStack src) {
        return FoodCapability.mergeItemStacks(dst, src);
    }

    public static int simulateMovedCount(@NotNull ItemStack dst, @NotNull ItemStack src) {
        if (dst.isEmpty() && src.isEmpty()) return 0;
        int before = src.getCount();

        ItemStack dstCopy = dst.copy();
        ItemStack srcCopy = src.copy();
        FoodCapability.mergeItemStacks(dstCopy, srcCopy);

        return before - srcCopy.getCount();
    }
}
