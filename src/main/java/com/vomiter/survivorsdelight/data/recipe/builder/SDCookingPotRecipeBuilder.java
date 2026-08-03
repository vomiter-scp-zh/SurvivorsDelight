package com.vomiter.survivorsdelight.data.recipe.builder;

import com.vomiter.survivorsdelight.registry.recipe.SDCookingPotRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SDCookingPotRecipeBuilder {
    private String group = "";
    private final List<Ingredient> ingredients = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    @Nullable private ItemStack container = null;
    private int cookingTime = 200;
    private float experience = 0f;
    @Nullable private FluidIngredient fluid = null;
    private int fluidAmountMb = 0;
    private float balanceFactor = 0.04f;

    public static SDCookingPotRecipeBuilder cooking(ItemStack result, int time, float exp) {
        final SDCookingPotRecipeBuilder b = new SDCookingPotRecipeBuilder();
        b.result = result.copy();
        b.cookingTime = time;
        b.experience = exp;
        return b;
    }
    public static SDCookingPotRecipeBuilder cooking(Item resultItem, int count, int time, float exp) {
        return cooking(new ItemStack(resultItem, count), time, exp);
    }

    public SDCookingPotRecipeBuilder group(String g) { this.group = g == null ? "" : g; return this; }
    public SDCookingPotRecipeBuilder addIngredient(Ingredient ing) { this.ingredients.add(ing); return this; }
    public SDCookingPotRecipeBuilder addIngredientNotRotten(Ingredient ing){this.ingredients.add(AndIngredient.of(ing, NotRottenIngredient.INSTANCE)); return this;}
    public SDCookingPotRecipeBuilder container(ItemStack c) { this.container = (c == null || c.isEmpty()) ? null : c.copy(); return this; }
    public SDCookingPotRecipeBuilder container(Item item) { return container(new ItemStack(item)); }
    public SDCookingPotRecipeBuilder fluid(FluidIngredient f, int amountMb) { this.fluid = f; this.fluidAmountMb = amountMb; return this; }
    public SDCookingPotRecipeBuilder time(int t) { this.cookingTime = t; return this; }
    public SDCookingPotRecipeBuilder xp(float x) { this.experience = x; return this; }
    public SDCookingPotRecipeBuilder balanceFactor(float bf) {this.balanceFactor = bf; return this;}

    /** 不帶 advancement、不帶條件 */
    public void save(RecipeOutput out, ResourceLocation id) {
        save(out, id, null);
    }

    /** 帶 advancement，但不帶條件 */
    public void save(RecipeOutput out, ResourceLocation id, @Nullable AdvancementHolder adv) {
        save(out, id, adv, new ICondition[0]);
    }

    /** 完整版本：可帶 advancement 與條件（NeoForge） */
    public void save(RecipeOutput out, ResourceLocation id, @Nullable AdvancementHolder adv, ICondition... conditions) {
        // 基本檢查
        if (result.isEmpty()) throw new IllegalStateException("result 不可為空: " + id);
        if (ingredients.isEmpty()) throw new IllegalStateException("ingredients 至少需要 1 個: " + id);
        if (fluid != null && fluidAmountMb <= 0) throw new IllegalStateException("有 fluid 時 fluid_amount 必須 > 0: " + id);

        final SDCookingPotRecipe recipe = new SDCookingPotRecipe(
                group,
                NonNullList.copyOf(ingredients),
                result,
                container,
                cookingTime,
                experience,
                fluid,
                fluidAmountMb,
                balanceFactor
        );

        out.accept(id, recipe, adv, conditions);
    }
}
