package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {
    public static final String MODID = "survivorsdelight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SurvivorsDelight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (!DatagenModLoader.isRunningDataGen()) {
        }
    }
}
