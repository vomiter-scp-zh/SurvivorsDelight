package com.vomiter.survivorsdelight.core.device.cooking_pot.bridge;

import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public interface ICookingPotRecipeBridge {
    void sdtfc$setCachedBridge(TFCPotRecipeBridgeFD recipe);
    void sdtfc$setCachedDynamicFoodResult(ItemStack item);
    TFCPotRecipeBridgeFD sdtfc$getCachedBridge();
    ItemStack sdtfc$getCachedDynamicFoodResult();
}
