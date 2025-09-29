package com.vomiter.survivorsdelight;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue skilletSlotNumber;
        public final ForgeConfigSpec.IntValue richSoilGrowthBoostTick;
        public final ForgeConfigSpec.IntValue richSoilFarmlandTemperatureExpansion;
        public final ForgeConfigSpec.IntValue richSoilFarmlandHydrationExpansion;

        public Common(ForgeConfigSpec.Builder builder) {
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
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getSpec() == COMMON_SPEC) {
            SurvivorsDelight.LOGGER.info("SurvivorsDelight Config Loaded: {}", configEvent.getConfig().getFileName());
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getSpec() == COMMON_SPEC) {
            SurvivorsDelight.LOGGER.info("SurvivorsDelight Config Reloaded: {}", configEvent.getConfig().getFileName());
        }
    }
}
