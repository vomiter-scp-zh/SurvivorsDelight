package com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public interface ICookingPotFluidAccess {
    IFluidTank sdtfc$getTank();
    ItemStackHandler sdtfc$getAuxInv();
    ItemStackHandler sdtfc$getInventory();
    void sdtfc$updateFluidIOSlots();
    void sdtfc$addPlayer(Player player);
    void sdtfc$removePlayer(Player player);

    default @Nullable IFluidHandler sd$getFluidHandler() {
        IFluidTank tank = sdtfc$getTank();
        return (tank instanceof IFluidHandler ih) ? ih : null;
    }


}
