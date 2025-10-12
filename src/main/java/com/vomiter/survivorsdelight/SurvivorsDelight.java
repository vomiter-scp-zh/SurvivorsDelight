package com.vomiter.survivorsdelight;

import com.mojang.logging.LogUtils;
import com.vomiter.survivorsdelight.client.ClientForgeEventHandler;
import com.vomiter.survivorsdelight.core.ForgeEventHandler;
import com.vomiter.survivorsdelight.core.device.skillet.itemcooking.SkilletCookingCap;
import com.vomiter.survivorsdelight.core.food.block.SDDecayingBlockEntityRegistry;
import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(SurvivorsDelight.MODID)
public class SurvivorsDelight {

    public static final String MODID = "survivorsdelight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public SurvivorsDelight(ModContainer mod, IEventBus bus) {
        common(bus);
        mod.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    public void common(IEventBus modBus){
        SDNetwork.register(modBus);

        SDFoodTraits.TRAITS.register(modBus);
        SDDecayingBlockEntityRegistry.register(modBus);
        SDSkilletBlocks.BLOCKS.register(modBus);
        SDSkilletItems.ITEMS.register(modBus);
        SDSkilletPartItems.ITEMS.register(modBus);

        SDCreativeTab.TABS.register(modBus);
        ForgeEventHandler.init();

        modBus.addListener(SkilletCookingCap::registerCaps);         // RegisterCapabilitiesEvent

        if (FMLEnvironment.dist == Dist.CLIENT){
            ClientOnly.init(modBus);
        }
    }

    static final class ClientOnly {
        static void init(IEventBus modBus) {
            ClientForgeEventHandler.init(modBus);
        }
    }
}
