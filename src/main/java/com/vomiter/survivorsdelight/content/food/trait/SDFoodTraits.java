package com.vomiter.survivorsdelight.content.food.trait;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.resources.ResourceLocation;

public final class SDFoodTraits {
    public static final String KEY_PREFIX = "foodtrait." + SurvivorsDelight.MODID + ".";

    private static boolean BOOTSTRAPPED = false;

    private static ResourceLocation id(String path) {
        return SDUtils.RLUtils.build(SurvivorsDelight.MODID, path);
    }
    private static String translationKey(String path){return KEY_PREFIX + path;}
    private static FoodTrait create(String path, float decay){return FoodTrait.register(id(path), new FoodTrait(decay, translationKey(path)));
    }

    public static FoodTrait FOOD_MODEL;
    public static FoodTrait SKILLET_COOKED;
    public static FoodTrait CABINET_STORED;

    public static void bootstrap() {
        if (BOOTSTRAPPED) return;
        BOOTSTRAPPED = true;

        FOOD_MODEL     = create("food_model", 0);
        SKILLET_COOKED = create("skillet_cooked", 0.8f);
        CABINET_STORED = create("cabinet_stored", 0.5f);
    }

    private SDFoodTraits() {}
}