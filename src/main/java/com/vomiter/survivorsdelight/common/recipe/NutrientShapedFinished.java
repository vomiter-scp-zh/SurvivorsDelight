package com.vomiter.survivorsdelight.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record NutrientShapedFinished(
        ResourceLocation id,
        Map<Character, Ingredient> key,
        List<String> pattern,
        ItemStack result,
        NutrientShapedRecipe recipe
) implements FinishedRecipe {

    @Override
    public void serializeRecipeData(@NotNull JsonObject json) {
        // key
        JsonObject keyObj = new JsonObject();
        for (Map.Entry<Character, Ingredient> e : key.entrySet()) {
            keyObj.add(String.valueOf(e.getKey()), e.getValue().toJson());
        }
        json.add("key", keyObj);

        // pattern
        JsonArray pat = new JsonArray();
        for (String p : pattern) pat.add(p);
        json.add("pattern", pat);

        recipe.commonSerialization(json, result);
    }

    @Override public @NotNull RecipeSerializer<?> getType() { return recipe.getSerializer(); }
    @Override public @NotNull ResourceLocation getId() { return id; }
    @Override public @Nullable JsonObject serializeAdvancement() { return null; }
    @Override public @Nullable ResourceLocation getAdvancementId() { return null; }

    public static Builder builder(ResourceLocation id, ItemStack result, Supplier<? extends RecipeSerializer<?>> serializer) {
        return new Builder(id, result, serializer);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Supplier<? extends RecipeSerializer<?>> serializer;
        private final Map<Character, Ingredient> key = new LinkedHashMap<>();
        private final java.util.List<String> pattern = new java.util.ArrayList<>();
        private NutrientShapedRecipe recipe;

        private Builder(ResourceLocation id, ItemStack result, Supplier<? extends RecipeSerializer<?>> serializer) {
            this.id = id;
            this.result = result;
            this.serializer = serializer;
        }
        public Builder key(char c, Ingredient ing){ this.key.put(c, ing); return this; }
        public Builder row(String r){ this.pattern.add(r); return this; }
        public Builder recipe(NutrientShapedRecipe recipe) { this.recipe = recipe; return this;}

        public NutrientShapedFinished build() {
            return new NutrientShapedFinished(id, key, pattern, result, recipe);
        }
    }
}
