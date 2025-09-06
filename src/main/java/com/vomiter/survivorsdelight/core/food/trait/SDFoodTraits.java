package com.vomiter.survivorsdelight.core.food.trait;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.resources.ResourceLocation;

public final class SDFoodTraits {
    public static final String KEY_PREFIX = SurvivorsDelight.MODID + ".tooltip.foodtrait.";

    private static boolean BOOTSTRAPPED = false;

    private static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(SurvivorsDelight.MODID, path);
    }

    public static final ResourceLocation FOOD_MODEL_ID     = id("food_model");
    public static final ResourceLocation SKILLET_COOKED_ID = id("skillet_cooked");

    public static FoodTrait FOOD_MODEL;
    public static FoodTrait SKILLET_COOKED;

    public static void bootstrap() {
        if (BOOTSTRAPPED) return;
        BOOTSTRAPPED = true;

        FOOD_MODEL     = FoodTrait.register(FOOD_MODEL_ID,     new FoodTrait(0.0f, KEY_PREFIX + "food_model"));
        SKILLET_COOKED = FoodTrait.register(SKILLET_COOKED_ID, new FoodTrait(0.8f, KEY_PREFIX + "skillet_cooked"));
    }

    private SDFoodTraits() {}
}