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

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            skilletSlotNumber = builder
                    .comment("How many items can be put into skillet block at once.")
                    .defineInRange("skilletSlotNumber", 8, 1, 32);

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
