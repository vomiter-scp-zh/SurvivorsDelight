package com.vomiter.survivorsdelight.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 直寫 JSON 的 Firmalife Oven Recipe Builder（統一為和 SDTFCHeatingRecipeBuilder 相同的用法）
 * 會輸出：
 * {
 *   "type": "firmalife:oven",
 *   "ingredient": { ... },                 // 可用 Ingredient 或自訂 JSON（如 tfc:not_rotten）
 *   "result_item": { "stack": {...}, "modifiers": [...] }, // 可選（ItemStackProvider）
 *   "temperature": 200.0,
 *   "duration": 1200
 * }
 */
public final class SDFLOvenRecipeBuilder {
    private final String modid;
    private final String path;

    private Ingredient ingredient = Ingredient.EMPTY; // vanilla Ingredient
    private JsonObject ingredientRaw;                 // 自訂 ingredient（如 tfc:not_rotten）
    private JsonObject resultItem;                    // ItemStackProvider JSON（可選）
    private Float temperature;                        // required
    private Integer duration;                         // required（ticks）

    private SDFLOvenRecipeBuilder(String modid, String path) {
        this.modid = Objects.requireNonNull(modid);
        this.path = Objects.requireNonNull(path);
    }

    public static SDFLOvenRecipeBuilder oven(String modid, String path) {
        return new SDFLOvenRecipeBuilder(modid, path);
    }

    /* ---------------- ingredient ---------------- */

    /** 一般 Ingredient（不含 tfc:not_rotten） */
    public SDFLOvenRecipeBuilder ingredient(Ingredient ing) {
        this.ingredient = Objects.requireNonNull(ing);
        this.ingredientRaw = null;
        return this;
    }

    public SDFLOvenRecipeBuilder ingredient(Item item) {
        this.ingredient = Ingredient.of(Objects.requireNonNull(item));
        this.ingredientRaw = null;
        return this;
    }

    public SDFLOvenRecipeBuilder ingredient(TagKey<Item> tag) {
        this.ingredient = Ingredient.of(tag);
        this.ingredientRaw = null;
        return this;
    }

    /** 直接塞完整 ingredient JSON（例如包一層 tfc:not_rotten） */
    public SDFLOvenRecipeBuilder ingredientRaw(JsonObject json) {
        this.ingredientRaw = Objects.requireNonNull(json);
        this.ingredient = Ingredient.EMPTY;
        return this;
    }

    /** 便捷：以單一物品包一層 tfc:not_rotten */
    public SDFLOvenRecipeBuilder ingredientNotRotten(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) throw new IllegalArgumentException("Unregistered item: " + item);

        JsonObject inner = new JsonObject();
        inner.addProperty("item", id.toString());

        JsonObject notRotten = new JsonObject();
        notRotten.addProperty("type", "tfc:not_rotten");
        notRotten.add("ingredient", inner);

        return ingredientRaw(notRotten);
    }

    /* ---------------- result_item（ItemStackProvider） ---------------- */

    /** 最簡 Provider: stack.item + count(可省)；可另外加 modifiers（字串 ResourceLocation） */
    public SDFLOvenRecipeBuilder resultItemProvider(ResourceLocation itemId, int count, String... modifierIds) {
        JsonObject stack = new JsonObject();
        stack.addProperty("item", itemId.toString());
        if (count > 1) stack.addProperty("count", count);

        JsonObject provider = new JsonObject();
        provider.add("stack", stack);

        if (modifierIds != null && modifierIds.length > 0) {
            JsonArray mods = new JsonArray();
            for (String m : modifierIds) mods.add(m);
            provider.add("modifiers", mods);
        }
        this.resultItem = provider;
        return this;
    }

    public SDFLOvenRecipeBuilder resultItemProvider(Item item, int count, String... modifierIds) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) throw new IllegalArgumentException("Unregistered item: " + item);
        return resultItemProvider(id, count, modifierIds);
    }

    /** 直接塞完整的 ItemStackProvider JSON */
    public SDFLOvenRecipeBuilder resultItemRaw(JsonObject providerJson) {
        this.resultItem = Objects.requireNonNull(providerJson);
        return this;
    }

    /* ---------------- oven 專屬欄位 ---------------- */

    public SDFLOvenRecipeBuilder temperature(float celsius) {
        this.temperature = celsius;
        return this;
    }

    public SDFLOvenRecipeBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }

    /* ---------------- build / save ---------------- */

    public JsonObject build() {
        if (ingredient == Ingredient.EMPTY && ingredientRaw == null)
            throw new IllegalStateException("Missing ingredient");
        if (temperature == null)
            throw new IllegalStateException("Missing temperature");
        if (duration == null || duration <= 0)
            throw new IllegalStateException("Missing/invalid duration");
        // result_item 可選（允許只烤溫度/時間觸發其他邏輯的情境）

        JsonObject json = new JsonObject();
        json.addProperty("type", "firmalife:oven");

        if (ingredientRaw != null) {
            json.add("ingredient", ingredientRaw);
        } else {
            json.add("ingredient", ingredient.toJson());
        }

        if (resultItem != null) {
            json.add("result_item", resultItem);
        }

        json.addProperty("temperature", temperature);
        json.addProperty("duration", duration);

        return json;
    }

    /** 輸出到 data/<modid>/recipes/oven/<path>.json */
    public void save(BiConsumer<ResourceLocation, JsonObject> out) {
        ResourceLocation id = new ResourceLocation(modid, "oven/" + path);
        out.accept(id, build());
    }
}
