package com.vomiter.survivorsdelight.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.utility.ItemUtils;

@Mixin(value = ItemUtils.class, remap = false)
public class ItemIsKnifeMixin {
    @Unique
    private static TagKey<Item> tfc$knife = ItemTags.create(ResourceLocation.fromNamespaceAndPath("tfc", "knives"));
    @Inject(method = "isKnife", at = @At("RETURN"), cancellable = true)
    private static void sdtfc$tfcKnifeIsKnife(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(stack.is(tfc$knife)){
            cir.setReturnValue(true);
        }
    }
}
