package com.vomiter.survivorsdelight.common.cabinet;

import com.vomiter.survivorsdelight.common.food.SDFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CabinetFoodStackHelper {

    private static final FoodTrait CABINET_STORED = SDFoodTraits.CABINET_STORED;

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

    public static void setStored(ItemStack food){
        FoodCapability.applyTrait(food, CABINET_STORED);
    }

    public static void removeStored(ItemStack food){
        FoodCapability.removeTrait(food, CABINET_STORED);
    }

}
