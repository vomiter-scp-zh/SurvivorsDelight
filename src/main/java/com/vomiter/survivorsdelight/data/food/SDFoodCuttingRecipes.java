package com.vomiter.survivorsdelight.data.food;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.recipe.builder.SDCuttingRecipeBuilder;
import com.vomiter.survivorsdelight.data.recipe.builder.SDJsonAdapters;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

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
            ItemStackProvider isp = ItemStackProvider.of(
                    cutSpec.item().get().getDefaultInstance().copyWithCount(cutSpec.slices()),
                    CopyFoodModifier.INSTANCE
            );

            SDCuttingRecipeBuilder.cutting()
                    .notRotten(ingredient)
                    .tool(knife)
                    .result(SDJsonAdapters.writeISP(isp))
            .build(out, SDUtils.RLUtils.build(SurvivorsDelight.MODID, "cutting/food/" + ForgeRegistries.ITEMS.getKey(cutSpec.item().get()).getPath()));
        });

        ItemStackProvider porkISP = ItemStackProvider.of(new ItemStack(SDUtils.getTFCFoodItem(Food.PORK), 2), CopyFoodModifier.INSTANCE);
        ItemStackProvider cookedPorkISP = ItemStackProvider.of(new ItemStack(SDUtils.getTFCFoodItem(Food.COOKED_PORK), 2), CopyFoodModifier.INSTANCE);
        ItemStackProvider rawPastaISP = ItemStackProvider.of(new ItemStack(ModItems.RAW_PASTA.get(), 2), CopyFoodModifier.INSTANCE);

        SDCuttingRecipeBuilder.cutting()
                    .notRotten(Ingredient.of(ModItems.HAM.get()))
                    .tool(knife)
                    .result(SDJsonAdapters.writeISP(porkISP))
                    .result(SDJsonAdapters.stackToJson(Items.BONE.getDefaultInstance()))
            .build(out, SDUtils.RLUtils.build(SurvivorsDelight.MODID, "cutting/food/ham"));
        SDCuttingRecipeBuilder.cutting()
                .notRotten(Ingredient.of(ModItems.SMOKED_HAM.get()))
                .tool(knife)
                .result(SDJsonAdapters.writeISP(cookedPorkISP))
                .result(SDJsonAdapters.stackToJson(Items.BONE.getDefaultInstance()))
                .build(out, SDUtils.RLUtils.build(SurvivorsDelight.MODID, "cutting/food/smoked_ham"));
        SDCuttingRecipeBuilder.cutting()
                .notRotten(Ingredient.of(SDTags.ItemTags.TFC_DOUGHS))
                .tool(knife)
                .result(SDJsonAdapters.writeISP(rawPastaISP))
                .build(out, SDUtils.RLUtils.build(SurvivorsDelight.MODID, "cutting/food/raw_pasta"));
    }
}
