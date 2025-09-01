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
                ResourceLocation.tryBuild(SurvivorsDelight.MODID, path)
        );
    }

    public static final TagKey<Block> STATIC_HEAT_250 = create("static_heat_250");
    public static final TagKey<Block> STATIC_HEAT_500 = create("static_heat_500");
    public static final TagKey<Block> STATIC_HEAT_1500 = create("static_heat_1500");
    public static final TagKey<Block> HEAT_SKILLET_BLOCK_BLACKLIST = create("heat_skillet_block_blacklist");
    public static final TagKey<Block> HEAT_SKILLET_IN_HAND_BLACKLIST = create("heat_skillet_in_hand_blacklist");

}
