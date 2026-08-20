package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.util.SDUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.List;

/**
 * 用來判斷「這個配方要不要被擋掉」的工具類
 */
public final class FDRecipeBlocker {

    private static final List<ResourceLocation> RLS = List.of(
            SDUtils.RLUtils.build("crafting/fd_straw2tfc_straw"),
            SDUtils.RLUtils.build("crafting/tfc_straw2fd_straw")
    );

    public static final String FD_NAMESPACE = FarmersDelight.MODID;

    private static final List<String> OTHER_BLOCKING_ID = List.of(
            "canvas",
            "melon_juice",
            "rice_roll_medley_block",
            "cutting/porkchop",
            "salvaging/saddle",
            "salvaging/leather_armor",
            "salvaging/leather_horse_armor",
            "paper_from_tree_bark"
    );

    private static final Ingredient OTHER_BLOCKING_TARGET = Ingredient.of(
            ModItems.HORSE_FEED.get(),
            ModItems.DOG_FOOD.get(),
            ModItems.SKILLET.get(),
            ModItems.STOVE.get(),
            ModItems.HONEY_GLAZED_HAM_BLOCK.get(),
            ModItems.ROAST_CHICKEN_BLOCK.get(),
            ModItems.STUFFED_PUMPKIN_BLOCK.get(),
            ModItems.SHEPHERDS_PIE_BLOCK.get()
    );

    private FDRecipeBlocker() {}

    public static boolean shouldBlock(ResourceLocation id, Recipe<?> recipe, RegistryAccess access) {
        if(RLS.contains(id)) return true;

        if (!FD_NAMESPACE.equals(id.getNamespace())) {
            return false;
        }

        if (!isTargetType(recipe)) {
            return false;
        }

        ItemStack result = recipe.getResultItem(access);
        if(isFood(result)) return true;
        if(OTHER_BLOCKING_ID.contains( id.getPath())) return true;
        return OTHER_BLOCKING_TARGET.test(result);
    }

    private static boolean isTargetType(Recipe<?> recipe) {
        RecipeType<?> type = recipe.getType();

        if (type == RecipeType.CRAFTING || recipe instanceof CraftingRecipe) {
            return true;
        }

        if (type == ModRecipeTypes.CUTTING.get() || recipe instanceof CuttingBoardRecipe) {
            return true;
        }

        if (type == ModRecipeTypes.COOKING.get() || recipe instanceof CookingPotRecipe) {
            return true;
        }

        return false;
    }

    private static boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().isEdible();
    }
}
