package com.vomiter.survivorsdelight.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SDFDCookingPotRecipeBuilder {

    private final CookingPotRecipeBuilder delegate;

    // 這個清單專門放「可選模組」的 ingredient，直接以 JSON 形式儲存
    private final List<JsonObject> ingredientsJson = new ArrayList<>();

    @Nullable private JsonElement fluidJson;
    private final List<ICondition> conditions = new ArrayList<>();

    private SDFDCookingPotRecipeBuilder(CookingPotRecipeBuilder delegate) {
        this.delegate = delegate;
    }

    /* ===== 建構入口，對齊 FD 的 API ===== */
    public static SDFDCookingPotRecipeBuilder cookingPotRecipe(ItemLike result, int count, int cookingTime, float exp) {
        return new SDFDCookingPotRecipeBuilder(
                CookingPotRecipeBuilder.cookingPotRecipe(result, count, cookingTime, exp)
        );
    }
    public static SDFDCookingPotRecipeBuilder cookingPotRecipe(ItemLike result, int count, int cookingTime, float exp, ItemLike container) {
        return new SDFDCookingPotRecipeBuilder(
                CookingPotRecipeBuilder.cookingPotRecipe(result, count, cookingTime, exp, container)
        );
    }

    /* ===== 條件 ===== */
    public SDFDCookingPotRecipeBuilder whenModLoaded(String modid) {
        this.conditions.add(new ModLoadedCondition(modid));
        return this;
    }
    public SDFDCookingPotRecipeBuilder whenAllModsLoaded(String... modids) {
        ICondition[] arr = Arrays.stream(modids).map(ModLoadedCondition::new).toArray(ICondition[]::new);
        this.conditions.add(new AndCondition(arr));
        return this;
    }
    public SDFDCookingPotRecipeBuilder whenAnyModLoaded(String... modids) {
        ICondition[] arr = Arrays.stream(modids).map(ModLoadedCondition::new).toArray(ICondition[]::new);
        this.conditions.add(new OrCondition(arr));
        return this;
    }
    public SDFDCookingPotRecipeBuilder unlessModLoaded(String modid) {
        this.conditions.add(new NotCondition(new ModLoadedCondition(modid)));
        return this;
    }
    public SDFDCookingPotRecipeBuilder when(ICondition condition) {
        this.conditions.add(condition);
        return this;
    }

    /* ===== 原 FD API 照轉（這些會立刻序列化為 Ingredient 物件；不要用於「可選模組」） ===== */
    public SDFDCookingPotRecipeBuilder addIngredient(ItemLike item) { delegate.addIngredient(item); return this; }
    public SDFDCookingPotRecipeBuilder addIngredient(ItemLike item, int qty) { delegate.addIngredient(item, qty); return this; }
    public SDFDCookingPotRecipeBuilder addIngredient(Ingredient ing) { delegate.addIngredient(ing); return this; }
    public SDFDCookingPotRecipeBuilder addIngredient(Ingredient ing, int qty) { delegate.addIngredient(ing, qty); return this; }
    public SDFDCookingPotRecipeBuilder addIngredient(TagKey<Item> tag) { delegate.addIngredient(tag); return this; }

    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(ItemLike item) {
        delegate.addIngredient(NotRottenIngredient.of(Ingredient.of(item)));
        return this;
    }
    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(ItemLike item, int qty) {
        delegate.addIngredient(NotRottenIngredient.of(Ingredient.of(item)), qty);
        return this;
    }
    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(Ingredient ing) {
        delegate.addIngredient(NotRottenIngredient.of(ing));
        return this;
    }
    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(Ingredient ing, int qty) {
        delegate.addIngredient(NotRottenIngredient.of(ing), qty);
        return this;
    }
    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(TagKey<Item> tag) {
        delegate.addIngredient(NotRottenIngredient.of(Ingredient.of(tag)));
        return this;
    }
    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(TagKey<Item> tag, int qty) {
        delegate.addIngredient(NotRottenIngredient.of(Ingredient.of(tag)), qty);
        return this;
    }

    /* ===== 以「純 JSON」加入 ingredient（專門給可選模組） ===== */

    /** 加入一個「item ingredient」： {"item":"<ns:id>"}  */
    public SDFDCookingPotRecipeBuilder addJsonItem(ResourceLocation itemId) {
        JsonObject o = new JsonObject();
        o.addProperty("item", itemId.toString());
        this.ingredientsJson.add(o);
        return this;
    }

    /** 同上，但重複 n 次（配方要 2～3 份時好用） */
    public SDFDCookingPotRecipeBuilder addJsonItem(ResourceLocation itemId, int count) {
        for (int i = 0; i < count; i++) addJsonItem(itemId);
        return this;
    }

    /** 加入一個「tag ingredient」： {"tag":"<ns:id>"} */
    public SDFDCookingPotRecipeBuilder addJsonTag(ResourceLocation tagId) {
        JsonObject o = new JsonObject();
        o.addProperty("tag", tagId.toString());
        this.ingredientsJson.add(o);
        return this;
    }

    /** 直接塞任意 JsonObject（若已有現成片段） */
    public SDFDCookingPotRecipeBuilder addJsonIngredient(JsonObject ing) {
        this.ingredientsJson.add(Objects.requireNonNull(ing));
        return this;
    }


    /* ===== Unlock recipe ===== */
    public SDFDCookingPotRecipeBuilder unlockedBy(String name, net.minecraft.advancements.CriterionTriggerInstance trigger) {
        delegate.unlockedBy(name, trigger);
        return this;
    }
    public SDFDCookingPotRecipeBuilder unlockedByItems(String name, ItemLike... items) {
        delegate.unlockedByItems(name, items);
        return this;
    }
    public SDFDCookingPotRecipeBuilder unlockedByAnyIngredient(ItemLike... items) {
        delegate.unlockedByAnyIngredient(items);
        return this;
    }
    public SDFDCookingPotRecipeBuilder setRecipeBookTab(@Nullable CookingPotRecipeBookTab tab) {
        delegate.setRecipeBookTab(tab);
        return this;
    }

    /* ===== fluid 欄位 ===== */
    public SDFDCookingPotRecipeBuilder fluid(JsonElement fluidObj) {
        this.fluidJson = Objects.requireNonNull(fluidObj);
        return this;
    }
    public SDFDCookingPotRecipeBuilder fluid(Fluid fluid, int amountMb) {
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        if (id == null) throw new IllegalArgumentException("Unregistered fluid: " + fluid);
        JsonObject obj = new JsonObject();
        obj.addProperty("ingredient", id.toString());
        obj.addProperty("amount", amountMb);
        this.fluidJson = obj;
        return this;
    }
    public SDFDCookingPotRecipeBuilder fluid(TagKey<Fluid> tag, int amountMb) {
        JsonObject obj = new JsonObject();
        JsonObject tagObj = new JsonObject();
        tagObj.addProperty("tag", tag.location().toString());
        obj.add("ingredient", tagObj);
        obj.addProperty("amount", amountMb);
        this.fluidJson = obj;
        return this;
    }

    /* ===== build ===== */
    public void build(Consumer<FinishedRecipe> out) { delegate.build(wrap(out)); }
    public void build(Consumer<FinishedRecipe> out, String save) { delegate.build(wrap(out), save); }
    public void build(Consumer<FinishedRecipe> out, ResourceLocation id) { delegate.build(wrap(out), id); }

    private Consumer<FinishedRecipe> wrap(Consumer<FinishedRecipe> out) {
        return base -> out.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                // 先讓 FD 寫原本的內容（含 ingredients/result/...）
                base.serializeRecipeData(json);

                // 把收集的 JSON ingredients 追加到原本的 "ingredients" 陣列
                if (!ingredientsJson.isEmpty()) {
                    JsonArray arr = json.has("ingredients") && json.get("ingredients").isJsonArray()
                            ? json.getAsJsonArray("ingredients")
                            : new JsonArray();
                    for (JsonObject o : ingredientsJson) arr.add(o);
                    json.add("ingredients", arr);
                }

                // fluid 塞到 root
                if (fluidJson != null) {
                    json.add("fluid", fluidJson);
                }

                // Forge 條件
                if (!conditions.isEmpty()) {
                    JsonArray arr = new JsonArray();
                    for (ICondition c : conditions) arr.add(CraftingHelper.serialize(c));
                    json.add("conditions", arr);
                }
            }

            @Override public @NotNull ResourceLocation getId() { return base.getId(); }
            @Override public @NotNull RecipeSerializer<?> getType() { return base.getType(); }
            @Override public @Nullable JsonObject serializeAdvancement() { return base.serializeAdvancement(); }
            @Override public @Nullable ResourceLocation getAdvancementId() { return base.getAdvancementId(); }
        });
    }
}
