// SDNetwork.java
package com.vomiter.survivorsdelight.network;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.common.device.skillet.SkilletDeflects;
import com.vomiter.survivorsdelight.network.cooking_pot.ClearCookingPotMealC2SPacket;
import com.vomiter.survivorsdelight.network.cooking_pot.OpenBackToFDPotC2SPacket;
import com.vomiter.survivorsdelight.network.cooking_pot.OpenPotFluidMenuC2SPacket;
import com.vomiter.survivorsdelight.network.cooking_pot.PotFluidSyncS2CPacket;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class SDNetwork {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;
    private static boolean initialized = false;

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SDNetwork::init); // 兩端都會跑，註冊 message handler
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        CHANNEL = NetworkRegistry.newSimpleChannel(
                SDUtils.RLUtils.build(SurvivorsDelight.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
        );

        int id = 0;

        CHANNEL.messageBuilder(SwingSkilletC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SwingSkilletC2S::encode)
                .decoder(SwingSkilletC2S::decode)
                .consumerMainThread(SwingSkilletC2S::handle)
                .add();

        CHANNEL.messageBuilder(OpenPotFluidMenuC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenPotFluidMenuC2SPacket::encode)
                .decoder(OpenPotFluidMenuC2SPacket::decode)
                .consumerMainThread(OpenPotFluidMenuC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenBackToFDPotC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenBackToFDPotC2SPacket::encode)
                .decoder(OpenBackToFDPotC2SPacket::decode)
                .consumerMainThread(OpenBackToFDPotC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(PotFluidSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PotFluidSyncS2CPacket::encode)
                .decoder(PotFluidSyncS2CPacket::decode)
                .consumerMainThread(PotFluidSyncS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(ClearCookingPotMealC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClearCookingPotMealC2SPacket::encode)
                .decoder(ClearCookingPotMealC2SPacket::decode)
                .consumerMainThread(ClearCookingPotMealC2SPacket::handle)
                .add();
    }

    private static NetworkDirection OptionalDirection(NetworkDirection dir) { return dir; }
    private static NetworkDirection ServerOnly() { return NetworkDirection.PLAY_TO_SERVER; }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    /* -------------------- Packets -------------------- */

    public record SwingSkilletC2S() {
        public static void encode(SwingSkilletC2S pkt, FriendlyByteBuf buf) {}
        public static SwingSkilletC2S decode(FriendlyByteBuf buf) { return new SwingSkilletC2S(); }
        public static void handle(SwingSkilletC2S pkt, Supplier<NetworkEvent.Context> ctx) {
            var c = ctx.get();
            c.enqueueWork(() -> {
                var sp = c.getSender();
                if (sp != null) SkilletDeflects.performSweepDeflect(sp);
            });
            c.setPacketHandled(true);
        }
    }






}
