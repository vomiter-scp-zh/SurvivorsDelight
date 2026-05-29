package com.vomiter.survivorsdelight.registry.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 1.21.1 datagen-only builder.
 *
 * 不再 implements FinishedRecipe。
 * 這個類現在只負責收集 shapeless recipe 的 datagen 資訊，
 * 最後用 RecipeOutput.accept(...) 輸出真正的 Recipe instance。
 */
public record NutrientShapelessFinished(
        ResourceLocation id,
        List<Ingredient> ingredients,
        ItemStack result,
        NutrientShapelessRecipe recipe
) {

    public void save(RecipeOutput output) {
        output.accept(id, recipe, null);
    }

    public static Builder builder(
            ResourceLocation id,
            ItemStack result,
            Supplier<? extends RecipeSerializer<?>> serializer
    ) {
        return new Builder(id, result, serializer);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Supplier<? extends RecipeSerializer<?>> serializer;

        private final List<Ingredient> ingredients = new ArrayList<>();
        private NutrientShapelessRecipe recipe;

        private Builder(
                ResourceLocation id,
                ItemStack result,
                Supplier<? extends RecipeSerializer<?>> serializer
        ) {
            this.id = id;
            this.result = result;
            this.serializer = serializer;
        }

        public Builder requires(Ingredient ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public Builder ingredient(Ingredient ingredient) {
            return requires(ingredient);
        }

        public Builder ingredients(List<Ingredient> ingredients) {
            this.ingredients.addAll(ingredients);
            return this;
        }

        public Builder recipe(NutrientShapelessRecipe recipe) {
            this.recipe = recipe;
            return this;
        }

        public NutrientShapelessFinished build() {
            if (recipe == null) {
                throw new IllegalStateException("NutrientShapelessRecipe 尚未設定。");
            }

            return new NutrientShapelessFinished(
                    id,
                    List.copyOf(ingredients),
                    result.copy(),
                    recipe
            );
        }

        public void save(RecipeOutput output) {
            build().save(output);
        }
    }
}