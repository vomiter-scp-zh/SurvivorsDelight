package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;


public class SDUtils {

    public static Item getTFCFoodItem(Food food){
        return TFCItems.FOOD.get(food).get();
    }
    public static float getExtraNutrientAfterCooking(ItemStack rawStack, Nutrient nutrient, Level level) {
        if (level == null || rawStack.isEmpty()) {
            return 0f;
        }

        float rawValue = getNutrient(rawStack, nutrient);

        RecipeManager rm = level.getRecipeManager();
        List<HeatingRecipe> allHeating = rm.getAllRecipesFor(TFCRecipeTypes.HEATING.get());

        HeatingRecipe matched = null;
        for (HeatingRecipe recipe : allHeating) {
            Ingredient ing = recipe.getIngredient();
            if (ing.test(rawStack)) {
                matched = recipe;
                break;
            }
        }

        if (matched == null) {
            return 0f;
        }

        ItemStackInventory inv = new ItemStackInventory(rawStack);
        ItemStack cookedStack = matched.assemble(inv, level.registryAccess());
        if (cookedStack.isEmpty()) {
            return 0f;
        }

        float cookedValue = getNutrient(cookedStack, nutrient);

        float diff = cookedValue - rawValue;
        return Math.max(diff, 0f);
    }

    // 小工具：從一個 stack 拿出指定營養素
    private static float getNutrient(ItemStack stack, Nutrient nutrient) {
        IFood food = FoodCapability.get(stack);
        if(food == null) return 0f;
        return food.getData().nutrient(nutrient);
    }

    public static class TagUtils{
        public static boolean fluidIngredientMatchesTag(FluidStackIngredient ingredient, TagKey<Fluid> fluidTag) {
            var tags = ForgeRegistries.FLUIDS.tags();
            if (tags == null || ingredient==null) return false;

            final int amount = Math.max(1, ingredient.amount());
            for (Fluid f : tags.getTag(fluidTag)) {
                if (ingredient.test(new FluidStack(f, amount))) {
                    return true;
                }
            }
            return false;
        }

        public static TagKey<Item> itemTag(String namespace, String path) {
            return TagKey.create(Registries.ITEM, RLUtils.build(namespace, path));
        }

        public static TagKey<Block> blockTag(String namespace, String path) {
            return TagKey.create(Registries.BLOCK, RLUtils.build(namespace, path));
        }

        public static TagKey<Fluid> fluidTag(String namespace, String path) {
            return TagKey.create(Registries.FLUID, RLUtils.build(namespace, path));
        }



    }

    public static boolean fluidIngredientMatchesTag(FluidStackIngredient ingredient, TagKey<Fluid> fluidTag) {
        var tags = ForgeRegistries.FLUIDS.tags();
        if (tags == null) return false;

        final int amount = Math.max(1, ingredient.amount());
        for (Fluid f : tags.getTag(fluidTag)) {
            if (ingredient.test(new FluidStack(f, amount))) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    public static class RLUtils {
        public static ResourceLocation build(String namespace, String path){
            return new ResourceLocation(namespace, path);
        }

        public static ResourceLocation build(String path){
            return new ResourceLocation(SurvivorsDelight.MODID, path);
        }
    }
}
