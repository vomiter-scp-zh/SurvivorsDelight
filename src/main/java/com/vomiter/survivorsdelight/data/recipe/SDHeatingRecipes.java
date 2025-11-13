package com.vomiter.survivorsdelight.data.recipe;

import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.data.recipe.builder.SDTFCHeatingRecipeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class SDHeatingRecipes {

    private final String modid;

    public SDHeatingRecipes(String modid) {
        this.modid = modid;
    }

    /** 呼叫這個方法，把所有「生肉切片 → 熟肉切片」丟進 out */
    public void generate(BiConsumer<ResourceLocation, JsonObject> out) {
        // 1) 建 raw->cooked 對映。
        Map<Item, Item> rawToCooked = new LinkedHashMap<>();
        rawToCooked.put(ModItems.CHICKEN_CUTS.get(),         ModItems.COOKED_CHICKEN_CUTS.get());
        rawToCooked.put(ModItems.BACON.get(),                 ModItems.COOKED_BACON.get());
        rawToCooked.put(ModItems.COD_SLICE.get(),            ModItems.COOKED_COD_SLICE.get());
        rawToCooked.put(ModItems.SALMON_SLICE.get(),         ModItems.COOKED_SALMON_SLICE.get());
        rawToCooked.put(ModItems.MUTTON_CHOPS.get(),         ModItems.COOKED_MUTTON_CHOPS.get());
        rawToCooked.put(ModItems.MINCED_BEEF.get(),          ModItems.BEEF_PATTY.get());
        rawToCooked.put(ModItems.HAM.get(),                  ModItems.SMOKED_HAM.get());


        // 2) 針對每個 raw→cooked 輸出 heating/farmersdelight/<raw_path>.json
        for (Map.Entry<Item, Item> e : rawToCooked.entrySet()) {
            Item raw = e.getKey();
            Item cooked = e.getValue();

            ResourceLocation rawId = BuiltInRegistries.ITEM.getKey(raw);
            ResourceLocation cookedId = BuiltInRegistries.ITEM.getKey(cooked);

            if (rawId == null || cookedId == null) continue; // 未註冊的極端情況，保險

            String path = "farmersdelight/" + rawId.getPath(); // e.g. heating/farmersdelight/chicken_cuts.json

            SDTFCHeatingRecipeBuilder.heating(modid, path)
                    .ingredientNotRotten(raw)                          // {"type":"tfc:not_rotten","ingredient":{"item": "..."}}
                    .resultItemProvider(cooked, 1, "tfc:copy_food")    // { "stack": {"item": ...}, "modifiers": ["tfc:copy_food"] }
                    .temperature(200f)
                    .save(out);
        }
    }
}
