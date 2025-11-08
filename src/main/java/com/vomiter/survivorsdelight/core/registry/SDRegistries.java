package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class SDRegistries {
    public static void register(IEventBus modBus){
        SDSkilletBlocks.BLOCKS.register(modBus);
        SDSkilletItems.ITEMS.register(modBus);
        SDSkilletPartItems.ITEMS.register(modBus);
        SDBlocks.BLOCKS.register(modBus);
        SDBlocks.BLOCK_ITEMS.register(modBus);
        SDItems.ITEMS.register(modBus);
        SDBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
        SDContainerTypes.CONTAINERS.register(modBus);
        SDCreativeTab.TABS.register(modBus);
        SDRecipeSerializers.SERIALIZERS.register(modBus);
    }
}
