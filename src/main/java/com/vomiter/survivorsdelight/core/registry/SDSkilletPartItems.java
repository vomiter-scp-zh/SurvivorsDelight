package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class SDSkilletPartItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SurvivorsDelight.MODID);
    public static final Map<SkilletMaterial, DeferredItem<Item>> HEADS = new EnumMap<>(SkilletMaterial.class);
    public static final Map<SkilletMaterial, DeferredItem<Item>> UNFINISHED = new EnumMap<>(SkilletMaterial.class);
    public static final DeferredItem<Item> LINING_TIN = ITEMS.register("skillet_lining/tin", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LINING_SILVER = ITEMS.register("skillet_lining/silver", () -> new Item(new Item.Properties()));
    static {
        for (SkilletMaterial m : SkilletMaterial.values()) {
            if(Objects.equals(m.material, "copper_silver") || Objects.equals(m.material, "copper_tin")) continue;
            DeferredItem<Item> roh = ITEMS.register(m.path_head(), () -> new Item(new Item.Properties()));
            DeferredItem<Item> rouf = ITEMS.register(m.path_uf(), () -> new Item(new Item.Properties()));
            HEADS.put(m, roh);
            UNFINISHED.put(m, rouf);
        }
    }
}
