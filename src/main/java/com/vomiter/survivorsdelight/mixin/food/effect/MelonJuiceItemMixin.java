package com.vomiter.survivorsdelight.mixin.food.effect;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.item.DrinkableItem;
import vectorwing.farmersdelight.common.item.MelonJuiceItem;

import java.util.Optional;

@Mixin(value = MelonJuiceItem.class, remap = false)
public abstract class MelonJuiceItemMixin extends DrinkableItem {

    MelonJuiceItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyConstant(method = "affectConsumer", constant = @Constant(floatValue = 2.0f))
    private float adjustHeal(float original){
        return 4.0f;
    }

    @Inject(method = "affectConsumer", at = @At("HEAD"))
    private void sdtfc$addNutrient(ItemStack stack, Level level, LivingEntity consumer, CallbackInfo ci){
        if(consumer instanceof Player player && player.getFoodData() instanceof TFCFoodData foodData){
            Optional.ofNullable(FoodCapability.get(stack)).ifPresent(foodData::eat);
        }
    }

}
