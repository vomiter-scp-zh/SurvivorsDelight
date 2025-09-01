package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.tags.SDBlockTags;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.tag.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput out,
                                CompletableFuture<HolderLookup.Provider> lookup,
                                @Nullable ExistingFileHelper efh) {
        super(out, lookup, SurvivorsDelight.MODID, efh);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> STATIC_HEAT_250 = SDBlockTags.STATIC_HEAT_LOW;
        TagKey<Block> STATIC_HEAT_500 = SDBlockTags.STATIC_HEAT_MODERATE;
        TagKey<Block> STATIC_HEAT_1500 = SDBlockTags.STATIC_HEAT_HIGH;

        TFCBlocks.MAGMA_BLOCKS.forEach((r, b)->{
            tag(STATIC_HEAT_250).add(b.getKey());
            tag(SDBlockTags.HEAT_TO_BLOCK_BLACKLIST).add(b.getKey());
        });

        tag(STATIC_HEAT_500) //fire. (not very sure about the vanilla magma and campfires, but maybe it could provide some compat in modpacks.)
                .add(Blocks.FIRE)
                .add(Blocks.MAGMA_BLOCK)
                .addTag(BlockTags.CAMPFIRES);

        tag(STATIC_HEAT_1500) //Lava, lava cauldron
                .add(Blocks.LAVA)
                .add(Blocks.LAVA_CAULDRON);

        tag(TFCTags.Blocks.FORGE_INVISIBLE_WHITELIST)
                .add(ModBlocks.SKILLET.getKey())
                .add(ModBlocks.COOKING_POT.getKey());

        tag(ModTags.HEAT_CONDUCTORS)
                .add(TFCBlocks.CRUCIBLE.getKey());

    }
}
