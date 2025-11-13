package com.vomiter.survivorsdelight.core.farming;

import com.vomiter.survivorsdelight.SDConfig;
import net.dries007.tfc.util.climate.ClimateRange;
import net.minecraft.resources.ResourceLocation;

public class ClimateRangeBuilder {
    public static ClimateRange deriveLoose(ClimateRange original) {
        var entry = original;
        var src   = entry != null ? entry : ClimateRange.NOOP;
        int tempExp = SDConfig.COMMON.richSoilFarmlandTemperatureExpansion.get();
        int hydraExp = SDConfig.COMMON.richSoilFarmlandHydrationExpansion.get();
        return new ClimateRange(
                Math.max(0, src.getMinHydration(false) - hydraExp),
                Math.min(100, src.getMaxHydration(false) + hydraExp),
                src.hydrationWiggleRange(),
                src.getMinTemperature(false) - tempExp,
                src.getMaxTemperature(false) + tempExp,
                src.temperatureWiggleRange()
        );
    }

    public static ClimateRange deriveLoose(ResourceLocation baseId) {
        var entry = ClimateRange.MANAGER.get(baseId);
        var src   = entry != null ? entry : ClimateRange.NOOP;
        int tempExp = SDConfig.COMMON.richSoilFarmlandTemperatureExpansion.get();
        int hydraExp = SDConfig.COMMON.richSoilFarmlandHydrationExpansion.get();
        return new ClimateRange(
                Math.max(0, src.getMinHydration(false) - hydraExp),
                Math.min(100, src.getMaxHydration(false) + hydraExp),
                src.hydrationWiggleRange(),
                src.getMinTemperature(false) - tempExp,
                src.getMaxTemperature(false) + tempExp,
                src.temperatureWiggleRange()
        );
    }
}
