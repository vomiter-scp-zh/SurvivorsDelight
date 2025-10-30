package com.vomiter.survivorsdelight.data.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.items.Food;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SDFoodDataProvider implements DataProvider {
    private final PackOutput packOutput;
    private final String modid;

    private final Map<ResourceLocation, JsonObject> entries = new LinkedHashMap<>();

    public SDFoodDataProvider(PackOutput packOutput, String modid) {
        this.packOutput = packOutput;
        this.modid = modid;
        FDFoodData fdFoodData = new FDFoodData(this);
        fdFoodData.save();
    }

    /** 使用單一 item 作為 ingredient（最常見情境） */
    public SDFoodDataProvider addStatic(String id, Item item,
                                        int hunger, double saturation, double water,
                                        double decayModifier,
                                        double grain, double fruit, double vegetables, double protein, double dairy) {
        JsonObject root = new JsonObject();
        root.add("ingredient", ingredientOf(item));
        root.addProperty("hunger", hunger);
        root.addProperty("saturation", saturation);
        root.addProperty("water", water);
        root.addProperty("decay_modifier", decayModifier);
        root.addProperty("grain", grain);
        root.addProperty("fruit", fruit);
        root.addProperty("vegetables", vegetables);
        root.addProperty("protein", protein);
        root.addProperty("dairy", dairy);
        entries.put(id(id), root);
        return this;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonObject> e : entries.entrySet()) {
            Path path = outputPath(e.getKey());
            futures.add(DataProvider.saveStable(cachedOutput, e.getValue(), path));
        }
        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return modid + " TFC Food Items";
    }

    // ---------- Helpers ----------

    private ResourceLocation id(String pathName) {
        return RLUtils.build(modid, pathName);
    }

    private Path outputPath(ResourceLocation id) {
        // data/<modid>/tfc/food_items/<path>.json
        return packOutput.getOutputFolder()
                .resolve("data/" + id.getNamespace() + "/tfc/food_items/" + id.getPath() + ".json");
    }

    private static JsonObject ingredientOf(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) throw new IllegalArgumentException("Unregistered item: " + item);
        JsonObject obj = new JsonObject();
        obj.addProperty("item", key.toString());
        return obj;
    }

    private static JsonObject ingredientTag(String tagPath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("tag", tagPath);
        return obj;
    }

    public static JsonArray ingredientAny(JsonElement... ingredients) {
        JsonArray arr = new JsonArray();
        for (JsonElement el : ingredients) arr.add(el);
        return arr;
    }

    public FoodData readTfcFoodJson(Food food) {
        return readTfcFoodJson(food.name().toLowerCase(Locale.ROOT));
    }


    public FoodData readTfcFoodJson(String path) {
        final ResourceLocation rl = RLUtils.build("tfc", path);
        final String cpPath = "data/" + rl.getNamespace() + "/tfc/food_items/" + rl.getPath() + ".json";
        InputStream in = getClass().getClassLoader().getResourceAsStream(cpPath);
        Reader r = new InputStreamReader(in, StandardCharsets.UTF_8);
        return FoodData.read(JsonParser.parseReader(r).getAsJsonObject());
    }

    public Builder newBuilder(String id) { return new Builder(this, id); }

    /** 方便使用：用 item 或 tag 做 ingredient，未指定營養=0，decay=1 */
    public static final class Builder {
        private final SDFoodDataProvider parent;
        private final String id;

        private JsonElement ingredient; // item / tag / 自定義 JSON
        private int hunger = 0;
        private double saturation = 0.0;
        private double water = 0.0;
        private double decay = 1.0;
        private double grain = 0.0;
        private double fruit = 0.0;
        private double vegetables = 0.0;
        private double protein = 0.0;
        private double dairy = 0.0;
        private String type;

        private Builder(SDFoodDataProvider parent, String id) {
            this.parent = parent;
            this.id = id;
        }

        public Builder from(Food food) {
            return from(parent.readTfcFoodJson(food));
        }
        public Builder from(FoodData data) {
            this.hunger = data.hunger();
            this.saturation = data.saturation();
            this.water = data.water();
            this.grain = data.grain();
            this.fruit = data.fruit();
            this.vegetables = data.vegetables();
            this.protein = data.protein();
            this.dairy = data.dairy();
            this.decay = data.decayModifier();
            return this;
        }

        public Builder slicedFrom(Food food, int n) {
            return slicedFrom(parent.readTfcFoodJson(food), n);
        }

        public Builder slicedFrom(FoodData data, int n) {
            if (n <= 0) throw new IllegalArgumentException("n must be >= 1");
            this.hunger = Math.round(data.hunger() / (float) n);
            this.saturation = data.saturation() / n;
            this.water = data.water() / n;
            this.grain = data.grain() / n;
            this.fruit = data.fruit() / n;
            this.vegetables = data.vegetables() / n;
            this.protein = data.protein() / n;
            this.dairy = data.dairy() / n;
            this.decay = data.decayModifier();
            return this;
        }

        public Builder multipliedFrom(Food food, int n) {
            return multipliedFrom(parent.readTfcFoodJson(food), n);
        }


        public Builder multipliedFrom(FoodData data, int n) {
            if (n <= 0) throw new IllegalArgumentException("n must be >= 1");
            this.hunger = data.hunger() * n;
            this.saturation = data.saturation() * n;
            this.water = data.water() * n;
            this.grain = data.grain() * n;
            this.fruit = data.fruit() * n;
            this.vegetables = data.vegetables() * n;
            this.protein = data.protein() * n;
            this.dairy = data.dairy() * n;
            this.decay = data.decayModifier();
            return this;
        }

        public Builder type(String type){
            this.type = type;
            return this;
        }


        /** 用單一 Item 作為 ingredient */
        public Builder item(Item item) {
            this.ingredient = ingredientOf(item);
            return this;
        }

        /** 用 tag 作為 ingredient，例如 "forge:crops/tomato" */
        public Builder tag(String tagPath) {
            this.ingredient = ingredientTag(tagPath);
            return this;
        }

        /** 進階：直接塞自定義的 ingredient JSON（可為陣列做 OR 等） */
        public Builder ingredient(JsonElement ingredientJson) {
            this.ingredient = ingredientJson;
            return this;
        }

        public Builder setHunger(int hunger) {
            this.hunger = hunger;
            return this;
        }

        public Builder setSaturation(double saturation) {
            this.saturation = saturation;
            return this;
        }

        public Builder setWater(double water) {
            this.water = water;
            return this;
        }

        /** 預設 1，不需要就別呼叫 */
        public Builder setDecay(double decayModifier) {
            this.decay = decayModifier;
            return this;
        }

        public Builder setGrain(double grain) {
            this.grain = grain;
            return this;
        }

        public Builder setFruit(double fruit) {
            this.fruit = fruit;
            return this;
        }

        public Builder setVegetables(double vegetables) {
            this.vegetables = vegetables;
            return this;
        }

        public Builder setProtein(double protein) {
            this.protein = protein;
            return this;
        }

        public Builder setDairy(double dairy) {
            this.dairy = dairy;
            return this;
        }

        /** 產生 JSON 物件但不存入 Provider（若你想先檢視用） */
        public JsonObject buildJson() {
            if (ingredient == null) {
                throw new IllegalStateException("Food item json must contain 'ingredient'");
            }
            JsonObject root = new JsonObject();
            root.add("ingredient", ingredient);
            root.addProperty("hunger", hunger);
            root.addProperty("saturation", saturation);
            root.addProperty("water", water);
            root.addProperty("decay_modifier", decay);
            root.addProperty("grain", grain);
            root.addProperty("fruit", fruit);
            root.addProperty("vegetables", vegetables);
            root.addProperty("protein", protein);
            root.addProperty("dairy", dairy);
            if(type != null) root.addProperty("type", type);
            return root;
        }

        /** 寫回 Provider 的 entries，回傳 Provider 方便繼續串其他東西 */
        public SDFoodDataProvider save() {
            JsonObject root = buildJson();
            parent.entries.put(parent.id(id), root);
            return parent;
        }
    }

}
