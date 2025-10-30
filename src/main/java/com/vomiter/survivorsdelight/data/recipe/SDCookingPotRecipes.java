package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.recipe.builder.SDFDCookingPotRecipeBuilder;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.RLUtils;
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
    }

    public void soup(Consumer<FinishedRecipe> out){
        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.NOODLE_SOUP.get(), 1,
                    2400,
                    15)
                .addIngredientNotRotten(SDTags.ItemTags.create("tfc", "soups"))
                .addIngredientNotRotten(ModItems.RAW_PASTA.get())
                .build(out, id("noodle_soup"));
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
                .build(out, id("pie_crust"));

        SDFDCookingPotRecipeBuilder.cookingPotRecipe(
                    ModItems.APPLE_PIE.get(), 1,
                    2400,
                    10,
                    ModItems.PIE_CRUST.get())
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredientNotRotten(SDTags.ItemTags.APPLE_FOR_CIDER)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_SWEETENER)
                .addIngredientNotRotten(SDTags.ItemTags.TFC_SWEETENER)
                .addIngredient(SDTags.ItemTags.create("tfc", "make_red_dye"))
                .build(out, id("apple_pie"));
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
                .build(out, id("hot_cocoa"));
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
                .build(out, id("apple_cider"));
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
                .build(out, id("melon_juice"));
    }
}
