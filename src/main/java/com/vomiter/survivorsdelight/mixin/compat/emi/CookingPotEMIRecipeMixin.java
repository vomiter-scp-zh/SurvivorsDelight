package com.vomiter.survivorsdelight.mixin.compat.emi;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.compat.emi.ICookingPotEMIRecipeDuck;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(value = CookingPotEmiRecipe.class, remap = false)
public abstract class CookingPotEMIRecipeMixin implements ICookingPotEMIRecipeDuck {
    @Unique @Nullable
    private SizedFluidIngredient sdtfc$fluidReq;
    @Unique private int sdtfc$fluidAmount;
    @Override public @Nullable SizedFluidIngredient sdtfc$getFluidIngredient() { return sdtfc$fluidReq; }
    @Override public int sdtfc$getRequiredFluidAmount() { return sdtfc$fluidAmount; }
    @Override public void sdtfc$setFluidRequirement(@Nullable SizedFluidIngredient ing, int amount) {
        this.sdtfc$fluidReq = ing;
        this.sdtfc$fluidAmount = Math.max(0, amount);
    }

    @Shadow
    @Final
    private ResourceLocation id;

    @Shadow
    protected abstract SlotWidget addSlot(WidgetHolder widgets, EmiIngredient ingredient, int x, int y);

    @Unique
    private static final int SD_FLUID_X = 0;
    @Unique
    private static final int SD_FLUID_Y = 36;

    @Inject(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/widget/WidgetHolder;addAnimatedTexture(Lnet/minecraft/resources/ResourceLocation;IIIIIIIZZZ)Ldev/emi/emi/api/widget/AnimatedTextureWidget;"))
    private void sdtfc$addFluidSlot(WidgetHolder widgets, CallbackInfo ci){
        var fluidStackIngredient = sdtfc$fluidReq;
        if(fluidStackIngredient == null) return;
        FluidStack[] fluids = fluidStackIngredient.ingredient().getStacks();
        if (fluids.length > 0) {
            var slot = addSlot(widgets, EmiHelpers.toIngredient(fluidStackIngredient), SD_FLUID_X, SD_FLUID_Y);
            slot.appendTooltip(Component.translatable("tooltip.survivorsdelight.fluid_required").append(Component.literal(" " + fluidStackIngredient.amount() + " mB")));
        }
    }

}
