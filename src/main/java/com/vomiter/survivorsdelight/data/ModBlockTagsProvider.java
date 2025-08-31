package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.tags.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
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
        TagKey<Block> STATIC_HEAT_250 = ModBlockTags.STATIC_HEAT_250;
        TagKey<Block> STATIC_HEAT_500 = ModBlockTags.STATIC_HEAT_500;
        TagKey<Block> STATIC_HEAT_1500 = ModBlockTags.STATIC_HEAT_1500;

        TFC_Constants.ROCKS.forEach((name, rock) -> {
            if(
                    rock.category().equals(TFC_Constants.RockCategory.IGNEOUS_INTRUSIVE)
                    || rock.category().equals(TFC_Constants.RockCategory.IGNEOUS_EXTRUSIVE)
            ){
                tag(STATIC_HEAT_250)
                        .add(
                                ResourceKey.create(
                                        ResourceKey.createRegistryKey(ResourceLocation.tryBuild("minecraft", "block")),
                                        ResourceLocation.tryBuild(TFC_Constants.MODID, "rock/magma/" + name)
                                )
                        );
            }
        });

        tag(STATIC_HEAT_500) //fire. (not very sure about the vanilla magma and campfires, but maybe it could provide some compat in modpacks.)
                .add(Blocks.FIRE)
                .add(Blocks.MAGMA_BLOCK)
                .addTag(BlockTags.CAMPFIRES);

        tag(STATIC_HEAT_1500) //Lava, lava cauldron
                .add(Blocks.LAVA)
                .add(Blocks.LAVA_CAULDRON);
    }
}
