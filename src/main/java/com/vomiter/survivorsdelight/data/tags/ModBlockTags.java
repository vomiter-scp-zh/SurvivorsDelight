package com.vomiter.survivorsdelight.data.tags;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> STATIC_HEAT_500 = TagKey.create(
            Registries.BLOCK, ResourceLocation.tryBuild(SurvivorsDelight.MODID, "static_heat_500"));
    public static final TagKey<Block> STATIC_HEAT_1500 = TagKey.create(
            Registries.BLOCK, ResourceLocation.tryBuild(SurvivorsDelight.MODID, "static_heat_1500"));

}
