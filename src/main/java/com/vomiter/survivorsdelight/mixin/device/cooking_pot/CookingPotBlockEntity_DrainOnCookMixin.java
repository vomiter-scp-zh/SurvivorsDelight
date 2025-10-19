package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.vomiter.survivorsdelight.core.device.cooking_pot.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.core.device.cooking_pot.IFluidRequiringRecipe;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntity_DrainOnCookMixin {

    @Inject(
            method = "processCooking(Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;" +
                    "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;)Z",
            at = @At("RETURN"),
            remap = false
    )
    private void sdtfc$drainFluidWhenCooked(CookingPotRecipe recipe,
                                            CookingPotBlockEntity self,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return; // 沒真的做出成品就不扣

        IFluidRequiringRecipe duck = (IFluidRequiringRecipe) (Object) recipe;
        if (duck.sdtfc$getFluidIngredient() == null || duck.sdtfc$getRequiredFluidAmount() <= 0) return;
        var acc = (ICookingPotFluidAccess)this;
        acc.sdtfc$getTank().drain(((IFluidRequiringRecipe) recipe).sdtfc$getRequiredFluidAmount(), IFluidHandler.FluidAction.EXECUTE);

    }
}
