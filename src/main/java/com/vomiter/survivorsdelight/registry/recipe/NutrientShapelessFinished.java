package com.vomiter.survivorsdelight.registry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record NutrientShapelessFinished(
        ResourceLocation id,
        List<Ingredient> ingredients,
        ItemStack result,
        NutrientShapelessRecipe recipe
) implements FinishedRecipe {

    @Override
    public void serializeRecipeData(@NotNull JsonObject json) {
        JsonArray ingredientsArray = new JsonArray();
        for (Ingredient ingredient : ingredients) {
            ingredientsArray.add(ingredient.toJson());
        }
        json.add("ingredients", ingredientsArray);

        JsonObject res = new JsonObject();
        res.addProperty(
                "item",
                Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())).toString()
        );
        if (result.getCount() > 1) {
            res.addProperty("count", result.getCount());
        }
        json.add("result", res);

        if(recipe.balanceFactor != 0.04f) json.addProperty("balance_factor", recipe.balanceFactor);
        if(recipe.presetHunger != -1) json.addProperty("hunger", recipe.presetHunger);
        if(recipe.presetDecay != 4.5f) json.addProperty("decay", recipe.presetDecay);
    }

    @Override
    public @NotNull RecipeSerializer<?> getType() {
        return recipe.getSerializer();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
        return null;
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
        return null;
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

        public Builder recipe(NutrientShapelessRecipe recipe) {
            this.recipe = recipe;
            return this;
        }

        public NutrientShapelessFinished build() {
            return new NutrientShapelessFinished(
                    id,
                    ingredients,
                    result,
                    recipe
            );
        }
    }
}