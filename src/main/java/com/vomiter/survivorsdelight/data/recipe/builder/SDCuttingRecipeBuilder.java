package com.vomiter.survivorsdelight.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class SDCuttingRecipeBuilder {
    private final List<JsonElement> ingredients = new ArrayList<>();
    private JsonElement toolJson;
    private final List<JsonElement> results = new ArrayList<>();
    private String group;
    private String sound;

    public static SDCuttingRecipeBuilder cutting() { return new SDCuttingRecipeBuilder(); }

    public SDCuttingRecipeBuilder ingredient(Ingredient ing) { ingredients.add(ing.toJson()); return this; }
    public SDCuttingRecipeBuilder notRotten(Ingredient ing) { ingredients.add(NotRottenIngredient.of(ing).toJson()); return this; }
    public SDCuttingRecipeBuilder tool(Ingredient tool) { this.toolJson = tool.toJson(); return this; }
    public SDCuttingRecipeBuilder result(JsonElement resultJson) { results.add(resultJson); return this; }

    public SDCuttingRecipeBuilder result(Item item, int count) {
        results.add(SDJsonAdapters.stackToJson(new ItemStack(item, count)));
        return this;
    }

    public SDCuttingRecipeBuilder group(String g) { this.group = g; return this; }
    public SDCuttingRecipeBuilder sound(String s) { this.sound = s; return this; }

    public void build(Consumer<FinishedRecipe> out, ResourceLocation id) {
        Objects.requireNonNull(toolJson, "tool is required");
        if (ingredients.isEmpty()) throw new IllegalStateException("at least one ingredient");
        if (results.isEmpty()) throw new IllegalStateException("at least one result");
        out.accept(new Result(id, this));
    }

    private static final class Result implements FinishedRecipe {
        private final ResourceLocation id; private final SDCuttingRecipeBuilder b;
        Result(ResourceLocation id, SDCuttingRecipeBuilder b) { this.id = id; this.b = b; }

        @Override public void serializeRecipeData(JsonObject json) {
            JsonArray ingr = new JsonArray(); b.ingredients.forEach(ingr::add); json.add("ingredients", ingr);
            json.add("tool", b.toolJson);
            JsonArray res = new JsonArray(); b.results.forEach(res::add); json.add("result", res);
            if (b.group != null) json.addProperty("group", b.group);
            if (b.sound != null) json.addProperty("sound", b.sound);
        }
        @Override public ResourceLocation getId() { return id; }
        @Override public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() { return ModRecipeSerializers.CUTTING.get(); }
        @Override public JsonObject serializeAdvancement() { return null; }
        @Override public ResourceLocation getAdvancementId() { return null; }
    }
}
