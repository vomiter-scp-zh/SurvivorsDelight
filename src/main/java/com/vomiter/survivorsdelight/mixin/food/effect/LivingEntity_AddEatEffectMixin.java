package com.vomiter.survivorsdelight.mixin.food.effect;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_AddEatEffectMixin {
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
}
