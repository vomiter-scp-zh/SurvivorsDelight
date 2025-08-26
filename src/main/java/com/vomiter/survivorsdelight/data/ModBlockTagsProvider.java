package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

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
        TagKey<Block> STATIC_HEAT_500 = TagKey.create(
                Registries.BLOCK, ResourceLocation.tryBuild(SurvivorsDelight.MODID, "static_heat_500"));
        TagKey<Block> STATIC_HEAT_1500 = TagKey.create(
                Registries.BLOCK, ResourceLocation.tryBuild(SurvivorsDelight.MODID, "static_heat_1500"));

        tag(STATIC_HEAT_500)
                .add(Blocks.FIRE)
                .add(Blocks.MAGMA_BLOCK)
                .addTag(BlockTags.CAMPFIRES);

        tag(STATIC_HEAT_1500)
                .add(Blocks.LAVA)           // 流動岩漿方塊
                .add(Blocks.LAVA_CAULDRON);  // 岩漿鍋
    }
}
