package com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle;

import com.vomiter.survivorsdelight.adapter.cooking_pot.ICookingPotHasChanged;
import com.vomiter.survivorsdelight.network.SDNetwork;
import com.vomiter.survivorsdelight.network.cooking_pot.PotFluidSyncS2CPayload;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingPotFluidIO {
    public static final String KEY_TANK = "survivorsdelight:pot_tank";
    public static final String KEY_AUX  = "survivorsdelight:aux_inv";

    public static void updateFluidIOSlots(CookingPotBlockEntity cookingPot) {
        var level = cookingPot.getLevel();
        if(level == null) return;
        var fluidAccess = (ICookingPotFluidAccess)cookingPot;
        var tank = fluidAccess.sdtfc$getTank();
        var inventory = fluidAccess.sdtfc$getAuxInv();
        final ItemStack input = fluidAccess.sdtfc$getAuxInv().getStackInSlot(0);
        if (!input.isEmpty() && fluidAccess.sdtfc$getAuxInv().getStackInSlot(1).isEmpty()) //only works when the input is not empty and output is empty
        {
            //Basically copied from barrel
            FluidHelpers.transferBetweenBlockEntityAndItem(input, cookingPot, level, cookingPot.getBlockPos(), (newOriginalStack, newContainerStack) -> {
                ((ICookingPotHasChanged)cookingPot).sdtfc$setChanged(true);
                //if(this instanceof LEGACY_ICookingPotRecipeBridge bridgePot) bridgePot.sdtfc$setCachedBridge(null);
                if (newContainerStack.isEmpty())
                {
                    // No new container was produced, so shove the first stack in the output, and clear the input
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    inventory.setStackInSlot(1, newOriginalStack);
                }
                else
                {
                    // We produced a new container - this will be the 'filled', so we need to shove *that* in the output
                    inventory.setStackInSlot(0, newOriginalStack);
                    inventory.setStackInSlot(1, newContainerStack);
                }
                if(level.isClientSide) return;

                List<ServerPlayer> toRemove = new ArrayList<>();
                fluidAccess.sdtfc$getPlayer().forEach(player -> {
                    if(player.distanceToSqr(Vec3.atCenterOf(cookingPot.getBlockPos())) >= 64.0) toRemove.add(player);
                    else{
                        SDNetwork.sendToClient(
                                player,
                                new PotFluidSyncS2CPayload(cookingPot.getBlockPos(), Optional.of(BuiltInRegistries.FLUID.getKey(tank.getFluid().getFluid())), tank.getFluidAmount())
                        );
                    }
                });
                toRemove.forEach(fluidAccess::sdtfc$removePlayer);
            });
        }
    }

    public static void load(Level level, CompoundTag compound, ICookingPotFluidAccess access) {
        if (compound.contains(KEY_TANK, Tag.TAG_COMPOUND)) {
            var fluidHandler = access.sdtfc$getTank();
            if(fluidHandler instanceof FluidTank fluidTank){
                fluidTank.readFromNBT(level.registryAccess(), compound.getCompound(KEY_TANK));
            }
        }
        if (compound.contains(KEY_AUX, Tag.TAG_COMPOUND)) {
            access.sdtfc$getAuxInv().deserializeNBT(level.registryAccess(), compound.getCompound(KEY_AUX));
        }
    }

    public static void save(Level level, CompoundTag compound, ICookingPotFluidAccess access) {
        CompoundTag tank = new CompoundTag();
        var fluidHandler = access.sdtfc$getTank();
        if(fluidHandler instanceof FluidTank fluidTank){
            fluidTank.writeToNBT(level.registryAccess(), tank);
        }
        compound.put(KEY_TANK, tank);
        CompoundTag aux = access.sdtfc$getAuxInv().serializeNBT(level.registryAccess());
        compound.put(KEY_AUX, aux);
    }

    public static void appendToUpdateTag(Level level, CompoundTag out, ICookingPotFluidAccess access) {
        save(level, out, access);
    }

    public static void handleUpdateTag(HolderLookup.Provider registries, CompoundTag tag, ICookingPotFluidAccess access) {
        if (tag.contains(KEY_TANK, Tag.TAG_COMPOUND)) {
            var fluidHandler = access.sdtfc$getTank();
            if(fluidHandler instanceof FluidTank fluidTank){
                fluidTank.readFromNBT(registries, tag.getCompound(KEY_TANK));
            }
        }
        if (tag.contains(KEY_AUX, Tag.TAG_COMPOUND)) {
            access.sdtfc$getAuxInv().deserializeNBT(registries, tag.getCompound(KEY_AUX));
        }
    }
}
