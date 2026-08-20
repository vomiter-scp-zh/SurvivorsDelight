package com.vomiter.survivorsdelight.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.BiConsumer;

public final class SDTFCHeatingRecipeBuilder {
    private final String modid;
    private final String path;

    private Ingredient ingredient = Ingredient.EMPTY; // 用於一般 vanilla Ingredient
    private JsonObject ingredientRaw;                 // 如 tfc:not_rotten
    private JsonObject resultItem;                    // ItemStackProvider JSON
    private ResourceLocation resultFluidId;           // optional
    private int resultFluidAmount;
    private Float temperature;                        // required
    private boolean useDurability = false;
    private float chance = 1f;

    private SDTFCHeatingRecipeBuilder(String modid, String path) {
        this.modid = Objects.requireNonNull(modid);
        this.path = Objects.requireNonNull(path);
    }

    public static SDTFCHeatingRecipeBuilder heating(String modid, String path) {
        return new SDTFCHeatingRecipeBuilder(modid, path);
    }

    public SDTFCHeatingRecipeBuilder ingredient(Ingredient ing) {
        this.ingredient = Objects.requireNonNull(ing);
        this.ingredientRaw = null;
        return this;
    }

    public SDTFCHeatingRecipeBuilder ingredient(Item item) {
        this.ingredient = Ingredient.of(Objects.requireNonNull(item));
        this.ingredientRaw = null;
        return this;
    }

    public SDTFCHeatingRecipeBuilder ingredient(TagKey<Item> tag) {
        this.ingredient = Ingredient.of(tag);
        this.ingredientRaw = null;
        return this;
    }

    public SDTFCHeatingRecipeBuilder ingredientRaw(JsonObject json) {
        this.ingredientRaw = Objects.requireNonNull(json);
        this.ingredient = Ingredient.EMPTY;
        return this;
    }

    public SDTFCHeatingRecipeBuilder ingredientNotRotten(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) throw new IllegalArgumentException("Unregistered item: " + item);
        JsonObject inner = new JsonObject();
        inner.addProperty("item", id.toString());
        JsonObject notRotten = new JsonObject();
        notRotten.addProperty("type", "tfc:not_rotten");
        notRotten.add("ingredient", inner);
        return ingredientRaw(notRotten);
    }

    /* -------------- result_item（ItemStackProvider） -------------- */

    /** 最簡 Provider: stack.item + count(可省)；可另外加上 modifiers */
    public SDTFCHeatingRecipeBuilder resultItemProvider(ResourceLocation itemId, int count, String... modifiersIds) {
        JsonObject stack = new JsonObject();
        stack.addProperty("item", itemId.toString());
        if (count > 1) stack.addProperty("count", count);

        JsonObject provider = new JsonObject();
        provider.add("stack", stack);

        if (modifiersIds != null && modifiersIds.length > 0) {
            JsonArray mods = new JsonArray();
            for (String m : modifiersIds) mods.add(m);
            provider.add("modifiers", mods);
        }
        this.resultItem = provider;
        return this;
    }

    public SDTFCHeatingRecipeBuilder resultItemProvider(Item item, int count, String... modifiersIds) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) throw new IllegalArgumentException("Unregistered item: " + item);
        return resultItemProvider(id, count, modifiersIds);
    }

    public SDTFCHeatingRecipeBuilder resultFluid(ResourceLocation fluidId, int amountMb) {
        if (amountMb <= 0) throw new IllegalArgumentException("amount must be > 0");
        this.resultFluidId = Objects.requireNonNull(fluidId);
        this.resultFluidAmount = amountMb;
        return this;
    }

    public SDTFCHeatingRecipeBuilder resultFluid(FluidStack stack) {
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        if (id == null) throw new IllegalArgumentException("Unregistered fluid: " + stack.getFluid());
        return resultFluid(id, stack.getAmount());
    }


    public SDTFCHeatingRecipeBuilder temperature(float celsius) {
        this.temperature = celsius;
        return this;
    }

    public JsonObject build() {
        if (ingredient == Ingredient.EMPTY && ingredientRaw == null)
            throw new IllegalStateException("Missing ingredient");
        if (temperature == null)
            throw new IllegalStateException("Missing temperature");
        if (resultItem == null && resultFluidId == null)
            throw new IllegalStateException("Need at least one of result_item or result_fluid");

        JsonObject json = new JsonObject();
        json.addProperty("type", "tfc:heating");

        if (ingredientRaw != null) {
            json.add("ingredient", ingredientRaw);
        } else {
            json.add("ingredient", ingredient.toJson());
        }

        if (resultItem != null) {
            json.add("result_item", resultItem);
        }
        if (resultFluidId != null) {
            JsonObject fluid = new JsonObject();
            fluid.addProperty("fluid", resultFluidId.toString());
            fluid.addProperty("amount", resultFluidAmount);
            json.add("result_fluid", fluid);
        }

        json.addProperty("temperature", temperature);

        if (useDurability) json.addProperty("use_durability", true);
        if (chance != 1f) json.addProperty("chance", chance);

        return json;
    }

    /** data/<modid>/recipes/heating/<path>.json */
    public void save(BiConsumer<ResourceLocation, JsonObject> out) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modid, "heating/" + path);
        out.accept(id, build());
    }
}
