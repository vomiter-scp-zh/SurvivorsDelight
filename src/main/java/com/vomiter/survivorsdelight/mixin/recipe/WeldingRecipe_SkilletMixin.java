package com.vomiter.survivorsdelight.mixin.recipe;

import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import com.vomiter.survivorsdelight.data.tags.SDItemTags;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WeldingRecipe.class, remap = false)
public class WeldingRecipe_SkilletMixin {
    @Shadow @Final private ItemStackProvider output;

    @Inject(
            method = "assemble(Lnet/dries007/tfc/common/recipes/WeldingRecipe$Inventory;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void applyForgingBonus(WeldingRecipe.Inventory inventory, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir){
        if(!cir.getReturnValue().is(SDItemTags.SKILLETS)) return;
        ItemStack output = cir.getReturnValue().copy();
        if(
                SDSkilletPartItems.HEADS.values().stream().anyMatch(
                        ro -> inventory.getLeft().is(ro.get()))
        ){
            ForgingBonus.set(output, ForgingBonus.get(inventory.getLeft()));
        } else if (
                SDSkilletPartItems.HEADS.values().stream().anyMatch(
                        ro -> inventory.getRight().is(ro.get()))
        ) {
            ForgingBonus.set(output, ForgingBonus.get(inventory.getRight()));
        }
        cir.setReturnValue(output);
    }
}
