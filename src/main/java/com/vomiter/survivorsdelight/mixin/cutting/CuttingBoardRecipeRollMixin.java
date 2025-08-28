// src/main/java/com/vomiter/survivorsdelight/mixin/fde/CuttingBoardRecipeRollMixin.java
package com.vomiter.survivorsdelight.mixin.cutting;

import com.vomiter.survivorsdelight.recipe.cutting.CuttingContext;
import com.vomiter.survivorsdelight.recipe.cutting.CuttingProvidersHandler;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

@Mixin(value = CuttingBoardRecipe.class, remap = false)
public abstract class CuttingBoardRecipeRollMixin {
    @Inject(
            method = "rollResults(Lnet/minecraft/util/RandomSource;I)Ljava/util/List;",
            at = @At("RETURN")
    )
    private void sdl$applyProviders(RandomSource random, int fortune, CallbackInfoReturnable<List<ItemStack>> cir) {
        final List<ItemStack> rolled = cir.getReturnValue();
        final ItemStack input = CuttingContext.get();
        if (rolled == null || rolled.isEmpty() || input == null) return;

        final CuttingBoardRecipe self = (CuttingBoardRecipe)(Object)this;
        final List<ItemStackProvider> providers =
                (self instanceof CuttingProvidersHandler cuttingProvidersHandler) ? cuttingProvidersHandler.sdtfc$getProviders() : null;
        if (providers == null || providers.isEmpty()) return;

        final int n = Math.min(rolled.size(), providers.size());
        for (int i = 0; i < n; i++) {
            ItemStackProvider p = providers.get(i);
            if (p != null) {
                rolled.set(i, p.getStack(input));
            }
        }
    }
}
