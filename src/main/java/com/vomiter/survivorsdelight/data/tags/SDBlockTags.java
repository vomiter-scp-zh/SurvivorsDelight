package com.vomiter.survivorsdelight.data.tags;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SDBlockTags {
    public static TagKey<Block> create(String path){
        return TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(SurvivorsDelight.MODID, path)
        );
    }

    public static final TagKey<Block> STATIC_HEAT_LOW = create("static_heat_low");
    public static final TagKey<Block> STATIC_HEAT_MODERATE = create("static_heat_moderate");
    public static final TagKey<Block> STATIC_HEAT_HIGH = create("static_heat_high");
    public static final TagKey<Block> HEAT_TO_BLOCK_BLACKLIST = create("heat_to_block_blacklist");
    public static final TagKey<Block> HEAT_TO_IN_HAND_BLACKLIST = create("heat_to_in_hand_blacklist");
    public static final TagKey<Block> SKILLETS = create("skillets");

}
