package com.vomiter.survivorsdelight.core.device.cooking_pot.bridge;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Unique;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public interface ICookingPotTFCRecipeBridge {
    RecipeHolder<CookingPotRecipe> sdtfc$getBridgeCached();
    void sdtfc$setBridgeCached(RecipeHolder<CookingPotRecipe> r);
}
