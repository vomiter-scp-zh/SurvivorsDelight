package com.vomiter.survivorsdelight.content.device.cooking_pot.bridge;

import net.minecraft.world.item.ItemStack;

public interface ICookingPotRecipeBridge {
    void sdtfc$setCachedBridge(TFCPotRecipeBridgeFD recipe);
    void sdtfc$setCachedDynamicFoodResult(ItemStack item);
    TFCPotRecipeBridgeFD sdtfc$getCachedBridge();
    ItemStack sdtfc$getCachedDynamicFoodResult();
}
