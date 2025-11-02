package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.recipe.builder.SDFDCookingPotRecipeBuilder;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.RLUtils;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.fluids.Alcohol;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Consumer;

public class SDCookingPotRecipes {
    private ResourceLocation id(String path){
        return RLUtils.build(SurvivorsDelight.MODID, "cooking/" + path);
    }

    public void save(Consumer<FinishedRecipe> out){
        drinks(out);
        pie(out);
        soup(out);
        meal(out);
        feast(out);
    }

    public void feast(Consumer<FinishedRecipe> out){
        var oliveOil = TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.OLIVE_OIL).getSource();
        TagKey<Fluid> milks = TagKey.create(Registries.FLUID, RLUtils.build("tfc", "milks"));
        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.SHEPHERDS_PIE_BLOCK.get(), 1,
                        1200,
                        20,
                        Items.BOWL)
                .addIngredientNotRotten(SDTags.ItemTags.MEATS_FOR_SHEPHERDS_PIE)
                .addIngredientNotRotten(SDTags.ItemTags.MEATS_FOR_SHEPHERDS_PIE)
                .addIngredientNotRotten(SDTags.ItemTags.MEATS_FOR_SHEPHERDS_PIE)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_DOUGHS)
                .addIngredientNotRotten(TFCItems.SALADS.get(Nutrient.VEGETABLES).get())
                .fluid(milks, 100)
                .build(out, "feast/shepherds_pie");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.HONEY_GLAZED_HAM_BLOCK.get(), 1,
                        1200,
                        20,
                        Items.BOWL)
                .addIngredient(SDTags.ItemTags.TFC_SWEETENER)
                .addIngredientNotRotten(ModItems.SMOKED_HAM.get())
                .addIngredient(SDTags.ItemTags.FRUIT_FOR_CHEESECAKE)
                .addIngredient(SDTags.ItemTags.TFC_DOUGHS)
                .fluid(oliveOil, 100)
                .build(out, "feast/honey_glazed_ham");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.STUFFED_PUMPKIN_BLOCK.get(), 1,
                        1200,
                        20,
                        Items.CARVED_PUMPKIN)
                .addIngredientNotRotten(Items.BROWN_MUSHROOM)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_GRAINS)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_FRUITS)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_VEGETABLES)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .fluid(oliveOil, 100)
                .build(out, "feast/stuffed_pumpkin");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.ROAST_CHICKEN_BLOCK.get(), 1,
                        1200,
                        20,
                        Items.BOWL)
                .addIngredientNotRotten(SDUtils.getTFCFoodItem(Food.CHICKEN))
                .addIngredientNotRotten(SDTags.ItemTags.FRUIT_FOR_CHEESECAKE)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_VEGETABLES)
                .addIngredient(SDTags.ItemTags.TFC_GRAINS)
                .fluid(oliveOil, 100)
                .build(out, "feast/roasted_chicken");
    }

    public void meal(Consumer<FinishedRecipe> out){
        var oliveOil = TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.OLIVE_OIL).getSource();

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.FRIED_RICE.get(), 1,
                        600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_RICE).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_EGG).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .fluid(oliveOil, 100)
                .build(out, "meal/fried_rice");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.BACON_AND_EGGS.get(), 1,
                        300,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_EGG).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_EGG).get())
                .addIngredientNotRotten(ModItems.BACON.get())
                .addIngredientNotRotten(ModItems.BACON.get())
                .fluid(oliveOil, 100)
                .build(out, "meal/bacon_and_eggs");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.PASTA_WITH_MEATBALLS.get(), 1,
                        600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(ModItems.TOMATO_SAUCE.get())
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .addIngredientNotRotten(ModItems.BEEF_PATTY.get())
                .addIngredientNotRotten(ModItems.BEEF_PATTY.get())
                .fluid(oliveOil, 100)
                .build(out, "meal/pasta_with_meatballs");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.PASTA_WITH_MUTTON_CHOP.get(), 1,
                        600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(ModItems.TOMATO_SAUCE.get())
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_MUTTON).get())
                .fluid(oliveOil, 100)
                .build(out, "meal/pasta_with_mutton_chop");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.ROASTED_MUTTON_CHOPS.get(), 1,
                        1200,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.MUTTON).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.TOMATO).get())
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/grains"))
                .fluid(oliveOil, 100)
                .build(out, "meal/roasted_mutton_chops");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.VEGETABLE_NOODLES.get(), 1,
                        1200,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .fluid(oliveOil, 100)
                .build(out, "meal/vegetable_noodles");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.STEAK_AND_POTATOES.get(), 1,
                        600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_BEEF).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.BAKED_POTATO).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .fluid(oliveOil, 100)
                .build(out, "meal/steak_and_potatoes");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.RATATOUILLE.get(), 1,
                        1200,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(ModItems.TOMATO_SAUCE.get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .addIngredient(SDTags.ItemTags.create("tfc", "foods/vegetables"))
                .fluid(oliveOil, 100)
                .build(out, "meal/ratatouille");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.SQUID_INK_PASTA.get(), 1,
                        1200,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .addIngredient(Items.INK_SAC)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_CALAMARI).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.GARLIC).get())
                .fluid(oliveOil, 100)
                .build(out, "meal/squid_ink_pasta");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.GRILLED_SALMON.get(), 1,
                        600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_SALMON).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .addIngredientNotRotten(SDTags.ItemTags.FRUIT_FOR_CHEESECAKE) // 這行要有 Tag 版 NotRotten
                .fluid(oliveOil, 100)
                .build(out, "meal/grilled_salmon");

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.MUSHROOM_RICE.get(), 1,
                        1200,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.RICE_GRAIN).get())
                .addIngredientNotRotten(Items.RED_MUSHROOM)
                .addIngredientNotRotten(Items.BROWN_MUSHROOM)
                .addIngredientNotRotten(ModItems.BONE_BROTH.get())
                .build(out, "meal/mushroom_rice");
    }

    public void soup(Consumer<FinishedRecipe> out){
        var TFCSoups = SDTags.ItemTags.create("tfc", "soups");
        var vegetableAndFruitSoup = Ingredient.of(TFCItems.SOUPS.get(Nutrient.VEGETABLES).get(), TFCItems.SOUPS.get(Nutrient.FRUIT).get());
        TagKey<Fluid> MILKS_TAG = TagKey.create(Registries.FLUID, RLUtils.build("tfc", "milks"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.PUMPKIN_SOUP.get(), 1,
                    600,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(TFCItems.SOUPS.get(Nutrient.PROTEIN).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.PUMPKIN_CHUNKS).get())
                .fluid(MILKS_TAG, 100)
                .build(out, id("soup/pumpkin_soup"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.BAKED_COD_STEW.get(), 1,
                    600,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(SDTags.ItemTags.create("forge", "cooked_fishes/cod"))
                .addIngredientNotRotten(TFCSoups)
                .addIngredientNotRotten(ModItems.BONE_BROTH.get())
                .build(out, id("soup/baked_cod_stew"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.CHICKEN_SOUP.get(), 1,
                    600,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(SDTags.ItemTags.create("forge", "cooked_chicken"))
                .addIngredientNotRotten(TFCSoups)
                .addIngredientNotRotten(ModItems.BONE_BROTH.get())
                .build(out, id("soup/chicken_soup"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.FISH_STEW.get(), 1,
                    900,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(SDTags.ItemTags.FISHES_USABLE_IN_STEW)
                .addIngredientNotRotten(vegetableAndFruitSoup)
                .build(out, id("fish_stew"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.VEGETABLE_SOUP.get(), 1,
                    900,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(ModItems.BONE_BROTH.get())
                .addIngredientNotRotten(TFCItems.SOUPS.get(Nutrient.VEGETABLES).get())
                .build(out, id("soup/vegetable_soup"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.BEEF_STEW.get(), 1,
                    900,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(vegetableAndFruitSoup)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.COOKED_BEEF).get())
                .addIngredient(TFCItems.POWDERS.get(Powder.SALT).get())
                .addIngredient(TFCItems.POWDERS.get(Powder.SALT).get())
                .build(out, id("soup/beef_stew"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.NOODLE_SOUP.get(), 1,
                    600,
                    15,
                    Items.BOWL)
                .addIngredientNotRotten(TFCSoups)
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .addIngredientNotRotten(ModItems.BONE_BROTH.get())
                .build(out, id("soup/noodle_soup"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.BONE_BROTH.get(), 4,
                        3600,
                        15,
                        Items.BOWL)
                .addIngredientNotRotten(Items.BONE.asItem())
                .addIngredientNotRotten(Items.BONE.asItem())
                .addIngredient(TFCItems.POWDERS.get(Powder.SALT).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.GARLIC).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.GARLIC).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.ONION).get())
                .fluid(TFCTags.Fluids.ANY_FRESH_WATER, 400)
                .build(out, "soup/bone_broth");

    }

    public void pie(Consumer<FinishedRecipe> out){
        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                        ModItems.PIE_CRUST.get(), 1,
                        1200,
                        10)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_DOUGHS)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_DOUGHS)
                .addIngredientNotRotten(SDTags.ItemTags.PIE_CRUST_DAIRY)
                .fluid(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.OLIVE_OIL).getSource(), 200)
                .build(out, id("pie/pie_crust"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.APPLE_PIE.get(), 1,
                    1200,
                    10,
                    ModItems.PIE_CRUST.get())
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredient(SDTags.ItemTags.TFC_SWEETENER)
                .addIngredient(SDTags.ItemTags.TFC_SWEETENER)
                .addIngredient(SDTags.ItemTags.create("tfc", "makes_red_dye"))
                .build(out, id("pie/apple_pie"));
    }

    public void drinks(Consumer<FinishedRecipe> out){
        appleCider(out);
        melonJuice(out);
        hotCocoa(out);
    }

    public void hotCocoa(Consumer<FinishedRecipe> out){
        TagKey<Fluid> MILKS_TAG = TagKey.create(Registries.FLUID, RLUtils.build("tfc", "milks"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                ModItems.HOT_COCOA.get(), 1,
                1200,
                10,
                Items.GLASS_BOTTLE)
                .addIngredientNotRotten(SDTags.ItemTags.COCOA_POWDER)
                .addIngredientNotRotten(SDTags.ItemTags.COCOA_POWDER)
                .addIngredient(Ingredient.of(SDTags.ItemTags.create("tfc", "sweetener")))
                .fluid(MILKS_TAG, 200)
                .whenModLoaded("firmalife")
                .build(out, id("drink/hot_cocoa"));
    }

    public void appleCider(Consumer<FinishedRecipe> out){
        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                ModItems.APPLE_CIDER.get(), 1,
                1200,
                10,
                Items.GLASS_BOTTLE)
                .addIngredientNotRotten(Ingredient.of(SDTags.ItemTags.APPLE_FOR_CIDER))
                .addIngredientNotRotten(Ingredient.of(SDTags.ItemTags.APPLE_FOR_CIDER))
                .addIngredientNotRotten(Ingredient.of(SDTags.ItemTags.APPLE_FOR_CIDER))
                .addIngredientNotRotten(Ingredient.of(SDTags.ItemTags.APPLE_FOR_CIDER))
                .addIngredient(Ingredient.of(SDTags.ItemTags.create("tfc", "sweetener")))
                .fluid(TFCFluids.ALCOHOLS.get(Alcohol.CIDER).getSource(), 400)
                .build(out, id("drink/apple_cider"));
    }

    public void melonJuice(Consumer<FinishedRecipe> out){
        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                ModItems.MELON_JUICE.get(), 1,
                1200,
                10,
                Items.GLASS_BOTTLE)
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.MELON_SLICE).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.MELON_SLICE).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.MELON_SLICE).get())
                .addIngredientNotRotten(TFCItems.FOOD.get(Food.MELON_SLICE).get())
                .addIngredientNotRotten(TFCItems.POWDERS.get(Powder.SALT).get())
                .build(out, id("drink/melon_juice"));
    }
}
