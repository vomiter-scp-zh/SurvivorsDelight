package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {
    public static final String MODID = "survivorsdelight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public SurvivorsDelight(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        common(modBus);
    }

    public SurvivorsDelight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        common(modBus);
    }

    public void common(IEventBus modBus){
        SDNetwork.init();

        SDFoodTraits.bootstrap();
        SDSkilletBlocks.BLOCKS.register(modBus);
        SDSkilletItems.ITEMS.register(modBus);
        SDSkilletPartItems.ITEMS.register(modBus);

        SDCreativeTab.TABS.register(modBus);

    }
}
