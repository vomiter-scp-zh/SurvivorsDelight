package com.vomiter.survivorsdelight.mixin.device.cutting;

import com.vomiter.survivorsdelight.core.device.cutting.CuttingProvidersHandler;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

@Mixin(value = CuttingBoardRecipe.class, remap = false)
public class CuttingBoardRecipeMixin implements CuttingProvidersHandler {
    @Unique
    private List<ItemStackProvider> sdtfcs$providers;

    @Override
    public void sdtfc$setProviders(List<ItemStackProvider> providers) {
        this.sdtfcs$providers = providers;
    }


    @Override
    public List<ItemStackProvider> sdtfc$getProviders() {
        return this.sdtfcs$providers;
    }
}