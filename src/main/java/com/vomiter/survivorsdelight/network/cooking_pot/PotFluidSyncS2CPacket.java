package com.vomiter.survivorsdelight.network.cooking_pot;

import com.vomiter.survivorsdelight.adapter.cooking_pot.fluid.SDCookingPotFluidMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public record PotFluidSyncS2CPacket(BlockPos pos,
                                    @Nullable ResourceLocation fluidKey,
                                    int amount
) {


    public static void encode(PotFluidSyncS2CPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
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


    public static void handle(PotFluidSyncS2CPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;

            FluidStack stack = FluidStack.EMPTY;
            if (pkt.fluidKey != null) {
                Fluid f = mc.level.registryAccess()
                        .registryOrThrow(Registries.FLUID)
                        .get(pkt.fluidKey);
                if (f != null) stack = new FluidStack(f, pkt.amount);
            }

            if (mc.player.containerMenu instanceof SDCookingPotFluidMenu menu
                    && pkt.pos.equals(menu.getPos())) {
                menu.setClientFluid(stack);
            }
        });
        c.setPacketHandled(true);
    }
}