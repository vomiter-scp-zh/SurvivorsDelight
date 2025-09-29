package com.vomiter.survivorsdelight.mixin.farming.farmland;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.Config;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@Mixin(value = FarmlandBlock.class, remap = false)
public class FarmlandBlock_RichSoilTooltip {
    @ModifyExpressionValue(
            method = "getHydrationTooltip(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;ZI)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;checkHydration(IZ)Lnet/dries007/tfc/util/climate/ClimateRange$Result;")
    )
    private static ClimateRange.Result expandHydrationRange(
            ClimateRange.Result original,
            @Local(argsOnly = true)LevelAccessor levelAccessor,
            @Local(argsOnly = true)BlockPos blockPos,
            @Local(argsOnly = true)ClimateRange climateRange,
            @Local(argsOnly = true) int hydration
            )
    {
        if(levelAccessor.getBlockState(blockPos).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int hydrationExpansion = Config.COMMON.richSoilFarmlandHydrationExpansion.get();
            int maxHydration = climateRange.getMaxHydration(false) + hydrationExpansion;
            int minHydration = climateRange.getMinHydration(false) - hydrationExpansion;
            boolean allowGrowth = maxHydration > hydration && minHydration < hydration;
            if(allowGrowth) return ClimateRange.Result.VALID;
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "getHydrationTooltip(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;ZI)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;getMaxHydration(Z)I")
    )
    private static int textMaxHydrationRange(
            int original,
            @Local(argsOnly = true)LevelAccessor levelAccessor,
            @Local(argsOnly = true)BlockPos blockPos,
            @Local(argsOnly = true)ClimateRange climateRange,
            @Local(argsOnly = true) int hydration
    )
    {
        if(levelAccessor.getBlockState(blockPos).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int hydrationExpansion = Config.COMMON.richSoilFarmlandHydrationExpansion.get();
            return climateRange.getMaxHydration(false) + hydrationExpansion;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getHydrationTooltip(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;ZI)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;getMinHydration(Z)I")
    )
    private static int textMinHydrationRange(
            int original,
            @Local(argsOnly = true)LevelAccessor levelAccessor,
            @Local(argsOnly = true)BlockPos blockPos,
            @Local(argsOnly = true)ClimateRange climateRange,
            @Local(argsOnly = true) int hydration
    )
    {
        if(levelAccessor.getBlockState(blockPos).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int hydrationExpansion = Config.COMMON.richSoilFarmlandHydrationExpansion.get();
            return climateRange.getMinHydration(false) - hydrationExpansion;
        }
        return original;
    }



    @ModifyExpressionValue(
            method = "getTemperatureTooltip(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;FZLjava/lang/String;)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;checkTemperature(FZ)Lnet/dries007/tfc/util/climate/ClimateRange$Result;")
    )
    private static ClimateRange.Result expandTemperatureRange(
            ClimateRange.Result original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true)BlockPos blockPos,
            @Local(argsOnly = true)ClimateRange climateRange,
            @Local(argsOnly = true) float temperature
    ){
        if(level.getBlockState(blockPos.below()).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int tempExpansion = Config.COMMON.richSoilFarmlandTemperatureExpansion.get();
            float maxTemp = climateRange.getMaxTemperature(false) + (float)tempExpansion;
            float minTemp = climateRange.getMinTemperature(false) - (float)tempExpansion;
            boolean allowGrowth = maxTemp > temperature && minTemp < temperature;
            if(allowGrowth) return ClimateRange.Result.VALID;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getTemperatureTooltip(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;FZLjava/lang/String;)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;getMaxTemperature(Z)F")
    )
    private static float textMaxTemperatureRange(
            float original, @Local(argsOnly = true) Level level, @Local(argsOnly = true)BlockPos blockPos, @Local(argsOnly = true)ClimateRange climateRange, @Local(argsOnly = true) float temperature
    ){
        if(level.getBlockState(blockPos.below()).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int tempExpansion = Config.COMMON.richSoilFarmlandTemperatureExpansion.get();
            return climateRange.getMaxTemperature(false) + (float)tempExpansion;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getTemperatureTooltip(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/dries007/tfc/util/climate/ClimateRange;FZLjava/lang/String;)Lnet/minecraft/network/chat/Component;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/climate/ClimateRange;getMaxTemperature(Z)F")
    )
    private static float textMinTemperatureRange(
            float original, @Local(argsOnly = true) Level level, @Local(argsOnly = true)BlockPos blockPos, @Local(argsOnly = true)ClimateRange climateRange, @Local(argsOnly = true) float temperature
    ){
        if(level.getBlockState(blockPos.below()).is(ModBlocks.RICH_SOIL_FARMLAND.get())){
            int tempExpansion = Config.COMMON.richSoilFarmlandTemperatureExpansion.get();
            return climateRange.getMinTemperature(false) - (float)tempExpansion;
        }
        return original;
    }


}
