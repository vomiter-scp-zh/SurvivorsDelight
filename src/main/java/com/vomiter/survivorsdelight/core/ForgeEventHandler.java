package com.vomiter.survivorsdelight.core;

import net.dries007.tfc.util.events.StartFireEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import vectorwing.farmersdelight.common.block.StoveBlock;

public class ForgeEventHandler {
    public static void init(){
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ForgeEventHandler::onFireStart);
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
