package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.registry.SDDataComponents;
import com.vomiter.survivorsdelight.registry.component.SDContainer;
import com.vomiter.survivorsdelight.registry.component.SDContainerStack;
import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class FoodItemContainerApply {
    public static void applyContainerNutrient(ItemStack stack, ItemStack container) {
        IFood containerFood = FoodCapability.get(container);
        if (containerFood == null) return;
        IFood mealFood = FoodCapability.get(stack);
        if (mealFood == null) return;
        FoodData foodData = mealFood.getData();
        FoodData containerData = containerFood.getData();

        float[] foodNutrient = foodData.nutrients();
        float[] containerNutrient = containerData.nutrients();
        float[] combinedNutrient = foodNutrient.clone();

        for (Nutrient nutrient : Nutrient.values()) {
            combinedNutrient[nutrient.ordinal()] += containerNutrient[nutrient.ordinal()] * 0.5f;
        }

        FoodData combined = new FoodData(
                foodData.hunger(),
                foodData.water(),
                foodData.saturation(),
                foodData.intoxication(),
                combinedNutrient,
                foodData.decayModifier()
        );
        FoodCapability.setFoodForDynamicItemOnCreate(stack, combined);

        var ingredientList = Optional.ofNullable(stack.get(TFCComponents.INGREDIENTS))
                .map(itemListComponent -> new ArrayList<>(itemListComponent.contents()))
                .orElse(null);
        if(ingredientList != null){
            ingredientList.add(container.copyWithCount(1));
            stack.set(TFCComponents.INGREDIENTS, new ItemListComponent(ingredientList));
        }
    }

    public static ItemStack applySoup(ItemStack mealStack, ItemStack containerStack){
        applyContainerNutrient(mealStack, containerStack.copyWithCount(1));
        mealStack.set(TFCComponents.BOWL, Bowl.of(containerStack));
        return mealStack;
    }

    public static ItemStack applyGeneral(ItemStack mealStack, ItemStack containerStack){
        applyContainerNutrient(mealStack, containerStack.copyWithCount(1));
        mealStack.set(SDDataComponents.FOOD_CONTAINER_STACK.get(), new SDContainerStack(containerStack.copyWithCount(1)));
        return mealStack;
    }

    public static ItemStack getContainer(ItemStack stack){
        var bowl = Optional.ofNullable(stack.get(TFCComponents.BOWL)).map(ItemComponent::stack);
        if(bowl.isPresent()) return bowl.get().copy();
        var container = Optional.ofNullable(stack.get(SDDataComponents.FOOD_CONTAINER_STACK)).map(SDContainerStack::stack);
        return container.orElse(stack.getCraftingRemainingItem());
    }

    public static ItemStack getRemainder(ItemStack stack){
        var container = getContainer(stack);
        var foodProperty = container.getFoodProperties(null);
        return foodProperty != null? getRemainder(container): container;
    }
}
