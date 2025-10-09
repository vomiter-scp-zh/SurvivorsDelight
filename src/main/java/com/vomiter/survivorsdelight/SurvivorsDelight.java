package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import com.vomiter.survivorsdelight.client.ClientForgeEventHandler;
import com.vomiter.survivorsdelight.core.ForgeEventHandler;
import com.vomiter.survivorsdelight.core.device.stove.StoveOvenCompat;
import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.LoadingModList;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {
    //TODO: add item of full chicken
    //TODO: add unroasted blocks

    public static final String MODID = "survivorsdelight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public SurvivorsDelight(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        init(modBus);
    }

    public SurvivorsDelight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        init(modBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (LoadingModList.get().getModFileById("firmalife") != null) {
                StoveOvenCompat.interactionRegister();
            }
        });
    }

    public void init(IEventBus modBus){
        SDNetwork.init();

        SDFoodTraits.bootstrap();
        SDSkilletBlocks.BLOCKS.register(modBus);
        SDSkilletItems.ITEMS.register(modBus);
        SDSkilletPartItems.ITEMS.register(modBus);

        SDCreativeTab.TABS.register(modBus);
        ForgeEventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT){
            ClientForgeEventHandler.init();
        }

        modBus.addListener(this::commonSetup);
    }
}
