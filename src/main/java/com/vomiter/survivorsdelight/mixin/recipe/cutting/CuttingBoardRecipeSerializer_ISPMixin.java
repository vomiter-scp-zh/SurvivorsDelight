package com.vomiter.survivorsdelight.mixin.recipe.cutting;

import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

@Mixin(value = CuttingBoardRecipe.Serializer.class, remap = false)
public abstract class CuttingBoardRecipeSerializer_ISPMixin {

}
