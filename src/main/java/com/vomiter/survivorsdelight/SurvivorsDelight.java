package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import com.vomiter.survivorsdelight.client.ClientForgeEventHandler;
import com.vomiter.survivorsdelight.client.SaladPredicates;
import com.vomiter.survivorsdelight.client.SandwichPredicates;
import com.vomiter.survivorsdelight.client.screen.SDCabinetScreen;
import com.vomiter.survivorsdelight.client.screen.SDPotFluidScreen;
import com.vomiter.survivorsdelight.core.ForgeEventHandler;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletModels;
import com.vomiter.survivorsdelight.core.device.skillet.itemcooking.ISkilletItemCookingData;
import com.vomiter.survivorsdelight.core.farming.RichSoilFarmlandBlockEntitySetup;
import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDContainerTypes;
import com.vomiter.survivorsdelight.core.registry.SDItemStackModifiers;
import com.vomiter.survivorsdelight.core.registry.SDRegistries;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletBlocks;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {
    //TODO: add aquaculture support
    //TODO: add tfc cs compat

    //TODO: add recipe for not food

    //TODO: add size to fd foods
    //TODO: add reasonable food tags
    //TODO: add food data
    //TODO: add washable tag for fd foods
    //TODO: check apple pie

    //TODO: add looting system (for ham)
    //TODO: make reasonable feast recipe
    //TODO: transfer manual recipes to datagen

    //TODO: another mod - Basket and storage blocks
    //TODO: another mod - Unroasted block and buildable feast
    //TODO: another mod - Beneath edition

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

    private void commonSetup(IEventBus modBus) {
        modBus.addListener(SDNetwork::onCommonSetup);
        modBus.addListener(RichSoilFarmlandBlockEntitySetup::onCommonSetup);
        modBus.addListener(SDSkilletBlocks.Compat::onCommonSetup);
        modBus.addListener(SDItemStackModifiers::onCommonSetUp);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(SDContainerTypes.CABINET.get(), SDCabinetScreen::new);
            MenuScreens.register(SDCookingPotFluidMenu.TYPE, SDPotFluidScreen::new);
            SandwichPredicates.addPredicate();
            SaladPredicates.addPredicate();
        });
    }

    public void init(IEventBus modBus){

        SDFoodTraits.bootstrap();

        SDRegistries.register(modBus);
        commonSetup(modBus);
        modBus.addListener((RegisterCapabilitiesEvent e) -> e.register(ISkilletItemCookingData.class));
        ForgeEventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT){
            ClientForgeEventHandler.init();
            modBus.addListener(this::clientSetup);
            modBus.addListener(SkilletModels::onModelRegister);
            modBus.addListener(SkilletModels::onModelBake);
        }
    }
}
