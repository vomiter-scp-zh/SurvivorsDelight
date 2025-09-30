package com.vomiter.survivorsdelight.core.food.trait;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class SDFoodTraits {
    public static final String KEY_PREFIX = SurvivorsDelight.MODID + ".tooltip.foodtrait.";
    public static final ResourceKey<Registry<FoodTrait>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("food_trait"));
    public static final Registry<FoodTrait> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();
    public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(KEY, SurvivorsDelight.MODID);


    private static boolean BOOTSTRAPPED = false;

    private static ResourceLocation id(String path) {
        return RLUtils.build(SurvivorsDelight.MODID, path);
    }

    public static DeferredHolder<FoodTrait, FoodTrait> FOOD_MODEL;
    public static DeferredHolder<FoodTrait, FoodTrait> SKILLET_COOKED;

    public static void bootstrap() {
        if (BOOTSTRAPPED) return;
        BOOTSTRAPPED = true;

        FOOD_MODEL     = TRAITS.register("food_model", () -> new FoodTrait(() -> 0.0, KEY_PREFIX + "food_model"));
        SKILLET_COOKED = TRAITS.register("skillet_cooked", () -> new FoodTrait(() -> 0.8, KEY_PREFIX + "skillet_cooked"));
    }

    private SDFoodTraits() {}
}