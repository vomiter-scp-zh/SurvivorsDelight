package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsabilities.core.SAEffects;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class SDItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SurvivorsDelight.MODID);
    public static final RegistryObject<Item> EFFECT_NOURISHMENT =
            ITEMS.register("effect_icon/nourishment", () -> new Item(new Item.Properties()){
                @Override
                public @NotNull String getDescriptionId() {
                    return ModEffects.NOURISHMENT.get().getDescriptionId();
                }
            });
    public static final RegistryObject<Item> EFFECT_COMFORT =
            ITEMS.register("effect_icon/comfort", () -> new Item(new Item.Properties()){
                @Override
                public @NotNull String getDescriptionId() {
                    return ModEffects.COMFORT.get().getDescriptionId();
                }
            });
    public static final RegistryObject<Item> EFFECT_WORKHORSE =
            ITEMS.register("effect_icon/workhorse", () -> new Item(new Item.Properties()){
                @Override
                public @NotNull String getDescriptionId() {
                    return SAEffects.WORKHORSE.get().getDescriptionId();
                }
            });
}
