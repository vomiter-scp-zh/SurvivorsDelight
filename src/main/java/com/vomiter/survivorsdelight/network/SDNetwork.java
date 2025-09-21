// SDNetwork.java
package com.vomiter.survivorsdelight.network;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletDeflects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class SDNetwork {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL; // 不要在這裡 new

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(SurvivorsDelight.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
        );
        int id = 0;
        CHANNEL.registerMessage(id++, SwingSkilletC2S.class,
                SwingSkilletC2S::encode, SwingSkilletC2S::decode, SwingSkilletC2S::handle);
    }

    public record SwingSkilletC2S() {
        public static void encode(SwingSkilletC2S pkt, net.minecraft.network.FriendlyByteBuf buf) {}
        public static SwingSkilletC2S decode(net.minecraft.network.FriendlyByteBuf buf) { return new SwingSkilletC2S(); }
        public static void handle(SwingSkilletC2S pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var sp = ctx.get().getSender();
                if (sp != null) SkilletDeflects.performSweepDeflect(sp);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
