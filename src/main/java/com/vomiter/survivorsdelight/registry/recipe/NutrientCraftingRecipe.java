package com.vomiter.survivorsdelight.registry.recipe;

import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.common.food.FoodContainerExpansion;
import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import com.vomiter.survivorsdelight.util.SimpleCraftingContainer;
import net.dries007.tfc.common.capabilities.food.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
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

    public NutrientCraftingRecipe(
            @Nullable CraftingRecipe vanilla,
            float balanceFactor,
            int presetHunger,
            float presetDecay,
            boolean damageTool,
            @Nullable Item container) {
        this.vanilla = vanilla;
        this.balanceFactor = balanceFactor;
        this.presetHunger = presetHunger;
        this.presetDecay = presetDecay;
        this.damageTool = damageTool;
        if(container == null){
            this.container = Items.AIR;
        } else this.container = container;
    }

    public void commonSerialization(JsonObject object, ItemStack result){
        JsonObject res = new JsonObject();
        res.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())).toString());
        if (result.getCount() > 1) res.addProperty("count", result.getCount());
        object.add("result", res);

        if(balanceFactor != 0.04f) object.addProperty("balance_factor", balanceFactor);
        if(presetHunger != -1) object.addProperty("hunger", presetHunger);
        if(presetDecay != 4.5f) object.addProperty("decay", presetDecay);
        if(!damageTool) object.addProperty("damage_tool", false);
        if(container != null && container != Items.AIR){
            object.addProperty("container", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(container)).toString());
        }
    }

    @Override public boolean matches(@NotNull CraftingContainer inv, @NotNull Level level) {
        boolean anyRot = inv.getItems().stream().anyMatch(item -> {
            IFood food = FoodCapability.get(item);
            if(food == null) return false;
            return food.isRotten();
        });
        if(anyRot) return false;
        boolean primaryMatch = vanilla.matches(inv, level);
        if(primaryMatch) return true;
        SimpleCraftingContainer invTemp = new SimpleCraftingContainer(inv);
        invTemp.replaceContainers(container); // <- causing crafting grid unsync and emptied unexpectedly
        return vanilla.matches(invTemp, level);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inv) {
        // 保留 vanilla shaped recipe 原本的 remaining item 行為
        NonNullList<ItemStack> remaining = vanilla.getRemainingItems(inv);

        if (!damageTool) {
            return remaining;
        }

        final int slots = inv.getContainerSize();

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
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess ra) {
        ItemStack out = vanilla.assemble(inv, ra).copy();

        List<FoodData> data = new ArrayList<>();
        List<ItemStack> ingredients = new ArrayList<>();
        ItemStack resultContainer = ItemStack.EMPTY;

        final int slots = inv.getContainerSize();
        for (int i = 0; i < slots; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if(container != null && resultContainer.isEmpty()){
                //only process when there's no defined container yet.
                if(s.is(container)){
                    resultContainer = FoodItemContainerApply.getContainer(s);
                    //let's say I put a salad or a soup as "container";
                    // this makes the recipe recognize it and apply the correct container.
                    if(resultContainer.isEmpty()) resultContainer = s.copyWithCount(1);
                } else if(FoodContainerExpansion.isExtraValid(container, s)){
                    resultContainer = s.copyWithCount(1);
                }
            }

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
                .thenComparing(item -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.getItem()))));


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

        if(this.presetHunger != -1) hunger = presetHunger;
        hunger = Math.round(hunger / (float)out.getCount());
        saturation = Math.round(saturation / out.getCount());
        water = Math.round(water / out.getCount());

        var outFood = FoodCapability.get(out);
        if (outFood instanceof FoodHandler.Dynamic outDynamic) {
            FoodData merged =
                    new FoodData(
                            hunger, saturation, water ,nutrients[0], nutrients[1], nutrients[2], nutrients[3], nutrients[4], presetDecay
                    );
            outDynamic.setIngredients(ingredients);
            outDynamic.setFood(merged);
        }
        if(!resultContainer.isEmpty()){
            FoodItemContainerApply.applyGeneral(out, resultContainer);
        }
        return out;
    }

    @Override public boolean canCraftInDimensions(int w, int h) { return vanilla.canCraftInDimensions(w, h); }
    @Override public @NotNull ItemStack getResultItem(@NotNull RegistryAccess ra) { return vanilla.getResultItem(ra); }
    @Override public @NotNull ResourceLocation getId() { return vanilla.getId(); }
    @Override public @NotNull RecipeType<?> getType() { return RecipeType.CRAFTING; }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override public @NotNull String getGroup() { return vanilla.getGroup(); }
    @Override public boolean isIncomplete() { return vanilla.isIncomplete(); }
    @Override public @NotNull NonNullList<Ingredient> getIngredients() { return vanilla.getIngredients(); }
}
