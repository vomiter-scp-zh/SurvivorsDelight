package com.vomiter.survivorsdelight.mixin.farming.farmland;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.Config;
import net.dries007.tfc.common.blocks.crop.CropHelpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@Mixin(value = CropHelpers.class, remap = false)
public class CropHelper_RSFarmlandBuff {
    @ModifyVariable(method = "growthTickStep", ordinal = 0, at = @At("STORE"))
    private static boolean expandClimateRange(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos,
            @Local ClimateRange climateRange,
            @Local(ordinal = 0) int startHydration,
            @Local(ordinal = 1) int endHydration,
            @Local(ordinal = 0) float startTemp,
            @Local(ordinal = 1) float endTemp
            ){
        if(!original && level.getBlockState(pos.below()).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int hydrationExpansion = Config.COMMON.richSoilFarmlandHydrationExpansion.get();
            int tempExpansion = Config.COMMON.richSoilFarmlandTemperatureExpansion.get();
            int maxHydration = climateRange.getMaxHydration(false) + hydrationExpansion;
            int minHydration = climateRange.getMinHydration(false) - hydrationExpansion;
            float maxTemp = climateRange.getMaxTemperature(false) + tempExpansion;
            float minTemp = climateRange.getMinTemperature(false) - tempExpansion;
            return (
                    maxHydration > startHydration
                    && minHydration < startHydration
                    && maxHydration > endHydration
                    && minHydration < endHydration
                    && maxTemp > startTemp
                    && minTemp < startTemp
                    && maxTemp > endTemp
                    && minTemp < endTemp
                    );
        }
        return original;
    }
}
