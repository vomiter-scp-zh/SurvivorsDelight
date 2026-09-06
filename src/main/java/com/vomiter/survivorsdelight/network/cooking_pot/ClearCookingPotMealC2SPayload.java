package com.vomiter.survivorsdelight.network.cooking_pot;

import com.vomiter.survivorsdelight.adapter.cooking_pot.bridge.ICookingPotTFCRecipeBridge;
import com.vomiter.survivorsdelight.adapter.cooking_pot.dynamic.ICookingPotCalcDynamic;
import com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

public record ClearCookingPotMealC2SPayload(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClearCookingPotMealC2SPayload> TYPE =
            new Type<>(SDUtils.RLUtils.build("clear_pot_meal"));

    public static final StreamCodec<FriendlyByteBuf, ClearCookingPotMealC2SPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ClearCookingPotMealC2SPayload::pos,
                    ClearCookingPotMealC2SPayload::new
            );

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    private static boolean isAndConsumeWaterBucket(ItemStack stack) {
        IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null && handler.getTanks() > 0
                && handler.getFluidInTank(0).getFluid().isSame(Fluids.WATER)
                && handler.getFluidInTank(0).getAmount() >= 1000) {
            handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    public static void handle(ClearCookingPotMealC2SPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var sp = ctx.player();
            if (sp == null) return;

            CookingPotBlockEntity pot;
            if (sp.containerMenu instanceof CookingPotMenu fdMenu){
                pot = fdMenu.blockEntity;
            } else if (sp.containerMenu instanceof SDCookingPotFluidMenu sdMenu) {
                pot = sdMenu.getBlockEntity();
            } else {
                return;
            }

            if (!pot.getBlockPos().equals(msg.pos())) return;
            ItemStack meal = pot.getMeal();
            if (meal.isEmpty()) return;

            ItemStack carried = sp.containerMenu.getCarried();
            if (!isAndConsumeWaterBucket(carried)) return;

            meal.setCount(0);
            ((ICookingPotCalcDynamic) pot).sdtfc$setCachedDynamic(ItemStack.EMPTY);
            ((ICookingPotTFCRecipeBridge) pot).sdtfc$setBridgeCached(null);
            pot.setChanged();
            sp.containerMenu.broadcastChanges();
        });
    }
}
