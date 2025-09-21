package com.vomiter.survivorsdelight.client;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SDSkilletItem;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkilletClientHooks {

    private static boolean isHoldingAttackableSkillet(Player player) {
        if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SDSkilletItem sdSkilletItem){
            return sdSkilletItem.canAttack();
        }
        return false;
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty e) {
        if (isHoldingAttackableSkillet(e.getEntity())) {
            SDNetwork.CHANNEL.sendToServer(new SDNetwork.SwingSkilletC2S());
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (isHoldingAttackableSkillet(e.getEntity())) {
            SDNetwork.CHANNEL.sendToServer(new SDNetwork.SwingSkilletC2S());
        }
    }
}
