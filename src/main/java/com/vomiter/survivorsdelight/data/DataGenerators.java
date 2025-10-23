package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.asset.SDCabinetBlockStateProvider;
import com.vomiter.survivorsdelight.data.asset.SDLangProvider;
import com.vomiter.survivorsdelight.data.asset.SDSkilletBlockStateProvider;
import com.vomiter.survivorsdelight.data.loot.SDCabinetLootTableProvider;
import com.vomiter.survivorsdelight.data.loot.SDSkilletLootTableProvider;
import com.vomiter.survivorsdelight.data.recipe.SDRecipeProvider;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        SDTags.gatherData(event);

        generator.addProvider(event.includeServer(), new SDRecipeProvider(output));
        generator.addProvider(event.includeServer(), new SDSkilletLootTableProvider(output));
        generator.addProvider(event.includeServer(), new SDCabinetLootTableProvider(output));

        generator.addProvider(event.includeClient(), new SDSkilletBlockStateProvider(output, helper));
        generator.addProvider(event.includeClient(), new SDCabinetBlockStateProvider(output, helper));

        generator.addProvider(event.includeClient(), new SDLangProvider(output, "en_us"));
        generator.addProvider(event.includeClient(), new SDLangProvider(output, "zh_tw"));
    }
}