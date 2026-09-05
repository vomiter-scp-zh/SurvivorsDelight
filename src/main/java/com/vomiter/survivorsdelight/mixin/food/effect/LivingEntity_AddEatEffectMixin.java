package com.vomiter.survivorsdelight.mixin.food.effect;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vomiter.survivorsdelight.util.SDThreadLocals;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_AddEatEffectMixin {
    @Shadow
    protected ItemStack useItem;

    @WrapMethod(method = "completeUsingItem")
    private void sdtfc$completeUsingItem(Operation<Void> original){
        try {
            SDThreadLocals.finishUsedItem.set(useItem.copy());
            original.call();
        } finally {
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


    /*
    @Inject(method = "addEatEffect", at = @At("HEAD"), cancellable = true)
    private void sdtfc$handleFDFoodEffects(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfo ci){
        if(!FoodCapability.isRotten(stack)) return;
        if(!Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).getNamespace().equals(FarmersDelight.MODID)) return;
        FoodProperties foodProperties = stack.getFoodProperties(livingEntity);
        if(foodProperties == null) return;
        List<Pair<MobEffectInstance, Float>> effects = foodProperties.getEffects();
        effects.removeIf(pair -> pair.getFirst().getEffect().isBeneficial());
        for(Pair<MobEffectInstance, Float> pair : effects) {
            if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                //SurvivorsDelight.LOGGER.info("added effect:" + pair.getFirst().getDescriptionId());
                livingEntity.addEffect(new MobEffectInstance(pair.getFirst()));
            }
        }
        ci.cancel();
    }

     */
}
