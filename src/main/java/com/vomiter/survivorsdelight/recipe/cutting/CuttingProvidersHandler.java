package com.vomiter.survivorsdelight.recipe.cutting;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import java.util.List;

public interface CuttingProvidersHandler {
    void sdtfc$setProviders(List<ItemStackProvider> providers);
    List<ItemStackProvider> sdtfc$getProviders();
}