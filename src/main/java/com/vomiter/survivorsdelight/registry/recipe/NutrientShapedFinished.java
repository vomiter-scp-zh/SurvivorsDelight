package com.vomiter.survivorsdelight.registry.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 1.21.1 datagen-only builder.
 *
 * 不再 implements FinishedRecipe。
 * 這個類現在只負責收集 shaped recipe 的 datagen 資訊，
 * 最後用 RecipeOutput.accept(...) 輸出真正的 Recipe instance。
 */
public record NutrientShapedFinished(
        ResourceLocation id,
        Map<Character, Ingredient> key,
        List<String> pattern,
        ItemStack result,
        NutrientShapedRecipe recipe
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

        /*
         * 先保留這個欄位，避免外部 builder(...) 呼叫點需要大改。
         * 目前這個 class 不直接使用 serializer；
         * 真的序列化會走 NutrientShapedRecipe#getSerializer()。
         */
        @SuppressWarnings("unused")
        private final Supplier<? extends RecipeSerializer<?>> serializer;

        private final Map<Character, Ingredient> key = new LinkedHashMap<>();
        private final List<String> pattern = new ArrayList<>();
        private NutrientShapedRecipe recipe;

        private Builder(
                ResourceLocation id,
                ItemStack result,
                Supplier<? extends RecipeSerializer<?>> serializer
        ) {
            this.id = id;
            this.result = result;
            this.serializer = serializer;
        }

        public Builder key(char c, Ingredient ing) {
            this.key.put(c, ing);
            return this;
        }

        public Builder row(String r) {
            this.pattern.add(r);
            return this;
        }

        public Builder recipe(NutrientShapedRecipe recipe) {
            this.recipe = recipe;
            return this;
        }

        public NutrientShapedFinished build() {
            if (recipe == null) {
                throw new IllegalStateException("NutrientShapedRecipe 尚未設定。");
            }

            return new NutrientShapedFinished(
                    id,
                    Map.copyOf(key),
                    List.copyOf(pattern),
                    result.copy(),
                    recipe
            );
        }

        public void save(RecipeOutput output) {
            build().save(output);
        }
    }
}