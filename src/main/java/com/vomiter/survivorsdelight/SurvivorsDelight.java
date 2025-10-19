package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import com.vomiter.survivorsdelight.client.ClientForgeEventHandler;
import com.vomiter.survivorsdelight.client.screen.SDPotFluidScreen;
import com.vomiter.survivorsdelight.client.screen.SDCabinetScreen;
import com.vomiter.survivorsdelight.core.ForgeEventHandler;
import com.vomiter.survivorsdelight.core.device.cooking_pot.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.core.device.stove.StoveOvenCompat;
import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import com.vomiter.survivorsdelight.core.registry.SDBlocks;
import com.vomiter.survivorsdelight.core.registry.SDContainerTypes;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.LoadingModList;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {
    //TODO: add item of full chicken
    //TODO: add unroasted blocks
    //TODO: 1.6 - Add workhorse effect to horse feed


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
            SDNetwork.init();
            if (LoadingModList.get().getModFileById("firmalife") != null) {
                StoveOvenCompat.interactionRegister();
            }
        });
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(SDContainerTypes.CABINET.get(), SDCabinetScreen::new);
            MenuScreens.register(SDCookingPotFluidMenu.TYPE, SDPotFluidScreen::new);
        });
    }

    public void init(IEventBus modBus){


        SDFoodTraits.bootstrap();
        SDSkilletBlocks.BLOCKS.register(modBus);
        SDSkilletItems.ITEMS.register(modBus);
        SDSkilletPartItems.ITEMS.register(modBus);
        SDBlocks.BLOCKS.register(modBus);
        SDBlocks.BLOCK_ITEMS.register(modBus);
        SDBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
        SDContainerTypes.CONTAINERS.register(modBus);

        SDCreativeTab.TABS.register(modBus);
        ForgeEventHandler.init();

        modBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT){
            ClientForgeEventHandler.init();
            modBus.addListener(this::clientSetup);
        }


    }
}
