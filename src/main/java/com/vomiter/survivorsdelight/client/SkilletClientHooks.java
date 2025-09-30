package com.vomiter.survivorsdelight.client;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SDSkilletItem;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SurvivorsDelight.MODID, value = Dist.CLIENT)
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
            SDNetwork.SwingSkilletC2S.sendToServer();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (isHoldingAttackableSkillet(e.getEntity())) {
            SDNetwork.SwingSkilletC2S.sendToServer();
        }
    }
}
