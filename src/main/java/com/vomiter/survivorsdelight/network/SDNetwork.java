// SDNetwork.java
package com.vomiter.survivorsdelight.network;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletDeflects;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import javax.annotation.Nullable;
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
                RLUtils.build(SurvivorsDelight.MODID, "main"),
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

    public record OpenPotFluidMenuC2SPacket(int containerId, BlockPos pos) {

        public static void encode(OpenPotFluidMenuC2SPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.containerId());
            buf.writeBlockPos(msg.pos());
        }

        public static OpenPotFluidMenuC2SPacket decode(FriendlyByteBuf buf) {
            int id = buf.readVarInt();
            BlockPos pos = buf.readBlockPos();
            return new OpenPotFluidMenuC2SPacket(id, pos);
        }

        public static void handle(OpenPotFluidMenuC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context c = ctx.get();
            c.enqueueWork(() -> {
                ServerPlayer sp = c.getSender();
                if (sp == null) return;

                // 防偽：確認封包的 containerId 與玩家當前開著的 menu 一致
                if (sp.containerMenu.containerId != msg.containerId()) return;

                MenuProvider provider = new SimpleMenuProvider(
                    (windowId, inv, player) -> new SDCookingPotFluidMenu(windowId, inv, msg.pos()),
                    Component.translatable("gui.survivorsdelight.pot.open_fluid")
                );
                NetworkHooks.openScreen(sp, provider, buf -> buf.writeBlockPos(msg.pos()));
            });
            c.setPacketHandled(true);
        }
    }

    public record OpenBackToFDPotC2SPacket() {
        public static void encode(OpenBackToFDPotC2SPacket pkt, FriendlyByteBuf buf) {}
        public static OpenBackToFDPotC2SPacket decode(FriendlyByteBuf buf) { return new OpenBackToFDPotC2SPacket(); }
        public static void handle(OpenBackToFDPotC2SPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            var c = ctx.get();
            c.enqueueWork(() -> {
                ServerPlayer sp = c.getSender();
                if (sp == null) return;
                if (sp.containerMenu instanceof SDCookingPotFluidMenu m) {
                    BlockPos pos = m.getPos(); // 請在 PotFluidMenu 實作 public BlockPos getPos()
                    BlockEntity be = sp.level().getBlockEntity(pos);
                    if (be instanceof CookingPotBlockEntity pot) {
                        NetworkHooks.openScreen(sp, pot, pos);
                    }
                }
            });
            c.setPacketHandled(true);
        }
    }

    public record PotFluidSyncS2CPacket(BlockPos pos,
                                        @Nullable ResourceLocation fluidKey,
                                        int amount
    ) {

        // ---------- Encode / Decode ----------

        public static void encode(PotFluidSyncS2CPacket pkt, FriendlyByteBuf buf) {
            buf.writeBlockPos(pkt.pos);
            // fluidKey 允許為 null，先寫個 boolean
            buf.writeBoolean(pkt.fluidKey != null);
            if (pkt.fluidKey != null) {
                buf.writeResourceLocation(pkt.fluidKey);
            }
            buf.writeVarInt(pkt.amount);
        }

        public static PotFluidSyncS2CPacket decode(FriendlyByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            ResourceLocation key = null;
            if (buf.readBoolean()) {
                key = buf.readResourceLocation();
            }
            int amount = buf.readVarInt();
            return new PotFluidSyncS2CPacket(pos, key, amount);
        }

        // ---------- Handle on client ----------

        public static void handle(PotFluidSyncS2CPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            var c = ctx.get();
            c.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null || mc.player == null) return;

                // 用 vanilla RegistryAccess 解析 ResourceLocation → Fluid
                FluidStack stack = FluidStack.EMPTY;
                if (pkt.fluidKey != null) {
                    Fluid f = mc.level.registryAccess()
                            .registryOrThrow(Registries.FLUID)
                            .get(pkt.fluidKey);
                    if (f != null) stack = new FluidStack(f, pkt.amount);
                }

                // 只更新當前開啟、且 pos 相符的 PotFluidMenu
                if (mc.player.containerMenu instanceof SDCookingPotFluidMenu menu
                        && pkt.pos.equals(menu.getPos())) {
                    menu.setClientFluid(stack);
                }
            });
            c.setPacketHandled(true);
        }
    }}
