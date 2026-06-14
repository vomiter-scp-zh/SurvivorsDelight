package com.vomiter.survivorsdelight.mixin;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TFCFoodData.class, remap = false)
public class DebugTFCFoodDataMixin {
    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
    private void debug$eat1(Item maybeFood, ItemStack stack, LivingEntity entity, CallbackInfo ci){
        SurvivorsDelight.LOGGER.info("EAT1");
    }

    @Inject(method = "eat(Lnet/dries007/tfc/common/capabilities/food/IFood;)V", at = @At("HEAD"))
    private void debug$eat2(IFood food, CallbackInfo ci){
        SurvivorsDelight.LOGGER.info("EAT2");
    }

    @Inject(method = "eat(Lnet/dries007/tfc/common/capabilities/food/FoodData;)V", at = @At("HEAD"))
    private void debug$eat3(FoodData data, CallbackInfo ci){
        SurvivorsDelight.LOGGER.info("EAT3");
    }
}
