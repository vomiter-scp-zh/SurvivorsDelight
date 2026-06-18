package com.vomiter.survivorsdelight.registry.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vomiter.survivorsdelight.common.food.FoodContainerExpansion;
import com.vomiter.survivorsdelight.mixin.ShapedRecipeAccessor;
import com.vomiter.survivorsdelight.registry.SDDataComponents;
import com.vomiter.survivorsdelight.registry.SDRecipeSerializers;
import com.vomiter.survivorsdelight.registry.component.MedleyContent;
import com.vomiter.survivorsdelight.registry.component.SDContainerStack;
import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import com.vomiter.survivorsdelight.util.SimpleCraftingContainer;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedleyCraftingRecipe extends ShapedRecipe {
    final Item container;

    public MedleyCraftingRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, Item container) {
        super(group, category, pattern, result);
        this.container = container;
    }

    @Override public @NotNull RecipeSerializer<?> getSerializer() { return SDRecipeSerializers.MEDLEY_CRAFTING.get(); }


    public Item getContainer(){
        return container;
    }

    public static void applyFoodStackForMedley(ItemStack medley, List<ItemStack> foodStacks){
        medley.set(SDDataComponents.MEDLEY_CONTENT.get(), new MedleyContent(foodStacks));
    }

    public static List<ItemStack> getFoodStackFromMedley(ItemStack medley){
        List<ItemStack> stacks = new ArrayList<>();
        var content = medley.get(SDDataComponents.MEDLEY_CONTENT.get());
        if(content != null) stacks.addAll(content.stacks());
        return stacks;
    }

    public ShapedRecipePattern getPattern(){
        return ((ShapedRecipeAccessor)this).sdtfc$getPattern();
    }

    public ItemStack getResult(){
        return ((ShapedRecipeAccessor)this).sdtfc$getResult();
    }


    @Override public boolean matches(@NotNull CraftingInput inv, @NotNull Level level) {
        boolean anyRot = inv.items().stream().anyMatch(item -> {
            IFood food = FoodCapability.get(item);
            if(food == null) return false;
            return food.isRotten();
        });
        if(anyRot) return false;
        boolean primaryMatch = super.matches(inv, level);
        if(primaryMatch) return true;
        SimpleCraftingContainer invTemp = new SimpleCraftingContainer(inv);
        invTemp.replaceContainers(container);
        return super.matches(invTemp.asCraftInput(), level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.@NotNull Provider ra) {
        var out = super.assemble(inv, ra);
        ItemStack resultContainer = ItemStack.EMPTY;
        long oldestCreationAt = Integer.MAX_VALUE;
        List<ItemStack> foodStacks = new ArrayList<>();

        final int slots = inv.size();
        for (int i = 0; i < slots; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (container != null && resultContainer.isEmpty()) {
                //only process when there's no defined container yet.
                if (s.is(container)) {
                    resultContainer = Optional.ofNullable(FoodItemContainerApply.getContainer(s)).orElse(ItemStack.EMPTY);
                    //let's say I put a salad or a soup as "container";
                    // this makes the recipe recognize it and apply the correct container.
                    if (resultContainer.isEmpty()) resultContainer = s.copyWithCount(1);
                } else if (FoodContainerExpansion.isExtraValid(container, s)) {
                    resultContainer = s.copyWithCount(1);
                }
            }
            IFood sFood = FoodCapability.get(s);
            if(sFood != null) {
                oldestCreationAt = Math.min(oldestCreationAt, sFood.getCreationDate());
                foodStacks.add(s);
            }
        }
        out.set(SDDataComponents.FOOD_CONTAINER_STACK.get(), new SDContainerStack(resultContainer));
        applyFoodStackForMedley(out, foodStacks);
        return out;
    }

    public static class Serializer implements RecipeSerializer<MedleyCraftingRecipe> {
        private static final MapCodec<MedleyCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(MedleyCraftingRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(MedleyCraftingRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(MedleyCraftingRecipe::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MedleyCraftingRecipe::getResult),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(r -> false),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("container").forGetter(MedleyCraftingRecipe::getContainer)
        ).apply(instance, (a,s,d,f,g,h) -> new MedleyCraftingRecipe(a,s,d,f,h)));

        private static final StreamCodec<RegistryFriendlyByteBuf, MedleyCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                MedleyCraftingRecipe::getGroup,

                CraftingBookCategory.STREAM_CODEC,
                MedleyCraftingRecipe::category,

                ShapedRecipePattern.STREAM_CODEC,
                MedleyCraftingRecipe::getPattern,

                ItemStack.STREAM_CODEC,
                MedleyCraftingRecipe::getResult,

                ByteBufCodecs.registry(Registries.ITEM),
                MedleyCraftingRecipe::getContainer,

                MedleyCraftingRecipe::new
        );

        @Override
        public @NotNull MapCodec<MedleyCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MedleyCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }}
