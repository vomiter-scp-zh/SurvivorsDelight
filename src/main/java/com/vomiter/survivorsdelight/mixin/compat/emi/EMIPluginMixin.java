package com.vomiter.survivorsdelight.mixin.compat.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.adapter.cooking_pot.fluid.IFluidRequiringRecipe;
import com.vomiter.survivorsdelight.compat.emi.EMIRegistryManager;
import com.vomiter.survivorsdelight.compat.emi.ICookingPotEMIRecipeDuck;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.integration.emi.EMIPlugin;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;

import java.util.Optional;

@Mixin(value = EMIPlugin.class, remap = false)
public class EMIPluginMixin {
    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/EmiRegistry;addRecipe(Ldev/emi/emi/api/recipe/EmiRecipe;)V"))
    private void sdtfc$addEMIRecipe(EmiRegistry instance, EmiRecipe emiRecipe, Operation<Void> original){
        original.call(instance, emiRecipe);
        if(emiRecipe instanceof ICookingPotEMIRecipeDuck cookingPotEmiRecipe){
            Optional.ofNullable(emiRecipe.getId()).ifPresent(id -> {
                SurvivorsDelight.LOGGER.info("id = {}", emiRecipe.getId());
                instance.getRecipeManager().byKey(id).ifPresent(recipe -> {
                    SurvivorsDelight.LOGGER.info("recipe = {}", recipe);
                    if(recipe instanceof IFluidRequiringRecipe fluidRequiringRecipe){
                        SurvivorsDelight.LOGGER.info("fluid = {}", fluidRequiringRecipe.sdtfc$getFluidIngredient());
                        cookingPotEmiRecipe.sdtfc$setFluidRequirement(fluidRequiringRecipe.sdtfc$getFluidIngredient(), fluidRequiringRecipe.sdtfc$getRequiredFluidAmount());
                    }
                });
            });

        }
    }

}
