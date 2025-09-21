package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class SDSkilletPartItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SurvivorsDelight.MODID);
    public static final Map<SkilletMaterial, RegistryObject<Item>> HEADS = new EnumMap<>(SkilletMaterial.class);
    public static final Map<SkilletMaterial, RegistryObject<Item>> UNFINISHED = new EnumMap<>(SkilletMaterial.class);
    public static final RegistryObject<Item> LINING_TIN = ITEMS.register("skillet_lining/tin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LINING_SILVER = ITEMS.register("skillet_lining/silver", () -> new Item(new Item.Properties()));
    static {
        for (SkilletMaterial m : SkilletMaterial.values()) {
            if(Objects.equals(m.material, "copper_silver") || Objects.equals(m.material, "copper_tin")) continue;
            RegistryObject<Item> roh = ITEMS.register(m.path_head(), () -> new Item(new Item.Properties()));
            RegistryObject<Item> rouf = ITEMS.register(m.path_uf(), () -> new Item(new Item.Properties()));
            HEADS.put(m, roh);
            UNFINISHED.put(m, rouf);
        }
    }
}
