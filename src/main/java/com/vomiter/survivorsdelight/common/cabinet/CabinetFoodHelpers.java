package com.vomiter.survivorsdelight.common.cabinet;

import com.vomiter.survivorsdelight.common.food.SDFoodTraits;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.world.item.ItemStack;

public class CabinetFoodHelpers {

    private static final FoodTrait CABINET_STORED = SDFoodTraits.CABINET_STORED;
    public static void setStored(ItemStack food){
        FoodCapability.applyTrait(food, CABINET_STORED);
    }
    public static void removeStored(ItemStack food){
        FoodCapability.removeTrait(food, CABINET_STORED);
    }

}
