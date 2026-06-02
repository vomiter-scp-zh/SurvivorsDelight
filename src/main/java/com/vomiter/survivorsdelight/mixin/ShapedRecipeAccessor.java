package com.vomiter.survivorsdelight.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {

    @Accessor("id")
    ResourceLocation sdtfc$getId();

    @Accessor("group")
    String sdtfc$getGroup();

    @Accessor("category")
    CraftingBookCategory sdtfc$getCategory();

    @Accessor("width")
    int sdtfc$getWidth();

    @Accessor("height")
    int sdtfc$getHeight();

    @Accessor("recipeItems")
    NonNullList<Ingredient> sdtfc$getRecipeItems();

    @Accessor("result")
    ItemStack sdtfc$getResult();

    @Accessor("showNotification")
    boolean sdtfc$getShowNotification();
}