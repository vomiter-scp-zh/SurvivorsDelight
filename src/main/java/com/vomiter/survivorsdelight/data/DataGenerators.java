package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.data.SDSkilletBlockStateProvider;
import com.vomiter.survivorsdelight.core.device.skillet.data.SDSkilletBlockModelProvider;
import com.vomiter.survivorsdelight.core.device.skillet.data.SDSkilletItemModelProvider;
import com.vomiter.survivorsdelight.core.device.skillet.data.SDSkilletLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = SurvivorsDelight.MODID)
public class DataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookupProvider, helper);
        ModItemTagsProvider itemTags = new ModItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), helper);
        ModEntityTypeTagsProvider entityTags = new ModEntityTypeTagsProvider(output, lookupProvider, SurvivorsDelight.MODID, helper);

        SDSkilletBlockModelProvider skilletModelProvider = new SDSkilletBlockModelProvider(output, helper);
        SDSkilletBlockStateProvider skilletBlockStateProvider = new SDSkilletBlockStateProvider(output, helper);
        SDSkilletItemModelProvider skilletItemModelProvider = new SDSkilletItemModelProvider(output, helper);
        SDSkilletLootTableProvider skilletLootTableProvider = new SDSkilletLootTableProvider(output);

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), itemTags);
        generator.addProvider(event.includeServer(), entityTags);

        generator.addProvider(true, skilletModelProvider);
        generator.addProvider(true, skilletBlockStateProvider);
        generator.addProvider(true, skilletItemModelProvider);
        generator.addProvider(event.includeServer(), skilletLootTableProvider);
    }
}