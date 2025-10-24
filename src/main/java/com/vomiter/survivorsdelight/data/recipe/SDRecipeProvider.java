package com.vomiter.survivorsdelight.data.recipe;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

public class SDRecipeProvider extends RecipeProvider {
    WoodCuttingRecipes woodCuttingRecipes = new WoodCuttingRecipes();
    CabinetRecipes cabinetRecipes = new CabinetRecipes();
    public SDRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> out) {
        TFCBlocks.WOODS.forEach((wood, blockTypes) -> {
            woodCuttingRecipes.stripForBark(wood, out);
            woodCuttingRecipes.salvageWoodFurniture(wood, out);
            cabinetRecipes.cabinetForWood(wood, out);
            Arrays.stream(Metal.Default.values()).filter(Metal.Default::hasUtilities).forEach(
                    m -> woodCuttingRecipes.salvageHangingSign(wood, m, out)
            );
        });
    }
}
