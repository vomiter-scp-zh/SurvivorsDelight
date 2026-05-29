package com.vomiter.survivorsdelight.registry.recipe;

import com.vomiter.survivorsdelight.util.SimpleCraftingContainer;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class NutrientCraftingRecipe implements CraftingRecipe {
    final CraftingRecipe vanilla;
    final float balanceFactor;
    final float presetDecay;
    final int presetHunger;
    final boolean damageTool;
    final Item container;

    public NutrientCraftingRecipe(@Nullable CraftingRecipe vanilla, float balanceFactor, int presetHunger, float presetDecay, boolean damageTool, @Nullable Item container) {
        this.vanilla = vanilla;
        this.balanceFactor = balanceFactor;
        this.presetHunger = presetHunger;
        this.presetDecay = presetDecay;
        this.damageTool = damageTool;
        this.container = Objects.requireNonNullElse(container, Items.AIR);
    }

    @Override public boolean matches(@NotNull CraftingInput inv, @NotNull Level level) {
        boolean anyRot = inv.items().stream().anyMatch(item -> {
            IFood food = FoodCapability.get(item);
            if(food == null) return false;
            return food.isRotten();
        });
        if(anyRot) return false;
        boolean primaryMatch = vanilla.matches(inv, level);
        if(primaryMatch) return true;
        SimpleCraftingContainer invTemp = new SimpleCraftingContainer(inv);
        invTemp.replaceContainers(container); // <- causing crafting grid unsync and emptied unexpectedly
        return vanilla.matches(invTemp.asCraftInput(), level);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput inv) {
        // 先保留 vanilla shaped recipe 原本的 remaining item 行為
        // 例如 bucket、bowl、container item 等
        NonNullList<ItemStack> remaining = vanilla.getRemainingItems(inv);

        if (!damageTool) {
            return remaining;
        }

        final int slots = inv.size();

        for (int i = 0; i < slots; i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            // 只處理有 durability 的工具 / 物品
            if (!stack.isDamageableItem()) {
                continue;
            }

            ItemStack damaged = stack.copyWithCount(1);
            damaged.setDamageValue(damaged.getDamageValue() + 1);

            // 如果耐久耗盡，就不留下
            if (damaged.getDamageValue() >= damaged.getMaxDamage()) {
                remaining.set(i, ItemStack.EMPTY);
            } else {
                remaining.set(i, damaged);
            }
        }

        return remaining;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.@NotNull Provider registries) {
        ItemStack out = vanilla.assemble(inv, registries).copy();

        List<FoodData> data = new ArrayList<>();
        List<ItemStack> ingredients = new ArrayList<>();

        final int slots = inv.size();
        for (int i = 0; i < slots; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;

            var fh = FoodCapability.get(s);
            if (fh == null) continue;
            data.add(fh.getData());

            boolean merged = false;
            for (int j = 0; j < ingredients.size(); j++) {
                ItemStack e = ingredients.get(j);
                if (ItemStack.isSameItem(e, s)) {
                    ItemStack copy = e.copyWithCount(1);
                    copy.setCount(e.getCount() + 1); // 每格代表消耗 1
                    ingredients.set(j, copy);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                ItemStack one = s.copyWithCount(1);
                ingredients.add(one);
            }


        }
        ingredients.sort(Comparator.comparing(ItemStack::getCount)
                .thenComparing(item -> Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.getItem()))));


        final int foodCount = data.size();
        float factor = 1f - (this.balanceFactor * foodCount);
        if (factor < 0f) factor = 0f;

        int hunger = 0;
        float saturation = 0f;
        float water = 0;
        float[] nutrients = new float[Nutrient.VALUES.length];

        for (var d : data) {
            if(this.presetHunger == -1) hunger = Math.max(d.hunger(), hunger);
            saturation += d.saturation();
            water += d.water();
            for (int i = 0; i < nutrients.length; i++) {
                nutrients[i] += d.nutrient(Nutrient.VALUES[i]) * factor;
            }
        }

        if (this.presetHunger != -1) hunger = presetHunger;
        hunger = Math.round(hunger / (float) out.getCount());
        saturation = Math.round(saturation / out.getCount());
        water = Math.round(water / out.getCount());

        FoodData merged = new FoodData(hunger, water, saturation, 0, nutrients, presetDecay);

        out.set(TFCComponents.INGREDIENTS, ItemListComponent.of(ingredients));
        FoodCapability.setFoodForDynamicItemOnCreate(out, merged);
        return out;
    }

    @Override public boolean canCraftInDimensions(int w, int h) { return vanilla.canCraftInDimensions(w, h); }
    @Override public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) { return vanilla.getResultItem(provider); }
    @Override public @NotNull RecipeType<?> getType() { return RecipeType.CRAFTING; }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override public @NotNull String getGroup() { return vanilla.getGroup(); }
    @Override public boolean isIncomplete() { return vanilla.isIncomplete(); }
    @Override public @NotNull NonNullList<Ingredient> getIngredients() { return vanilla.getIngredients(); }
}
