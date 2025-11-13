package com.vomiter.survivorsdelight.core.food.trait;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SDFoodTraits {
    public static final String KEY_PREFIX = SurvivorsDelight.MODID + ".tooltip.foodtrait.";
    public static final ResourceKey<Registry<FoodTrait>> KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("tfc", "food_trait"));

    public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(KEY, SurvivorsDelight.MODID);
    private static ResourceLocation id(String path) {
        return SDUtils.RLUtils.build(SurvivorsDelight.MODID, path);
    }
    public static DeferredHolder<FoodTrait, FoodTrait> FOOD_MODEL     = TRAITS.register("food_model", () -> new FoodTrait(() -> 0.0, KEY_PREFIX + "food_model"));
    public static DeferredHolder<FoodTrait, FoodTrait> SKILLET_COOKED = TRAITS.register("skillet_cooked", () -> new FoodTrait(() -> 0.8, KEY_PREFIX + "skillet_cooked"));
    public static DeferredHolder<FoodTrait, FoodTrait> CABINET_STORED = TRAITS.register("cabinet_stored", () -> new FoodTrait(() -> 0.5, KEY_PREFIX + "cabinet_stored"));

    private SDFoodTraits() {}
}