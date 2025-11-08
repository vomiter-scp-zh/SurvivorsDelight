package com.vomiter.survivorsdelight.core.registry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public record ShapedLikeFinished(
        ResourceLocation id,
        Map<Character, Ingredient> key,   // 建議用 LinkedHashMap 保存順序
        List<String> pattern,
        ItemStack result,
        @Nullable String group,
        float balanceFactor,
        int presetHunger,
        float presetDecay,
        Supplier<? extends RecipeSerializer<?>> serializer
) implements FinishedRecipe {

    @Override
    public void serializeRecipeData(JsonObject json) {
        if (group != null && !group.isEmpty()) json.addProperty("group", group);

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

        // result
        JsonObject res = new JsonObject();
        res.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())).toString());
        if (result.getCount() > 1) res.addProperty("count", result.getCount());
        json.add("result", res);

        if(balanceFactor != 0.04f) json.addProperty("balance_factor", balanceFactor);
        if(presetHunger != -1) json.addProperty("hunger", presetHunger);
        if(presetDecay != 4.5f) json.addProperty("decay", presetDecay);
    }

    @Override public RecipeSerializer<?> getType() { return serializer.get(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public @Nullable JsonObject serializeAdvancement() { return null; }
    @Override public @Nullable ResourceLocation getAdvancementId() { return null; }

    public static Builder builder(ResourceLocation id, ItemStack result, Supplier<? extends RecipeSerializer<?>> serializer) {
        return new Builder(id, result, serializer);
    }

    // 小幫手：方便在 builder 端逐步填
    public static final class Builder {
        private final ResourceLocation id;
        private final ItemStack result;
        private final Supplier<? extends RecipeSerializer<?>> serializer;
        private final Map<Character, Ingredient> key = new LinkedHashMap<>();
        private final java.util.List<String> pattern = new java.util.ArrayList<>();
        private String group = null;
        private float balanceFactor = 0.04f;
        private int presetHunger = -1;
        private float presetDecay = 4.5f;

        private Builder(ResourceLocation id, ItemStack result, Supplier<? extends RecipeSerializer<?>> serializer) {
            this.id = id;
            this.result = result;
            this.serializer = serializer;
        }
        public Builder group(String g){ this.group = g; return this; }
        public Builder balance(float f){ this.balanceFactor = f; return this; }
        public Builder key(char c, Ingredient ing){ this.key.put(c, ing); return this; }
        public Builder row(String r){ this.pattern.add(r); return this; }
        public Builder presetHunger(int i){ this.presetHunger = i; return this; }
        public Builder presetDecay(float f){ this.presetDecay = f; return this; }

        public ShapedLikeFinished build() {
            return new ShapedLikeFinished(id, key, pattern, result, group, balanceFactor, presetHunger, presetDecay, serializer);
        }
    }
}
