package com.vomiter.survivorsdelight.util;

import net.dries007.tfc.common.capabilities.food.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class FoodItemContainerApply {
    private static final String NBT_SOUP_BOWL = "bowl";
    private static final String NBT_CONTAINER = "Container";

    public static void applyContainerNutrient(ItemStack stack, ItemStack container) {
        IFood containerFood = FoodCapability.get(container);
        if (containerFood == null) return;
        IFood mealFood = FoodCapability.get(stack);
        if (!(mealFood instanceof FoodHandler.Dynamic dynamicFood)) return;
        FoodData foodData = dynamicFood.getData();
        FoodData containerData = containerFood.getData();

        float[] foodNutrient = foodData.nutrients();
        float[] containerNutrient = containerData.nutrients();
        float[] combinedNutrient = foodNutrient.clone();

        for (Nutrient nutrient : Nutrient.values()) {
            combinedNutrient[nutrient.ordinal()] += containerNutrient[nutrient.ordinal()] * 0.5f;
        }

        FoodData combined = FoodData.create(
                foodData.hunger(),
                foodData.water(),
                foodData.saturation(),
                combinedNutrient,
                foodData.decayModifier()
        );

        var ingredientList = new ArrayList<>(dynamicFood.getIngredients());
        ingredientList.add(container.copyWithCount(1));

        dynamicFood.setIngredients(ingredientList);
        dynamicFood.setFood(combined);
        FoodCapability.updateFoodDecayOnCreate(stack);
    }

    public static ItemStack applySoup(ItemStack mealStack, ItemStack containerStack){
        applyContainerNutrient(mealStack, containerStack);
        mealStack.getOrCreateTag().put(NBT_SOUP_BOWL, containerStack.split(1).serializeNBT());
        return mealStack;
    }

    public static ItemStack applyGeneral(ItemStack mealStack, ItemStack containerStack){
        applyContainerNutrient(mealStack, containerStack);
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
        var foodProperty = container.getFoodProperties(null);
        return foodProperty != null? ItemStack.EMPTY: container;
    }
}
