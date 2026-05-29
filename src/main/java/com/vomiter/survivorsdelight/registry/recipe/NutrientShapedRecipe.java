package com.vomiter.survivorsdelight.registry.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vomiter.survivorsdelight.registry.SDRecipeSerializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class NutrientShapedRecipe extends NutrientCraftingRecipe implements CraftingRecipe {
    public NutrientShapedRecipe(ShapedRecipe vanilla, float balanceFactor, int presetHunger, float presetDecay, boolean damageTool, Item container) {
        super(vanilla, balanceFactor, presetHunger, presetDecay, damageTool, container);
    }

    @Override public @NotNull RecipeSerializer<?> getSerializer() { return SDRecipeSerializers.NUTRITION_CRAFTING.get(); }

    // ============ Serializer ============
    public static class Serializer implements RecipeSerializer<NutrientShapedRecipe> {
        private static final MapCodec<NutrientShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ShapedRecipe.CODEC.fieldOf("vanilla").forGetter(r -> r.vanilla),
                Codec.FLOAT.fieldOf("balance_factor").orElse(0.04f).forGetter(r -> r.balanceFactor),
                Codec.INT.fieldOf("hunger").orElse(-1).forGetter(r -> r.presetHunger),
                Codec.FLOAT.fieldOf("decay").orElse(4.5f).forGetter(r -> r.presetDecay),
                Codec.BOOL.fieldOf("damage_tool").orElse(true).forGetter(r -> r.damageTool),
                BuiltInRegistries.ITEM.byNameCodec()
                        .fieldOf("container")
                        .orElse(Items.AIR)
                        .forGetter(r -> r.container)
        ).apply(instance, (vanilla, bf, hunger, decay, damageTool, container) -> new NutrientShapedRecipe((ShapedRecipe) vanilla, bf, hunger, decay, damageTool, container)));

        private static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC =
                ByteBufCodecs.registry(Registries.ITEM);

        @Override
        public @NotNull MapCodec<NutrientShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, NutrientShapedRecipe> streamCodec() {
            return StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
        }

        private static NutrientShapedRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            ShapedRecipe vanilla = (ShapedRecipe) ShapedRecipe.STREAM_CODEC.decode(buf);
            float bf = buf.readFloat();
            int presetHunger = buf.readInt();
            float presetDecay = buf.readFloat();
            boolean damageTool = buf.readBoolean();
            Item container = ITEM_STREAM_CODEC.decode(buf);
            return new NutrientShapedRecipe(vanilla, bf, presetHunger, presetDecay, damageTool, container);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull NutrientShapedRecipe recipe) {
            ShapedRecipe.STREAM_CODEC.encode(buf, recipe.vanilla);
            buf.writeFloat(recipe.balanceFactor);
            buf.writeInt(recipe.presetHunger);
            buf.writeFloat(recipe.presetDecay);
            buf.writeBoolean(recipe.damageTool);
            ITEM_STREAM_CODEC.encode(buf, recipe.container);
        }

    }
}
