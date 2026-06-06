package com.vomiter.survivorsdelight.registry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.registry.SDRecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public record MedleyCraftingFinished(
        ResourceLocation id,
        Map<Character, Ingredient> key,
        List<String> pattern,
        ItemStack result,
        Item container
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
        JsonObject res = new JsonObject();
        res.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())).toString());
        if (result.getCount() > 1) res.addProperty("count", result.getCount());
        json.add("result", res);

        if(container != null && container != Items.AIR){
            json.addProperty("container", Objects.requireNonNull(
                    ForgeRegistries.ITEMS.getKey(container)).toString());
        }

    }

    @Override public @NotNull RecipeSerializer<?> getType() { return SDRecipeSerializers.MEDLEY_CRAFTING.get(); }
    @Override public @NotNull ResourceLocation getId() { return id; }
    @Override public @Nullable JsonObject serializeAdvancement() { return null; }
    @Override public @Nullable ResourceLocation getAdvancementId() { return null; }

    public static Builder builder(ResourceLocation id, ItemStack result) {
        return new Builder(id, result);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Map<Character, Ingredient> key = new LinkedHashMap<>();
        private final List<String> pattern = new java.util.ArrayList<>();
        private Item container;

        private Builder(ResourceLocation id, ItemStack result) {
            this.id = id;
            this.result = result;
        }

        public Builder key(char c, Ingredient ing){ this.key.put(c, ing); return this; }
        public Builder row(String r){ this.pattern.add(r); return this; }
        public Builder container(Item container) { this.container = container; return this;}

        public MedleyCraftingFinished build() {
            return new MedleyCraftingFinished(id, key, pattern, result, container);
        }
    }
}
