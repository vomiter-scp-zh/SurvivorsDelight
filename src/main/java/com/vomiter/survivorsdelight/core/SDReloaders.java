package com.vomiter.survivorsdelight.core;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.cutting.CuttingProvidersFromRecipes;
import com.vomiter.survivorsdelight.core.device.cutting.CuttingProvidersHandler;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.List;

@EventBusSubscriber(modid = SurvivorsDelight.MODID)
public final class SDReloaders {
    private SDReloaders() {}

    private static CuttingProvidersFromRecipes CUTTING_FROM_RECIPES;

    public static List<ItemStackProvider> cuttingProviders(ResourceLocation recipeId) {
        return CUTTING_FROM_RECIPES != null ? CUTTING_FROM_RECIPES.get(recipeId) : List.of();
    }

    @SubscribeEvent
    public static void onAddReload(AddReloadListenerEvent e) {
        if (CUTTING_FROM_RECIPES == null) {
            CUTTING_FROM_RECIPES = new CuttingProvidersFromRecipes();
        }
        e.addListener(CUTTING_FROM_RECIPES);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        attach(e.getServer().getRecipeManager());
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent e) {
        attach(e.getRecipeManager());
    }

    private static void attach(RecipeManager manager) {
        // 1) 拿到該類型所有配方（List<RecipeHolder<CuttingBoardRecipe>>）
        RecipeType<CuttingBoardRecipe> type = ModRecipeTypes.CUTTING.get();
        var holders = manager.getAllRecipesFor(type);

        // 2) 逐一處理
        for (RecipeHolder<CuttingBoardRecipe> holder : holders) {
            ResourceLocation id = holder.id();
            CuttingBoardRecipe cut = holder.value();

            if (cut instanceof CuttingProvidersHandler h) {
                var prov = SDReloaders.cuttingProviders(id);
                if (!prov.isEmpty()) {
                    h.sdtfc$setProviders(prov);
                }
            }
        }
    }
}
