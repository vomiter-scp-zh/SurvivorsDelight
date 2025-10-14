package com.vomiter.survivorsdelight.mixin.recipe;

import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
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
    private void applyForgingBonus(WeldingRecipe.Inventory inventory, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty() || !result.is(SDItemTags.SKILLETS)) return;

        ForgingBonus left  = net.dries007.tfc.common.capabilities.forge.ForgingBonus.get(inventory.getLeft());
        ForgingBonus right = net.dries007.tfc.common.capabilities.forge.ForgingBonus.get(inventory.getRight());

        boolean leftIsHead  = SDSkilletPartItems.HEADS.values().stream().anyMatch(ro -> inventory.getLeft().is(ro.get()));
        boolean rightIsHead = SDSkilletPartItems.HEADS.values().stream().anyMatch(ro -> inventory.getRight().is(ro.get()));

        ForgingBonus bonus = leftIsHead ? left : (rightIsHead ? right : ForgingBonus.NONE);

        if (!bonus.equals(ForgingBonus.NONE)) {
            ItemStack copy = result.copy();
            net.dries007.tfc.common.capabilities.forge.ForgingBonus.set(copy, bonus);
            cir.setReturnValue(copy);
        }
    }
}
