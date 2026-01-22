package com.vomiter.survivorsdelight.content.device.cooking_pot.fluid_handle;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public interface ICookingPotFluidAccess {
    IFluidTank sdtfc$getTank();
    ItemStackHandler sdtfc$getAuxInv();
    ItemStackHandler sdtfc$getInventory();
    void sdtfc$updateFluidIOSlots();
    void sdtfc$addPlayer(ServerPlayer player);
    void sdtfc$removePlayer(ServerPlayer player);

    default @Nullable IFluidHandler sd$getFluidHandler() {
        IFluidTank tank = sdtfc$getTank();
        return (tank instanceof IFluidHandler ih) ? ih : null;
    }


}
