package com.vomiter.survivorsdelight.data.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SDFDCookingPotRecipeBuilder {

    private final CookingPotRecipeBuilder delegate;
    @Nullable private JsonElement fluidJson;
    private final List<ICondition> conditions = new ArrayList<>();

    private SDFDCookingPotRecipeBuilder(CookingPotRecipeBuilder delegate) {
        this.delegate = delegate;
    }

    public static SDFDCookingPotRecipeBuilder cookingPotRecipe(ItemLike result, int count, int cookingTime, float exp, ItemLike container) {
        return new SDFDCookingPotRecipeBuilder(
                CookingPotRecipeBuilder.cookingPotRecipe(result, count, cookingTime, exp, container)
        );
    }

    public SDFDCookingPotRecipeBuilder whenModLoaded(String modid) {
        this.conditions.add(new ModLoadedCondition(modid));
        return this;
    }

    public SDFDCookingPotRecipeBuilder when(ICondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public SDFDCookingPotRecipeBuilder addIngredient(Ingredient ing) { delegate.addIngredient(ing); return this; }

    public SDFDCookingPotRecipeBuilder addIngredientNotRotten(Ingredient ing) {
        delegate.addIngredient(NotRottenIngredient.of(ing));
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
    public void build(Consumer<FinishedRecipe> out) { delegate.save(wrap(out)); }
    public void build(Consumer<FinishedRecipe> out, String save) { delegate.save(wrap(out), save); }
    public void build(Consumer<FinishedRecipe> out, ResourceLocation id) { delegate.save(wrap(out), id); }

    private Consumer<FinishedRecipe> wrap(Consumer<FinishedRecipe> out) {
        return base -> out.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {

                base.serializeRecipeData(json);

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
