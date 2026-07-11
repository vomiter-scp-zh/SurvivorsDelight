package com.vomiter.survivorsdelight.mixin.compat.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.compat.emi.ICookingPotEMIRecipeDuck;
import com.vomiter.survivorsdelight.registry.recipe.SDCookingPotRecipe;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.integration.emi.EMIPlugin;

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
                    if(recipe.value() instanceof SDCookingPotRecipe fluidRequiringRecipe && fluidRequiringRecipe.getFluid()!= null){
                        SurvivorsDelight.LOGGER.info("fluid = {}", fluidRequiringRecipe.getFluid());
                        cookingPotEmiRecipe.sdtfc$setFluidRequirement(new SizedFluidIngredient(fluidRequiringRecipe.getFluid(), fluidRequiringRecipe.getFluidAmountMb()), fluidRequiringRecipe.getFluidAmountMb());
                    }
                });
            });

        }
    }

}
