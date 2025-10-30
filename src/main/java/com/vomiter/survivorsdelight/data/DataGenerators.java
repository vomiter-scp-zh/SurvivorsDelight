package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.asset.SDCabinetBlockStateProvider;
import com.vomiter.survivorsdelight.data.asset.SDLangProvider;
import com.vomiter.survivorsdelight.data.asset.SDSkilletBlockStateProvider;
import com.vomiter.survivorsdelight.data.book.SDPatchouliCategoryProvider;
import com.vomiter.survivorsdelight.data.book.SDPatchouliContent;
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

import java.io.FileNotFoundException;
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

        SDPatchouliCategoryProvider cats = new SDPatchouliCategoryProvider(output);
        SDPatchouliEntryProvider entries = new SDPatchouliEntryProvider(output);

        SDPatchouliContent.accept(cats, entries);
        generator.addProvider(event.includeClient(), cats);
        generator.addProvider(event.includeClient(), entries);

        generator.addProvider(event.includeServer(), new SDFoodDataProvider(output, SurvivorsDelight.MODID));
        generator.addProvider(event.includeServer(), new SDItemSizeProvider(output, SurvivorsDelight.MODID));
    }

    private static void ensureTfcNotRottenRegistered() {
        try {
            // 取得「同一個實例」
            var notRotten = net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient.Serializer.INSTANCE;

            // 用反查看看這個實例是否已被賦予 id
            if (CraftingHelper.getID(notRotten) == null) {
                // 沒有的話，補註冊到 tfc:not_rotten
                CraftingHelper.register(new ResourceLocation("tfc", "not_rotten"), notRotten);
            }
        } catch (Throwable ignored) {
            // 沒有 TFC 類別（你把 TFC 拔掉）就略過；不會用到就不需要註冊
        }
    }
}