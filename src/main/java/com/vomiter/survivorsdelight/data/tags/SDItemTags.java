package com.vomiter.survivorsdelight.data.tags;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SDItemTags {
    public static TagKey<Item> create(String path){
        return TagKey.create(
                Registries.ITEM,
                RLUtils.build(SurvivorsDelight.MODID, path)
        );
    }
    public static final TagKey<Item> FOOD_MODEL_COATING = create("food_model_coating");
    public static final TagKey<Item> RETURN_COPPER_SKILLET = create("return_copper_skillet");
    public static final TagKey<Item> SKILLETS = create("skillets");
    public static final TagKey<Item> SKILLET_HEADS = create("skillet_heads");
    public static final TagKey<Item> UNFINISHED_SKILLETS = create("unfinished_skillets");
}
