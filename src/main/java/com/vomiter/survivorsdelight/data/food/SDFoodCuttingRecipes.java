package com.vomiter.survivorsdelight.data.food;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SDFoodCuttingRecipes {
    public static List<Food> SALMON_LIKE = List.of(Food.SALMON, Food.LAKE_TROUT, Food.RAINBOW_TROUT);
    public static List<Food> COD_LIKE = List.of(Food.COD, Food.CRAPPIE, Food.LARGEMOUTH_BASS, Food.SMALLMOUTH_BASS);
    public static Map<Food, List<Food>> FISH_TO_CUT = Map.of(Food.SALMON, SALMON_LIKE, Food.COD, COD_LIKE);

    public void cut2(Consumer<FinishedRecipe> out){
        var knife = Ingredient.of(ForgeTags.TOOLS_KNIVES);
        SDBasicFoodData.cutSpecs.forEach(cutSpec -> {
            Ingredient ingredient = Ingredient.of(TFCItems.FOOD.get(cutSpec.from()).get());
            var fishType = FISH_TO_CUT.get(cutSpec.from());
            if(fishType != null){
                if(cutSpec.from().name().contains("COOKED")) fishType =  fishType.stream().map(f -> Food.valueOf("COOKED " + f.name())).toList();
                ingredient = Ingredient.of(fishType.stream().map(f -> TFCItems.FOOD.get(f).get().getDefaultInstance()));
            }

            CuttingBoardRecipeBuilder.cuttingRecipe(
                    NotRottenIngredient.of(ingredient),
                    knife,
                    cutSpec.item().get(),
                    cutSpec.slices()
            ).build(out, RLUtils.build(SurvivorsDelight.MODID, "cutting/food/" + ForgeRegistries.ITEMS.getKey(cutSpec.item().get()).getPath()));
        });

        CuttingBoardRecipeBuilder.cuttingRecipe(
                NotRottenIngredient.of(Ingredient.of(ModItems.HAM.get())),
                knife,
                TFCItems.FOOD.get(Food.PORK).get(),
                2
        ).addResult(Items.BONE).build(out, RLUtils.build(SurvivorsDelight.MODID, "cutting/food/ham"));

        CuttingBoardRecipeBuilder.cuttingRecipe(
                NotRottenIngredient.of(Ingredient.of(ModItems.SMOKED_HAM.get())),
                knife,
                TFCItems.FOOD.get(Food.COOKED_PORK).get(),
                2
        ).addResult(Items.BONE).build(out, RLUtils.build(SurvivorsDelight.MODID, "cutting/food/smoked_ham"));

        CuttingBoardRecipeBuilder.cuttingRecipe(
                NotRottenIngredient.of(Ingredient.of(SDTags.ItemTags.TFC_DOUGHS)),
                knife,
                ModItems.RAW_PASTA.get(),
                1
        ).build(out, RLUtils.build(SurvivorsDelight.MODID, "cutting/food/raw_pasta"));

    }
}
