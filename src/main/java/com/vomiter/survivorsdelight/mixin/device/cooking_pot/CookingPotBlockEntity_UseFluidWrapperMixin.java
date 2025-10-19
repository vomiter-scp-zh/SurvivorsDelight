package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.core.device.cooking_pot.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.core.device.cooking_pot.wrap.CookingPotFluidRecipeWrapper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

@Mixin(CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntity_UseFluidWrapperMixin {

    @Shadow @Final private ItemStackHandler inventory;

    /**
     * 假設 FD 在呼叫 getRecipeFor 前會 new RecipeWrapper(itemHandler)
     * 我們用 @ModifyArg 把它換成 PotRecipeWrapper(items, tankSnapshot)。
     */
    @ModifyArg(
            method = "cookingTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;getMatchingRecipe(Lnet/minecraftforge/items/wrapper/RecipeWrapper;)Ljava/util/Optional;" // new RecipeWrapper(IItemHandler)
            ),
            index = 0,
            remap = false
    )
    private static RecipeWrapper sdtfc$swapWrapper(
            RecipeWrapper inventoryWrapper,
            @Local(argsOnly = true)CookingPotBlockEntity cookingPot
    ) {
        var acc = (ICookingPotFluidAccess)cookingPot;
        return new CookingPotFluidRecipeWrapper(acc.sdtfc$getInventory(), acc.sdtfc$getTank().getFluid());
    }

}
