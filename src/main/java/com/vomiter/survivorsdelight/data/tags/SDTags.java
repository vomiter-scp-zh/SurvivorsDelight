package com.vomiter.survivorsdelight.data.tags;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class SDTags {
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookupProvider, helper);
        ModItemTagsProvider itemTags = new ModItemTagsProvider(output, lookupProvider, blockTags, helper);
        ModEntityTypeTagsProvider entityTags = new ModEntityTypeTagsProvider(output, lookupProvider, SurvivorsDelight.MODID, helper);

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), itemTags);
        generator.addProvider(event.includeServer(), entityTags);
    }

    public static class BlockTags{
        public static TagKey<Block> create(String path){
            return TagKey.create(
                    Registries.BLOCK,
                    RLUtils.build(SurvivorsDelight.MODID, path)
            );
        }

        public static final TagKey<Block> STATIC_HEAT_LOW = create("static_heat_low");
        public static final TagKey<Block> STATIC_HEAT_MODERATE = create("static_heat_moderate");
        public static final TagKey<Block> STATIC_HEAT_HIGH = create("static_heat_high");
        public static final TagKey<Block> HEAT_TO_BLOCK_BLACKLIST = create("heat_to_block_blacklist");
        public static final TagKey<Block> HEAT_TO_IN_HAND_BLACKLIST = create("heat_to_in_hand_blacklist");
        public static final TagKey<Block> SKILLETS = create("skillets");

    }

    public static class ItemTags {
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

}
