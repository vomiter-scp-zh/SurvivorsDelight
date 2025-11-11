package com.vomiter.survivorsdelight.data.recipe;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;

import java.util.List;

/** 在 FD Cutting 基礎上，額外支援 TFC 的 ItemStackProvider 輸出。 */
public class SDCuttingRecipe extends CuttingBoardRecipe {
    private final List<ItemStackProvider> providers;
    private final List<ChanceResult> vanillaResults;

    public SDCuttingRecipe(ResourceLocation id,
                           String group,
                           Ingredient ingredient,
                           Ingredient tool,
                           NonNullList<ChanceResult> vanillaResults,
                           List<ItemStackProvider> providers,
                           String soundEvent
    ) {
        super(id, group, ingredient, tool, vanillaResults, soundEvent);
        this.providers = providers;
        this.vanillaResults = vanillaResults;
    }

    public List<ItemStackProvider> getProviders() {
        return providers;
    }

    public List<ChanceResult> getVanillaResults(){
        return vanillaResults;
    }


    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        NonNullList<ChanceResult> rolls = super.getRollableResults();
        if (rolls.isEmpty()) {
            return ItemStack.EMPTY; // 關鍵：避免 FD 內部對 index 0 的假設
        }
        // FD 原邏輯等同於拿第一個結果；你也可以依需求改成合併或隨機
        return rolls.get(0).getStack();
    }

    @Override
    public @NotNull NonNullList<ChanceResult> getRollableResults() {
        if (providers.isEmpty()) return super.getRollableResults();
        final NonNullList<ChanceResult> out = NonNullList.create();
        out.addAll(vanillaResults); // 兼容原本的
        for (ItemStackProvider isp : providers) {
            out.add(new ChanceResult(isp.stack().get(), 1.0f));
        }
        return out;
    }
}
