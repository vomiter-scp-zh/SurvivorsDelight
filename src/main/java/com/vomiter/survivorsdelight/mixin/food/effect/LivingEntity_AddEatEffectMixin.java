package com.vomiter.survivorsdelight.mixin.food.effect;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vomiter.survivorsdelight.util.SDThreadLocals;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_AddEatEffectMixin {
    @Shadow
    protected ItemStack useItem;

    @WrapMethod(method = "completeUsingItem")
    private void sdtfc$completeUsingItem(Operation<Void> original){
        try {
            SDThreadLocals.finishUsedItem.set(useItem.copy());
            if (FoodCapability.get(useItem) != null && (Object)this instanceof Player){
                SDThreadLocals.shouldApplyEating.set(true);
            }
            original.call();
        } finally {
            if (SDThreadLocals.shouldApplyEating.get()){
                var foodItem = SDThreadLocals.finishUsedItem.get();
                if ((Object)this instanceof Player player){
                    player.eat(player.level(), foodItem);
                }
            }

            SDThreadLocals.finishUsedItem.remove();
        }
    }

    @WrapMethod(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
    private boolean sdtfc$addEffect(MobEffectInstance mobEffectInstance, Entity p_147209_, Operation<Boolean> original){
        if (mobEffectInstance.getEffect().isBeneficial()){
            ItemStack stack = SDThreadLocals.finishUsedItem.get();
            if (!stack.isEmpty()){
                IFood food = FoodCapability.get(stack);
                if (food != null && food.isRotten()){
                    return false;
                }
            }
        }
        return original.call(mobEffectInstance, p_147209_);
    }

    @WrapMethod(method = "heal")
    private void sdtfc$heal(float p_21116_, Operation<Void> original){
        ItemStack stack = SDThreadLocals.finishUsedItem.get();
        if (!stack.isEmpty()){
            IFood food = FoodCapability.get(stack);
            if (food != null && food.isRotten()){
                return;
            }
        }
        original.call(p_21116_);
    }
}
