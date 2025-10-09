package com.vomiter.survivorsdelight.core;

import com.vomiter.survivorsdelight.core.device.skillet.itemcooking.SkilletCookingCap;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import com.vomiter.survivorsdelight.mixin.BlockEntityTypeAccessor;
import net.dries007.tfc.util.events.StartFireEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.HashSet;

import static com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks.FARMER;

public class ForgeEventHandler {
    public static void init(){
        final IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(ForgeEventHandler::onFireStart);
        bus.addListener(SkilletCookingCap::onClone);
    }

    public static void onFireStart(StartFireEvent event){
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();
        if(block instanceof StoveBlock stove){
            if(!state.getValue(StoveBlock.LIT)){
                level.setBlockAndUpdate(pos, state.setValue(StoveBlock.LIT, true));
                event.setCanceled(true);
            }
        }
    }
}
