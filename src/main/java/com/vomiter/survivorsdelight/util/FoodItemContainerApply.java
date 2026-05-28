package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.minecraft.nbt.CompoundTag;
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

    public static ItemStack getContainer(ItemStack stack){
        var tag = stack.getTag();
        if(tag == null) return ItemStack.EMPTY;
        if(tag.get(NBT_SOUP_BOWL) instanceof CompoundTag ct) return ItemStack.of(ct);
        if(tag.get(NBT_CONTAINER) instanceof CompoundTag ct) return ItemStack.of(ct);
        return ItemStack.EMPTY;
    }

    public static ItemStack getRemainder(ItemStack stack){
        var container = getContainer(stack);
        return container.is(SDTags.ItemTags.CONTAINER_NO_REMAINDER)? ItemStack.EMPTY: container;
    }
}
