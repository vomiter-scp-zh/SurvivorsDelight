package com.vomiter.survivorsdelight.common.food;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class FoodContainerExpansion {

    private static final Map<Item, List<Predicate<ItemStack>>> EXTRA_VALID_CONTAINERS = new ConcurrentHashMap<>();

    public static void register(Item mealContainer, Predicate<ItemStack> predicate) {
        EXTRA_VALID_CONTAINERS
                .computeIfAbsent(mealContainer, k -> new ArrayList<>())
                .add(predicate);
    }

    public static boolean isExtraValid(Item mealContainer, ItemStack containerStack) {
        var list = EXTRA_VALID_CONTAINERS.get(mealContainer);
        if (list == null) return false;

        for (var pred : list) {
            if (pred.test(containerStack)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack replaceStack(Item originalContainer, ItemStack containerStack){
        if(isExtraValid(originalContainer, containerStack)){
            return originalContainer.getDefaultInstance();
        }
        return containerStack;
    }


    public static ItemStack replaceStack(ItemStack containerStack){
        for (Item item : EXTRA_VALID_CONTAINERS.keySet()) {
            boolean match = isExtraValid(item, containerStack);
            if(match) return new ItemStack(item, containerStack.getCount());
        }
        return containerStack;
    }
}