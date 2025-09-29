package com.vomiter.survivorsdelight.mixin.food;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TFCFoodData.class)
public class TFCFoodData_ThirstNourishmentMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/capabilities/food/TFCFoodData;getThirstModifier(Lnet/minecraft/world/entity/player/Player;)F"))
    private float preventThirstLoss(
            float original,
            @Local(argsOnly = true) Player player
    ){
        /*
        if(player.hasEffect(ModEffects.NOURISHMENT.get())){
            return 0f;
        }
         */
        return original;
    }
}
