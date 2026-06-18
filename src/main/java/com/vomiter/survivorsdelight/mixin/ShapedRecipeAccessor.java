package com.vomiter.survivorsdelight.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {

    @Accessor("group")
    String sdtfc$getGroup();

    @Accessor("category")
    CraftingBookCategory sdtfc$getCategory();

    @Accessor("pattern")
    ShapedRecipePattern sdtfc$getPattern();

    @Accessor("result")
    ItemStack sdtfc$getResult();

    @Accessor("showNotification")
    boolean sdtfc$getShowNotification();
}