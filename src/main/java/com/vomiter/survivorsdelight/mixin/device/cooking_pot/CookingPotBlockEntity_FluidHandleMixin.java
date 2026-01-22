package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.vomiter.survivorsdelight.content.device.cooking_pot.ICookingPotHasChanged;
import com.vomiter.survivorsdelight.content.device.cooking_pot.bridge.ICookingPotRecipeBridge;
import com.vomiter.survivorsdelight.content.device.cooking_pot.fluid_handle.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.registry.recipe.SDCookingPotRecipe;
import com.vomiter.survivorsdelight.network.SDNetwork;
import com.vomiter.survivorsdelight.network.cooking_pot.PotFluidSyncS2CPayload;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
This mixin handles how fluid works in cooking pot and how fluid requiring recipe is handled.
For TFC pot recipe bridge, please check CookingPotBlockEntity_PotRecipeBridgeMixin.java
 */
@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntity_FluidHandleMixin extends BlockEntity implements ICookingPotFluidAccess {

    public CookingPotBlockEntity_FluidHandleMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ====== Players To Send Pkt =======
    @Unique private final List<ServerPlayer> sdtfc$players = new ArrayList<>();
    @Override public void sdtfc$addPlayer(ServerPlayer player){
        this.sdtfc$players.add(player);
    }
    @Override public void sdtfc$removePlayer(ServerPlayer player){
        this.sdtfc$players.remove(player);
    }
    // ====== Fluid Tank======
    @Unique private final FluidTank sdtfc$fluidTank = new FluidTank(4000) {
        @Override protected void onContentsChanged() { sdtfc$setChangedAndSync(); }
    };
    @Unique private final IFluidHandler sdtfc$fluidCap = sdtfc$fluidTank;
    @Unique @Override public FluidTank sdtfc$getTank() { return sdtfc$fluidTank; }
    // ====== Item Slot for buckets ======
    @Unique private final ItemStackHandler sdtfc$auxInv = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { sdtfc$setChangedAndSync(); }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // slot 0 = 輸入（允許桶/可裝流體的容器）；slot 1 = 輸出（拒收）
            if (slot == 1) return false;
            return stack.getItem() instanceof BucketItem
                    || stack.getItem() instanceof FluidContainerItem
                    || FluidUtil.getFluidHandler(stack).isPresent();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    @Unique @Override public ItemStackHandler sdtfc$getAuxInv() { return sdtfc$auxInv; }

    // ====== TFC barrel-like fluid input/output with fluid container items ======
    @Inject(method = "cookingTick", at = @At("HEAD"))
    private static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot, CallbackInfo ci) {
        if (level.isClientSide) return;
        var self = (ICookingPotFluidAccess)cookingPot;
        self.sdtfc$updateFluidIOSlots();
    }

    @Unique
    public void sdtfc$updateFluidIOSlots() {
        assert level != null;
        var self = (ICookingPotFluidAccess)this;
        var tank = self.sdtfc$getTank();
        var inventory = self.sdtfc$getAuxInv();
        final ItemStack input = self.sdtfc$getAuxInv().getStackInSlot(0);
        if (!input.isEmpty() && self.sdtfc$getAuxInv().getStackInSlot(1).isEmpty()) //only works when the input is not empty and output is empty
        {
            //Basically copied from barrel
            FluidHelpers.transferBetweenBlockEntityAndItem(input, this, level, worldPosition, (newOriginalStack, newContainerStack) -> {
                ((ICookingPotHasChanged)this).sdtfc$setChanged(true);
                if(this instanceof ICookingPotRecipeBridge bridgePot) bridgePot.sdtfc$setCachedBridge(null); //to make it match pot recipe again
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

                sdtfc$players.forEach(player -> {
                    if(player.distanceToSqr(Vec3.atCenterOf(worldPosition)) >= 64.0) sdtfc$removePlayer(player);
                    else{
                        SDNetwork.sendToClient(
                                player,
                                new PotFluidSyncS2CPayload(getBlockPos(), Optional.of(BuiltInRegistries.FLUID.getKey(tank.getFluid().getFluid())), tank.getFluidAmount())
                        );

                    }
                });
            });
        }
    }

    // ====== Drain fluid ingredient upon finish ======
    @Inject(
            method = "processCooking",
            at = @At("RETURN"),
            remap = false
    )
    private void sdtfc$drainFluidWhenCooked(
            RecipeHolder<CookingPotRecipe> recipe,
            CookingPotBlockEntity cookingPot,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()) return; // 沒真的做出成品就不扣
        if (level == null) return;
        if(!(recipe.value() instanceof SDCookingPotRecipe sdCookingPotRecipe)) return;
        var acc = (ICookingPotFluidAccess)this;
        acc.sdtfc$getTank().drain(sdCookingPotRecipe.getFluidAmountMb(), IFluidHandler.FluidAction.EXECUTE);
    }

    // ====== 方塊破壞時清除 Caps =======
    @Inject(method = "setRemoved", at = @At("TAIL"), remap = true)
    private void sdtfc$setRemoved(CallbackInfo ci) {
    }

    // ====== NBT：載入 / 儲存 ======
    @Inject(method = "loadAdditional", at = @At("TAIL"), remap = true)
    private void sdtfc$loadExtraData(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        if(level == null) return;
        if (compound.contains("survivorsdelight:pot_tank", Tag.TAG_COMPOUND)) {
            sdtfc$fluidTank.readFromNBT(level.registryAccess() ,compound.getCompound("survivorsdelight:pot_tank"));
        }
        if (compound.contains("survivorsdelight:aux_inv", Tag.TAG_COMPOUND)) {
            sdtfc$auxInv.deserializeNBT(level.registryAccess(), compound.getCompound("survivorsdelight:aux_inv"));
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = true)
    private void sdtfc$saveExtraData(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        if(level == null) return;
        CompoundTag tank = new CompoundTag();
        sdtfc$fluidTank.writeToNBT(level.registryAccess(), tank);
        compound.put("survivorsdelight:pot_tank", tank);

        CompoundTag aux = sdtfc$auxInv.serializeNBT(level.registryAccess());
        compound.put("survivorsdelight:aux_inv", aux);
    }

    // ====== 同步：update tag ======
    @Inject(method = "getUpdateTag", at = @At("RETURN"), cancellable = true, remap = true)
    private void sdtfc$appendExtraToUpdateTag(CallbackInfoReturnable<CompoundTag> cir) {
        if(level == null) return;
        CompoundTag out = cir.getReturnValue();

        CompoundTag tank = new CompoundTag();
        sdtfc$fluidTank.writeToNBT(level.registryAccess(), tank);
        out.put("survivorsdelight:pot_tank", tank);

        CompoundTag aux = sdtfc$auxInv.serializeNBT(level.registryAccess());
        out.put("survivorsdelight:aux_inv", aux);

        cir.setReturnValue(out);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        if(level == null) return;
        if (tag.contains("survivorsdelight:pot_tank", Tag.TAG_COMPOUND)) {
            sdtfc$fluidTank.readFromNBT(registries, tag.getCompound("survivorsdelight:pot_tank"));
        }
        if (tag.contains("survivorsdelight:aux_inv", Tag.TAG_COMPOUND)) {
            sdtfc$auxInv.deserializeNBT(registries, tag.getCompound("survivorsdelight:aux_inv"));
        }
    }

    @Unique
    private void sdtfc$setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

}
