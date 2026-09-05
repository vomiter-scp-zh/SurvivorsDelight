package com.vomiter.survivorsdelight.common.recipe;

import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.registry.SDRecipeSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NutrientShapelessRecipe extends NutrientCraftingRecipe implements CraftingRecipe {
    public NutrientShapelessRecipe(ShapelessRecipe vanilla, float balanceFactor, int presetHunger, float presetDecay, boolean damageTool, Item container) {
        super(vanilla, balanceFactor, presetHunger, presetDecay, damageTool, container);
    }

    @Override public @NotNull RecipeSerializer<?> getSerializer() { return SDRecipeSerializers.NUTRITION_CRAFTING_SHAPELESS.get(); }

    // ============ Serializer ============
    public static class Serializer implements RecipeSerializer<NutrientShapelessRecipe> {
        @Override
        public @NotNull NutrientShapelessRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            // 先讓 vanilla 解析 key/pattern/result
            ShapelessRecipe vanilla = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
            float bf = GsonHelper.getAsFloat(json, "balance_factor", 0.04f);
            int presetHunger = GsonHelper.getAsInt(json, "hunger", -1);
            float presetDecay = GsonHelper.getAsFloat(json, "decay", 4.5f);
            boolean damageTool = GsonHelper.getAsBoolean(json, "damage_tool", true);
            Item container = Items.AIR;
            if (json.has("container")) {
                ResourceLocation containerId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "container"));
                container = ForgeRegistries.ITEMS.getValue(containerId);
            }
            return new NutrientShapelessRecipe(vanilla, bf, presetHunger, presetDecay, damageTool, container);
        }

        @Override
        public NutrientShapelessRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            ShapelessRecipe vanilla = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buf);
            float bf = buf.readFloat();
            int presetHunger = buf.readInt();
            float presetDecay = buf.readFloat();
            boolean damageTool = buf.readBoolean();
            Item container = buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            return new NutrientShapelessRecipe(vanilla, bf, presetHunger, presetDecay, damageTool, container);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, NutrientShapelessRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buf, (ShapelessRecipe) recipe.vanilla);
            buf.writeFloat(recipe.balanceFactor);
            buf.writeInt(recipe.presetHunger);
            buf.writeFloat(recipe.presetDecay);
            buf.writeBoolean(recipe.damageTool);
            buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, Objects.requireNonNullElse(recipe.container, Items.AIR));
        }
    }
}
