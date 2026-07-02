package com.vomiter.survivorsdelight.mixin.compat.emi;

import com.vomiter.survivorsdelight.compat.emi.ICookingPotEMIRecipeDuck;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = CookingPotEmiRecipe.class, remap = false)
public abstract class CookingPotEMIRecipeMixin implements ICookingPotEMIRecipeDuck {
    @Unique @Nullable
    private FluidStackIngredient sdtfc$fluidReq;
    @Unique private int sdtfc$fluidAmount;
    @Override public @Nullable FluidStackIngredient sdtfc$getFluidIngredient() { return sdtfc$fluidReq; }
    @Override public int sdtfc$getRequiredFluidAmount() { return sdtfc$fluidAmount; }
    @Override public void sdtfc$setFluidRequirement(@Nullable FluidStackIngredient ing, int amount) {
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
        Collection<Fluid> fluids = fluidStackIngredient.ingredient().fluids();
        Set<String> fluidNames = fluids.stream().map(f -> new FluidStack(f, fluidStackIngredient.amount()).getDisplayName().getString()).collect(Collectors.toSet());
        if (!fluids.isEmpty()) {
            var slot = addSlot(widgets, FluidEmiStack.of(fluids.stream().findFirst().get()), SD_FLUID_X, SD_FLUID_Y);
            slot.appendTooltip(Component.translatable("tooltip.survivorsdelight.fluid_required").append(Component.literal(" " + fluidStackIngredient.amount() + " mB")));
            if(fluidNames.size() > 1){
                fluidNames.forEach(name -> slot.appendTooltip(Component.literal(name)));
            }
        }
    }

}
