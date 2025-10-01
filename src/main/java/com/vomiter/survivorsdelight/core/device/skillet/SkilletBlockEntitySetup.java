package com.vomiter.survivorsdelight.core.device.skillet;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import com.vomiter.survivorsdelight.mixin.BlockEntityTypeAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.HashSet;

import static com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks.FARMER;

@EventBusSubscriber(modid = SurvivorsDelight.MODID)
public class SkilletBlockEntitySetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityType<SkilletBlockEntity> type = ModBlockEntityTypes.SKILLET.get();
            BlockEntityTypeAccessor acc = (BlockEntityTypeAccessor)type;
            HashSet<Block> validBlocks = new HashSet<>(acc.getValidBlocks());
            acc.setValidBlocks(validBlocks);
            SDSkilletBlocks.SKILLETS.values().forEach(ro -> validBlocks.add(ro.get()));
            validBlocks.add(FARMER.get());
        });
    }
}
