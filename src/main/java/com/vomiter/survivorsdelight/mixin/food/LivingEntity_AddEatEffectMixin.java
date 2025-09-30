package com.vomiter.survivorsdelight.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.FarmersDelight;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_AddEatEffectMixin {
    @ModifyArg(
            method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V")
    )
    private FoodProperties sdtfc$handleFDfoodEffects(
            FoodProperties foodProperties,
            @Local(argsOnly = true) ItemStack stack
            ){
        if(!FoodCapability.isRotten(stack)) return foodProperties;
        if(!SDUtils.isFromMod(stack, FarmersDelight.MODID)) return foodProperties;
        if(foodProperties == null) return null;
        var builder = new FoodProperties.Builder();
        foodProperties.effects().forEach(pe -> {
            if(pe.effect().getEffect().value().isBeneficial()) return;
            builder.effect(
                    pe::effect,
                    pe.probability()
            );
        });
        return builder.build();
    }
}
