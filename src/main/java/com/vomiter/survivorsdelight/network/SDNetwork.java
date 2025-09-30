// SDNetwork.java
package com.vomiter.survivorsdelight.network;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletDeflects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

public final class SDNetwork {
    public static final String PROTOCOL = "1";

    public static void register(IEventBus modBus) {
        modBus.addListener(SDNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(SurvivorsDelight.MODID).versioned(PROTOCOL);
        reg.playToServer(SwingSkilletC2S.TYPE, SwingSkilletC2S.CODEC, SDNetwork::handleSwingSkilletC2S);
    }

    private static void handleSwingSkilletC2S(SwingSkilletC2S pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sp = (ServerPlayer) ctx.player();
            SkilletDeflects.performSweepDeflect(sp);
        });
    }

    public record SwingSkilletC2S() implements CustomPacketPayload {
        public static final Type<SwingSkilletC2S> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(SurvivorsDelight.MODID, "swing_skillet"));

        public static final StreamCodec<FriendlyByteBuf, SwingSkilletC2S> CODEC =
                StreamCodec.unit(new SwingSkilletC2S());

        @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

        public static void sendToServer() {
            PacketDistributor.sendToServer(new SwingSkilletC2S());
        }
    }
}
