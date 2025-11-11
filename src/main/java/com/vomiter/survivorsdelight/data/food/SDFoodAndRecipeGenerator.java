package com.vomiter.survivorsdelight.data.food;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.registry.SDRecipeSerializers;
import com.vomiter.survivorsdelight.core.registry.recipe.ShapedLikeFinished;
import com.vomiter.survivorsdelight.data.recipe.builder.SDFDCookingPotRecipeBuilder;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.items.Food;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SDFoodAndRecipeGenerator {

    public static Map<ItemLike, FoodData> foodDataMap = new HashMap<>();

    private final String modId;

    private final AtomicReference<SDFoodDataProvider> providerCache = new AtomicReference<>(null);
    private final AtomicReference<PackOutput> packOutputRef = new AtomicReference<>(null);
    private final Supplier<SDFoodDataProvider> providerFactory;

    public SDFoodAndRecipeGenerator(String modId, SDFoodDataProvider readyProvider) {
        this.modId = Objects.requireNonNull(modId);
        Objects.requireNonNull(readyProvider);
        this.providerFactory = () -> readyProvider; // 不用延遲，直接回傳
        this.providerCache.set(readyProvider);
    }

    public SDFoodAndRecipeGenerator(String modId, PackOutput packOutput) {
        this.modId = Objects.requireNonNull(modId);
        this.packOutputRef.set(Objects.requireNonNull(packOutput));
        this.providerFactory = this::createProviderOrThrow;
    }

    public SDFoodAndRecipeGenerator(String modId) {
        this.modId = Objects.requireNonNull(modId);
        this.providerFactory = this::createProviderOrThrow;
    }

    public void injectPackOutput(PackOutput packOutput) {
        Objects.requireNonNull(packOutput, "packOutput");
        if (!this.packOutputRef.compareAndSet(null, packOutput)) {
            throw new IllegalStateException("PackOutput 已存在或已被使用，請勿重複注入。");
        }
    }

    /* =========================
       內部：取得或建立 Provider（只建立一次）
       ========================= */

    public SDFoodDataProvider provider() {
        SDFoodDataProvider cached = providerCache.get();
        if (cached != null) return cached;

        SDFoodDataProvider created = Objects.requireNonNull(providerFactory.get(), "providerFactory.get() 回傳了 null");
        if (providerCache.compareAndSet(null, created)) {
            return created;
        }
        return providerCache.get();
    }

    /** 真的需要建立時才建立；若未注入 PackOutput 就會清楚拋錯 */
    private SDFoodDataProvider createProviderOrThrow() {
        PackOutput out = packOutputRef.get();
        if (out == null) {
            throw new IllegalStateException(
                    "尚未注入 PackOutput。請在使用前先呼叫 injectPackOutput(packOutput)。"
            );
        }
        return new SDFoodDataProvider(out, modId, "Food Data for Cooking");
    }

    /* =========================
        DSL 與邏輯
       ========================= */

    public CookingBuilder cooking(String path, ItemLike result, int resultCount, int cookTime, float exp, ItemLike container) {
        return new CookingBuilder(path, result, resultCount, cookTime, exp, container);
    }

    public ShapedCraftingBuilder crafting(String path, ItemLike result, int resultCount) {
        return new ShapedCraftingBuilder(path, result, resultCount);
    }


    private ResourceLocation recipeId(String path) {
        return SDUtils.RLUtils.build(modId, "cooking/" + path);
    }

    public enum Kind {
        FOOD, NONFOOD
    }

    public record IngredientEntry(Kind kind, Ingredient ingredient, boolean notRotten, FoodData proxyData) {

        /* ====== 非食物 ====== */
        public static IngredientEntry nonFood(Ingredient any) {
            return new IngredientEntry(Kind.NONFOOD, any, false, null);
        }
        public static IngredientEntry nonFood(ItemLike ing) {
            return nonFood(Ingredient.of(ing));
        }
        public static IngredientEntry nonFood(TagKey<Item> tag) {
            return nonFood(Ingredient.of(tag));
        }

        /* ====== 食物（精確物品 or Tag）====== */

        public static IngredientEntry tagFood(TagKey<Item> tag, FoodData proxyData) {
            return new IngredientEntry(Kind.FOOD, Ingredient.of(tag), true, proxyData);
        }

        public static IngredientEntry tagFood(TagKey<Item> tag) {
            return new IngredientEntry(Kind.FOOD, Ingredient.of(tag), true, null);
        }

        public static IngredientEntry food(ItemLike concreteFoodItem, FoodData proxyData) {
            return new IngredientEntry(Kind.FOOD, Ingredient.of(concreteFoodItem), true, proxyData);
        }

        public static IngredientEntry food(ItemLike concreteFoodItem) {
            return new IngredientEntry(Kind.FOOD, Ingredient.of(concreteFoodItem), true, SDFoodAndRecipeGenerator.foodDataMap.get(concreteFoodItem));
        }

        public static IngredientEntry food(Food foodEnum) {
            final ItemLike item = SDUtils.getTFCFoodItem(foodEnum);
            final FoodData data = SurvivorsDelight.foodAndCookingGenerator
                    .provider().readTfcFoodJson(foodEnum);
            return food(item, data);
        }

        public static IngredientEntry food(Ingredient ingredient, FoodData proxyData) {
            return new IngredientEntry(Kind.FOOD, ingredient, true, proxyData);
        }

        /* ====== 一般化 item/ingredient 版本（可標示是否不腐敗）====== */
        public static IngredientEntry item(Kind kind, ItemLike item, boolean notRotten, FoodData proxyData) {
            return new IngredientEntry(kind, Ingredient.of(item), notRotten, proxyData);
        }
        public static IngredientEntry item(Kind kind, Ingredient ingredient, boolean notRotten, FoodData proxyData) {
            return new IngredientEntry(kind, ingredient, notRotten, proxyData);
        }
    }

    public static boolean isFoodDataStatic(List<IngredientEntry> entries){
        return entries.stream().allMatch(e ->
                e.kind().equals(Kind.NONFOOD) || e.proxyData() != null
        );
    }

    public final class CookingBuilder {
        private final String path;
        private final ItemLike result;
        private final int resultCount;
        private final int cookTime;
        private final float exp;
        private final ItemLike container;
        private Integer fluidAmount;
        private Object fluidKeyOrTag;
        private final List<IngredientEntry> entries = new ArrayList<>();
        private String whenModLoaded;
        private SDFoodDataProvider.Builder foodDataBuilder;
        private float balanceFactor = 0.04f;

        private CookingBuilder(String path, ItemLike result, int resultCount, int cookTime, float exp, ItemLike container) {
            this.path = path;
            this.result = result;
            this.resultCount = resultCount;
            this.cookTime = cookTime;
            this.exp = exp;
            this.container = container;
        }

        public List<IngredientEntry> getEntries(){return entries;}

        public CookingBuilder factorPerIngredient(float f){this.balanceFactor = f; return this;}
        public CookingBuilder add(IngredientEntry e) { entries.add(e); return this; }
        public CookingBuilder food(Food food) {add(IngredientEntry.food(food)); return this;}
        public CookingBuilder food(Item food) {add(IngredientEntry.food(food)); return this;}
        public CookingBuilder food(Item food, FoodData proxyData) {add(IngredientEntry.food(food, proxyData)); return this;}
        public CookingBuilder food(Ingredient food) {add(IngredientEntry.food(food, null)); return this;}
        public CookingBuilder food(Ingredient food, FoodData proxyData) {add(IngredientEntry.food(food, proxyData)); return this;}
        public CookingBuilder food(TagKey<Item> food) {add(IngredientEntry.tagFood(food)); return this;}
        public CookingBuilder food(TagKey<Item> tag, FoodData proxy) {add(IngredientEntry.tagFood(tag, proxy));return this;}
        public CookingBuilder nonfood(Ingredient ing) {add(IngredientEntry.nonFood(ing)); return this;}
        public CookingBuilder nonfood(ItemLike ing) {return nonfood(Ingredient.of(ing));}
        public CookingBuilder nonfood(TagKey<Item> ing) {return nonfood(Ingredient.of(ing));}
        public CookingBuilder whenModLoaded(String modid) { this.whenModLoaded = modid; return this; }
        public CookingBuilder fluid(Fluid source, int amount) { this.fluidKeyOrTag = source; this.fluidAmount = amount; return this; }
        public CookingBuilder fluid(TagKey<Fluid> tag, int amount) { this.fluidKeyOrTag = tag; this.fluidAmount = amount; return this; }

        public CookingBuilder build(Consumer<FinishedRecipe> out) {
            final SDFDCookingPotRecipeBuilder pot = SDFDCookingPotRecipeBuilder
                    .cookingPotRecipe(result, resultCount, cookTime, exp, container == null ? null : container.asItem());

            for (IngredientEntry e : entries) {
                if (e.notRotten) pot.addIngredientNotRotten(e.ingredient);
                else pot.addIngredient(e.ingredient);
            }
            if (fluidAmount != null && fluidKeyOrTag != null) {
                if (fluidKeyOrTag instanceof Fluid f) pot.fluid(f, fluidAmount);
                else pot.fluid((TagKey<Fluid>) fluidKeyOrTag, fluidAmount);
            }
            if (whenModLoaded != null) pot.whenModLoaded(whenModLoaded);
            pot.build(out, recipeId(path));

            final boolean foodDataIsStatic = isFoodDataStatic(entries);
            foodDataBuilder = provider()
                    .newBuilder(path)
                    .item(result.asItem());

            if (!foodDataIsStatic) {
                foodDataBuilder.type("dynamic");
                return this;
            }

            final long foodCount = entries.stream().filter(e -> e.kind == Kind.FOOD).count();
            float factor = 1f - (balanceFactor * (int) foodCount);
            if (factor < 0f) factor = 0f;

            for (IngredientEntry e : entries) {
                if (e.kind == Kind.FOOD) {
                    if (e.proxyData() == null) continue;
                    foodDataBuilder.addNutrientsAndSetMaxHunger(e.proxyData(), factor);
                }
            }
            return this;
        }

        public SDFoodDataProvider.Builder getFoodData() {
            return foodDataBuilder;
        }

        public void saveFoodData() {
            foodDataBuilder.save();
        }
    }

    public final class ShapedCraftingBuilder{
        private final String path;
        private final ItemLike result;
        private final int resultCount;
        private final ShapedRecipeBuilder innerBuilder;
        private final List<String> patterns = new ArrayList<>();
        private final List<IngredientEntry> entries = new ArrayList<>();
        private final Map<Character, IngredientEntry> keyMap = new LinkedHashMap<>();
        private String group;
        private String whenModLoaded;
        private SDFoodDataProvider.Builder foodDataBuilder;
        private float balanceFactor = 0.04f;
        private int presetHunger = -1;
        private float presetDecay = 4.5f;

        public List<IngredientEntry> getEntries(){return entries;}

        private Supplier<? extends RecipeSerializer<?>> customSerializer = null;

        private ShapedCraftingBuilder(String path, ItemLike result, int resultCount) {
            this.path = path;
            this.result = result;
            this.resultCount = resultCount;
            innerBuilder = ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, result, resultCount);
        }

        public ShapedCraftingBuilder group(String g){ this.group = g; innerBuilder.group(g); return this; }
        public ShapedCraftingBuilder factorPerIngredient(float f){ this.balanceFactor = f; return this; }
        public ShapedCraftingBuilder serializer(Supplier<? extends RecipeSerializer<?>> s){ this.customSerializer = s; return this; }

        /** row/shape */
        public ShapedCraftingBuilder row(String p){
            innerBuilder.pattern(p);
            patterns.add(p);
            return this;
        }
        public ShapedCraftingBuilder shape(String... rows){
            for(String r : rows) row(r);
            return this;
        }

        public ShapedCraftingBuilder hunger(int i){
            this.presetHunger = i;
            return this;
        }

        public ShapedCraftingBuilder decay(float f){
            this.presetDecay = f;
            return this;
        }

        public ShapedCraftingBuilder define(char key, IngredientEntry entry){
            Objects.requireNonNull(entry, "entry");
            innerBuilder.define(key, entry.ingredient());
            keyMap.put(key, entry);
            entries.add(entry);
            return this;
        }
        public ShapedCraftingBuilder defineNonFood(char key, ItemLike ing){ return define(key, IngredientEntry.nonFood(ing)); }
        public ShapedCraftingBuilder defineNonFood(char key, TagKey<Item> tag){ return define(key, IngredientEntry.nonFood(tag)); }
        public ShapedCraftingBuilder defineNonFood(char key, Ingredient ing){ return define(key, IngredientEntry.nonFood(ing)); }
        public ShapedCraftingBuilder defineFood(char key, Food tfcFoodEnum){ return define(key, IngredientEntry.food(tfcFoodEnum)); }
        public ShapedCraftingBuilder defineFood(char key, ItemLike concreteFoodItem){ return define(key, IngredientEntry.food(concreteFoodItem)); }
        public ShapedCraftingBuilder defineFood(char key, ItemLike concreteFoodItem, FoodData proxyData){ return define(key, IngredientEntry.food(concreteFoodItem, proxyData)); }
        public ShapedCraftingBuilder defineFood(char key, TagKey<Item> tag){ return define(key, IngredientEntry.tagFood(tag)); }
        public ShapedCraftingBuilder defineFood(char key, TagKey<Item> tag, FoodData proxyData){ return define(key, IngredientEntry.tagFood(tag, proxyData)); }
        public ShapedCraftingBuilder defineFood(char key, Ingredient ingredient){ return define(key, IngredientEntry.food(ingredient, null)); }
        public ShapedCraftingBuilder defineFood(char key, Ingredient ingredient, FoodData proxyData){ return define(key, IngredientEntry.food(ingredient, proxyData)); }

        public ShapedCraftingBuilder whenModLoaded(String modid) { this.whenModLoaded = modid; return this; }

        public ShapedCraftingBuilder build(Consumer<FinishedRecipe> out){
            final ResourceLocation id = SDUtils.RLUtils.build(modId, "crafting/" + path);

            innerBuilder.unlockedBy(
                    "has_result",
                    InventoryChangeTrigger.TriggerInstance.hasItems(result)
            );

            final boolean foodDataIsStatic = isFoodDataStatic(entries);
            if(!foodDataIsStatic && customSerializer == null) serializer(SDRecipeSerializers.NUTRITION_CRAFTING);

            if (customSerializer == null) {
                if (whenModLoaded != null && !whenModLoaded.isBlank()) {
                    ConditionalRecipe.builder()
                            .addCondition(new ModLoadedCondition(whenModLoaded))
                            .addRecipe(r -> innerBuilder.save(r, id))
                            .build(out, id);
                } else {
                    innerBuilder.save(out, id);
                }
            } else {
                ShapedLikeFinished.Builder b = ShapedLikeFinished
                        .builder(id, new ItemStack(result.asItem(), resultCount), customSerializer)
                        .balance(this.balanceFactor)
                        .presetDecay(presetDecay)
                        .presetHunger(presetHunger);

                if (group != null) b.group(group);
                for (String p : patterns) b.row(p);
                for (Map.Entry<Character, IngredientEntry> e : keyMap.entrySet()) {
                    b.key(e.getKey(), e.getValue().ingredient());
                }

                FinishedRecipe fr = b.build();

                if (whenModLoaded != null && !whenModLoaded.isBlank()) {
                    ConditionalRecipe.builder()
                            .addCondition(new ModLoadedCondition(whenModLoaded))
                            .addRecipe(r -> r.accept(fr))
                            .build(out, id);
                } else {
                    out.accept(fr);
                }
            }


            foodDataBuilder = provider()
                    .newBuilder(path)
                    .item(result.asItem());

            if (!foodDataIsStatic) {
                foodDataBuilder.type("dynamic");
                return this;
            }

            final long foodCount = entries.stream().filter(e -> e.kind() == Kind.FOOD).count();
            float factor = 1f - (balanceFactor * (int) foodCount);
            if (factor < 0f) factor = 0f;

            for (IngredientEntry e : entries) {
                if (e.kind() == Kind.FOOD && e.proxyData() != null) {
                    foodDataBuilder.addNutrientsAndSetMaxHunger(e.proxyData(), factor);
                }
            }
            return this;
        }

        public SDFoodDataProvider.Builder getFoodData() { return foodDataBuilder; }
        public void saveFoodData() { if (foodDataBuilder != null) foodDataBuilder.save(); }
    }



}
