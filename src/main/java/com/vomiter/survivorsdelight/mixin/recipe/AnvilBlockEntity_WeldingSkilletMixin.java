package com.vomiter.survivorsdelight.mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingBonusComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnvilBlockEntity.class, remap = false)
public class AnvilBlockEntity_WeldingSkilletMixin {
    @ModifyExpressionValue(method = "weld", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/recipes/WeldingRecipe;assemble(Lnet/dries007/tfc/common/recipes/WeldingRecipe$Inventory;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack applyForgingBonus(
            ItemStack original,
            @Local(ordinal = 0) ItemStack left,
            @Local(ordinal = 1) ItemStack right
    ){
        boolean leftIsHead  =
                SDSkilletPartItems.HEADS.values().stream()
                        .anyMatch(ro -> left.is(ro.get()));
        boolean rightIsHead =
                SDSkilletPartItems.HEADS.values().stream()
                        .anyMatch(ro -> right.is(ro.get()));
        ForgingBonusComponent bonusComponent = leftIsHead ? left.get(TFCComponents.FORGING_BONUS.get()) : (rightIsHead ? right.get(TFCComponents.FORGING_BONUS.get()) : null);
        ForgingBonus bonus = bonusComponent == null ? ForgingBonus.NONE : bonusComponent.type();
        if (!bonus.equals(ForgingBonus.NONE)) {
            original.set(TFCComponents.FORGING_BONUS.get(), bonusComponent);
        }
        return original;
    }
}
