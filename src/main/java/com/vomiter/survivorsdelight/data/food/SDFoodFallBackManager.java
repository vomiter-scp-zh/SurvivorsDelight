package com.vomiter.survivorsdelight.data.food;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SDFoodFallBackManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String DIRECTORY = "survivorsdelight/food_fallback";
    private static final Map<ResourceLocation, FoodData> foodDataMap = new HashMap<>();
    public SDFoodFallBackManager() {
        super(GSON, DIRECTORY);
    }
    private static final SDFoodFallBackManager INSTANCE = new SDFoodFallBackManager();
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }


    @Override
    protected void apply(
            @NotNull Map<ResourceLocation, JsonElement> map,
            @NotNull ResourceManager manager,
            @NotNull ProfilerFiller filler) {
        map.forEach((id, json) -> {
            var food = FoodData.read(json.getAsJsonObject());
            SurvivorsDelight.LOGGER.info("[SD Fallback] id = {}, food = {}", id, food);
            foodDataMap.put(id, food);
        });
    }

    public static FoodData getFood(ResourceLocation rl) {
        return foodDataMap.get(rl);
    }

    public static FoodData getFood(Item item) {
        var rl = ForgeRegistries.ITEMS.getKey(item);
        return foodDataMap.get(rl);
    }

}
