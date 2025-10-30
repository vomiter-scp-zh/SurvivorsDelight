package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.data.recipe.cutting.FoodCuttingRecipes;
import com.vomiter.survivorsdelight.data.recipe.cutting.WoodCuttingRecipes;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.Arrays;
import java.util.function.Consumer;

public class SDRecipeProvider extends RecipeProvider {
    WoodCuttingRecipes woodCuttingRecipes = new WoodCuttingRecipes();
    SDCraftingRecipes craftingRecipes = new SDCraftingRecipes();
    SDCookingPotRecipes cookingPotRecipes = new SDCookingPotRecipes();
    FoodCuttingRecipes foodCuttingRecipes = new FoodCuttingRecipes();

    public SDRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> out) {
        TFCBlocks.WOODS.forEach((wood, blockTypes) -> {
            woodCuttingRecipes.stripForBark(wood, out);
            woodCuttingRecipes.salvageWoodFurniture(wood, out);
            craftingRecipes.cabinetForWood(wood, out);
            Arrays.stream(Metal.Default.values()).filter(Metal.Default::hasUtilities).forEach(
                    m -> woodCuttingRecipes.salvageHangingSign(wood, m, out)
            );
        });
        craftingRecipes.save(out);
        cookingPotRecipes.save(out);
        foodCuttingRecipes.cut2(out);
    }
}
