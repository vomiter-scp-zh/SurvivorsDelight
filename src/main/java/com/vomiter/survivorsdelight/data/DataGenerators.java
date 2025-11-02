package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.asset.SDCabinetBlockStateProvider;
import com.vomiter.survivorsdelight.data.asset.SDLangProvider;
import com.vomiter.survivorsdelight.data.asset.SDSkilletBlockStateProvider;
import com.vomiter.survivorsdelight.data.book.SDPatchouliCategoryProvider;
import com.vomiter.survivorsdelight.data.book.content.SDBookEN;
import com.vomiter.survivorsdelight.data.book.SDPatchouliEntryProvider;
import com.vomiter.survivorsdelight.data.food.SDFoodDataProvider;
import com.vomiter.survivorsdelight.data.loot.SDCabinetLootTableProvider;
import com.vomiter.survivorsdelight.data.loot.SDSkilletLootTableProvider;
import com.vomiter.survivorsdelight.data.recipe.SDRecipeDumpProvider;
import com.vomiter.survivorsdelight.data.recipe.SDRecipeProvider;
import com.vomiter.survivorsdelight.data.size.SDItemSizeProvider;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
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
        ensureTfcNotRottenRegistered();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        SDTags.gatherData(event);

        generator.addProvider(event.includeServer(), new SDRecipeProvider(output));
        generator.addProvider(event.includeServer(), new SDRecipeDumpProvider(output));
        generator.addProvider(event.includeServer(), new SDSkilletLootTableProvider(output));
        generator.addProvider(event.includeServer(), new SDCabinetLootTableProvider(output));

        generator.addProvider(event.includeClient(), new SDSkilletBlockStateProvider(output, helper));
        generator.addProvider(event.includeClient(), new SDCabinetBlockStateProvider(output, helper));

        generator.addProvider(event.includeClient(), new SDLangProvider(output, "en_us"));
        generator.addProvider(event.includeClient(), new SDLangProvider(output, "zh_tw"));

        generator.addProvider(event.includeServer(), new SDFoodDataProvider(output, SurvivorsDelight.MODID));
        generator.addProvider(event.includeServer(), new SDItemSizeProvider(output, SurvivorsDelight.MODID));

        SDBookEN.accept(event);

    }

    private static void ensureTfcNotRottenRegistered() {
        try {
            var notRotten = net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient.Serializer.INSTANCE;

            if (CraftingHelper.getID(notRotten) == null) {
                CraftingHelper.register(new ResourceLocation("tfc", "not_rotten"), notRotten);
            }
        } catch (Throwable ignored) {

        }
    }
}