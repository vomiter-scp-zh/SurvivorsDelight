package com.vomiter.survivorsdelight.common.recipe;

import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.common.food.FoodContainerExpansion;
import com.vomiter.survivorsdelight.mixin.ShapedRecipeAccessor;
import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import com.vomiter.survivorsdelight.util.SimpleCraftingContainer;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MedleyCraftingRecipe extends ShapedRecipe {
    final Item container;

    public MedleyCraftingRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory category,
            int width,
            int height,
            NonNullList<Ingredient> ingredients,
            ItemStack result,
            boolean showNotification,
            Item container
    ) {
        super(id, group, category, width, height, ingredients, result, showNotification);
        this.container = container;
    }

    public Item getContainer(){
        return container;
    }

    static String MEDLEY_CONTENT_TAG = "medley_content";
    public static void applyFoodStackForMedley(ItemStack medley, List<ItemStack> foodStacks){
        var tag = medley.getOrCreateTag();
        var contentListTag = new ListTag();
        for (ItemStack foodStack : foodStacks) {
            contentListTag.add(foodStack.copyWithCount(1).save(new CompoundTag()));
        }
        tag.put(MEDLEY_CONTENT_TAG, contentListTag);
    }

    public static List<ItemStack> getFoodStackFromMedley(ItemStack medley){
        List<ItemStack> stacks = new ArrayList<>();
        var tag = medley.getTag();
        if(tag == null || tag.isEmpty()) return stacks;
        var content = tag.get(MEDLEY_CONTENT_TAG);
        if(content instanceof ListTag listTag){
            for (Tag tag1 : listTag) {
                stacks.add(ItemStack.of((CompoundTag) tag1));
            }
        }
        return stacks;
    }

    @Override public boolean matches(@NotNull CraftingContainer inv, @NotNull Level level) {
        boolean anyRot = inv.getItems().stream().anyMatch(item -> {
            IFood food = FoodCapability.get(item);
            if(food == null) return false;
            return food.isRotten();
        });
        if(anyRot) return false;
        boolean primaryMatch = super.matches(inv, level);
        if(primaryMatch) return true;
        SimpleCraftingContainer invTemp = new SimpleCraftingContainer(inv);
        invTemp.replaceContainers(container); // <- causing crafting grid unsync and emptied unexpectedly
        return super.matches(invTemp, level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess ra) {
        var out = super.assemble(inv, ra);
        ItemStack resultContainer = ItemStack.EMPTY;
        long oldestCreationAt = Integer.MAX_VALUE;
        List<ItemStack> foodStacks = new ArrayList<>();

        final int slots = inv.getContainerSize();
        for (int i = 0; i < slots; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (container != null && resultContainer.isEmpty()) {
                //only process when there's no defined container yet.
                if (s.is(container)) {
                    resultContainer = FoodItemContainerApply.getContainer(s);
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
        applyFoodStackForMedley(out, foodStacks);
        return out;
    }

    public static class Serializer  implements RecipeSerializer<MedleyCraftingRecipe> {

        @Override
        public @NotNull MedleyCraftingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            var vanilla = RecipeSerializer.SHAPED_RECIPE.fromJson(id, json);
            ShapedRecipeAccessor accessor = (ShapedRecipeAccessor) vanilla;
            Item container = Items.AIR;
            if (json.has("container")) {
                ResourceLocation containerId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "container"));
                container = ForgeRegistries.ITEMS.getValue(containerId);
            }

            return new MedleyCraftingRecipe(
                    accessor.sdtfc$getId(),
                    accessor.sdtfc$getGroup(),
                    accessor.sdtfc$getCategory(),
                    accessor.sdtfc$getWidth(),
                    accessor.sdtfc$getHeight(),
                    accessor.sdtfc$getRecipeItems(),
                    accessor.sdtfc$getResult().copy(),
                    accessor.sdtfc$getShowNotification(),
                    container
            );
        }

        @Override
        public @Nullable MedleyCraftingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf byteBuf) {
            var vanilla = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, byteBuf);
            if(vanilla == null) return null;
            ShapedRecipeAccessor accessor = (ShapedRecipeAccessor) vanilla;
            Item container = byteBuf.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            return new MedleyCraftingRecipe(
                    accessor.sdtfc$getId(),
                    accessor.sdtfc$getGroup(),
                    accessor.sdtfc$getCategory(),
                    accessor.sdtfc$getWidth(),
                    accessor.sdtfc$getHeight(),
                    accessor.sdtfc$getRecipeItems(),
                    accessor.sdtfc$getResult().copy(),
                    accessor.sdtfc$getShowNotification(),
                    container
            );
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf byteBuf, @NotNull MedleyCraftingRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(byteBuf, recipe);
            byteBuf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, Objects.requireNonNullElse(recipe.container, Items.AIR));
        }
    }
}
