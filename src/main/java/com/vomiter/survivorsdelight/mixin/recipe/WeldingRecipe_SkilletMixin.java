package com.vomiter.survivorsdelight.mixin.recipe;

import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingBonusComponent;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
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
            method = "assemble",
            at = @At("RETURN"),
            cancellable = true
    )
    private void applyForgingBonus(WeldingRecipe.Inventory input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        boolean leftIsHead  =
                SDSkilletPartItems.HEADS.values().stream()
                        .anyMatch(ro -> input.getLeft().is(ro.get()));
        boolean rightIsHead =
                SDSkilletPartItems.HEADS.values().stream()
                        .anyMatch(ro -> input.getRight().is(ro.get()));
        ForgingBonusComponent bonusComponent = leftIsHead ? input.getLeft().get(TFCComponents.FORGING_BONUS.get()) : (rightIsHead ? input.getRight().get(TFCComponents.FORGING_BONUS.get()) : null);
        ForgingBonus bonus = bonusComponent == null ? ForgingBonus.NONE : bonusComponent.type();
        if (!bonus.equals(ForgingBonus.NONE)) {
            result.set(TFCComponents.FORGING_BONUS.get(), bonusComponent);
        }
        cir.setReturnValue(result);
    }
}
