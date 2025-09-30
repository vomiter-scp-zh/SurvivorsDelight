package com.vomiter.survivorsdelight;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SurvivorsDelight.MODID)
public class Config {
    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        public final ModConfigSpec.IntValue skilletSlotNumber;
        public final ModConfigSpec.IntValue richSoilGrowthBoostTick;
        public final ModConfigSpec.IntValue richSoilFarmlandTemperatureExpansion;
        public final ModConfigSpec.IntValue richSoilFarmlandHydrationExpansion;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("general");

            skilletSlotNumber = builder
                    .comment("How many items can be put into skillet block at once.")
                    .defineInRange("skilletSlotNumber", 8, 1, 32);

            richSoilGrowthBoostTick = builder
                    .comment("How many ticks rich soil should boost the growth of the block above it.")
                    .defineInRange("richSoilGrowthBoostTick", 2400, 0, 24000 * 10);

            richSoilFarmlandTemperatureExpansion = builder
                    .comment("How many degrees of temperature deviated from usual range is allowed for crops planted on rich soil farmlands to grow.")
                    .defineInRange("richSoilFarmlandTemperatureExpansion", 5, 0, 100);

            richSoilFarmlandHydrationExpansion = builder
                    .comment("How many percentile of hydration deviated from usual range is allowed for crops planted on rich soil farmlands to grow.")
                    .defineInRange("richSoilFarmlandHydrationExpansion", 5, 0, 100);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading e) {
        if (e.getConfig().getSpec() == COMMON_SPEC) {
            SurvivorsDelight.LOGGER.info("SurvivorsDelight Config Loaded: {}", e.getConfig().getFileName());
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading e) {
        if (e.getConfig().getSpec() == COMMON_SPEC) {
            SurvivorsDelight.LOGGER.info("SurvivorsDelight Config Reloaded: {}", e.getConfig().getFileName());
        }
    }
}
