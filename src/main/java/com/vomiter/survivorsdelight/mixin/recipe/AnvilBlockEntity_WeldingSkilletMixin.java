package com.vomiter.survivorsdelight.mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.registry.skillet.SDSkilletPartItems;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnvilBlockEntity.class, remap = false)
public class AnvilBlockEntity_WeldingSkilletMixin {
    @ModifyExpressionValue(method = "weld", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/recipes/WeldingRecipe;assemble(Lnet/dries007/tfc/common/recipes/WeldingRecipe$Inventory;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;"))
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
        ForgingBonus bonus = leftIsHead ? ForgingBonus.get(left) : (rightIsHead ? ForgingBonus.get(right) : ForgingBonus.NONE);
        if (!bonus.equals(ForgingBonus.NONE)) {
            ForgingBonus.set(original, bonus);
        }
        return original;
    }
}
