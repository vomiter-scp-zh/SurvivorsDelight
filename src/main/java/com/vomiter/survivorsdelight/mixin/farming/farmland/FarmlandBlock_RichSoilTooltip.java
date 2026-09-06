package com.vomiter.survivorsdelight.mixin.farming.farmland;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.common.farming.ClimateRangeBuilder;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// NOTE:
// pos may refer to crop position in some call paths, hence pos.below() is intentional.

@Mixin(value = FarmlandBlock.class, remap = false)
public class FarmlandBlock_RichSoilTooltip {

    @ModifyVariable(
            method = "getHydrationTooltip(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;Z)Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private static ClimateRange expandHydration(
            ClimateRange value,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        if (level.getBlockState(pos).is(SDTags.BlockTags.FARMERS_FARMLAND)) {
            return ClimateRangeBuilder.deriveLoose(value);
        }
        return value;
    }
}
