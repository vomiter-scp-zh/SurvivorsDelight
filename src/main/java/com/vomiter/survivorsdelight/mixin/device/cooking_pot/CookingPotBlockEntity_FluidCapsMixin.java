package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.vomiter.survivorsdelight.core.device.cooking_pot.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntity_FluidCapsMixin extends BlockEntity implements ICookingPotFluidAccess {
    @Shadow @Final private ItemStackHandler inventory;

    public CookingPotBlockEntity_FluidCapsMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ====== Players To Send Pkt =======
    @Unique private final List<Player> sdtfc$players = new ArrayList<>();
    @Override public void sdtfc$addPlayer(Player player){
        this.sdtfc$players.add(player);
    }
    @Override public void sdtfc$removePlayer(Player player){
        this.sdtfc$players.remove(player);
    }
    // ====== 流體 Tank======
    @Unique private final FluidTank sdtfc$fluidTank = new FluidTank(4000) {
        @Override protected void onContentsChanged() { sdtfc$setChangedAndSync(); }
    };
    @Unique private final LazyOptional<IFluidHandler> sdtfc$fluidCap = LazyOptional.of(() -> sdtfc$fluidTank);

    // ====== 物品 Slot ======
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
    @Unique private final LazyOptional<IItemHandler> sdtfc$auxItemCap = LazyOptional.of(() -> sdtfc$auxInv);

    // 只攔截流體能力
    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true)
    private <T> void sdtfc$injectFluidCap(Capability<T> cap, @Nullable Direction side,
                                          CallbackInfoReturnable<LazyOptional<T>> cir) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            cir.setReturnValue(sdtfc$fluidCap.cast());
            cir.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), remap = true)
    private void sdtfc$setRemoved(CallbackInfo ci) {
        sdtfc$fluidCap.invalidate();
        sdtfc$auxItemCap.invalidate();
    }

    // ====== NBT：載入 / 儲存 ======
    @Inject(method = "load", at = @At("TAIL"), remap = true)
    private void sdtfc$loadExtraData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("survivorsdelight:pot_tank", Tag.TAG_COMPOUND)) {
            sdtfc$fluidTank.readFromNBT(tag.getCompound("survivorsdelight:pot_tank"));
        }
        if (tag.contains("survivorsdelight:aux_inv", Tag.TAG_COMPOUND)) {
            sdtfc$auxInv.deserializeNBT(tag.getCompound("survivorsdelight:aux_inv"));
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = true)
    private void sdtfc$saveExtraData(CompoundTag tag, CallbackInfo ci) {
        CompoundTag tank = new CompoundTag();
        sdtfc$fluidTank.writeToNBT(tank);
        tag.put("survivorsdelight:pot_tank", tank);

        CompoundTag aux = sdtfc$auxInv.serializeNBT();
        tag.put("survivorsdelight:aux_inv", aux);
    }

    // ====== 同步：update tag ======
    @Inject(method = "getUpdateTag", at = @At("RETURN"), cancellable = true, remap = true)
    private void sdtfc$appendExtraToUpdateTag(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag out = cir.getReturnValue();

        CompoundTag tank = new CompoundTag();
        sdtfc$fluidTank.writeToNBT(tank);
        out.put("survivorsdelight:pot_tank", tank);

        CompoundTag aux = sdtfc$auxInv.serializeNBT();
        out.put("survivorsdelight:aux_inv", aux);

        cir.setReturnValue(out);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("survivorsdelight:pot_tank", Tag.TAG_COMPOUND)) {
            sdtfc$fluidTank.readFromNBT(tag.getCompound("survivorsdelight:pot_tank"));
        }
        if (tag.contains("survivorsdelight:aux_inv", Tag.TAG_COMPOUND)) {
            sdtfc$auxInv.deserializeNBT(tag.getCompound("survivorsdelight:aux_inv"));
        }
    }

    @Unique
    private void sdtfc$setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Unique @Override public FluidTank sdtfc$getTank() { return sdtfc$fluidTank; }
    @Unique @Override public ItemStackHandler sdtfc$getInventory() {return inventory;}

    @Unique @Override public ItemStackHandler sdtfc$getAuxInv() { return sdtfc$auxInv; }

    @Inject(method = "cookingTick", at = @At("HEAD"))
    private static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot, CallbackInfo ci) {
        if (level.isClientSide) return;
        var self = (ICookingPotFluidAccess)cookingPot;

        self.sdtfc$updateFluidIOSlots();
    }

    @Unique
    public void sdtfc$updateFluidIOSlots()
    {
        assert level != null;
        var self = (ICookingPotFluidAccess)this;
        var tank = self.sdtfc$getTank();
        var inventory = self.sdtfc$getAuxInv();
        final ItemStack input = self.sdtfc$getAuxInv().getStackInSlot(0);
        if (!input.isEmpty() && self.sdtfc$getAuxInv().getStackInSlot(1).isEmpty())
        {
            FluidHelpers.transferBetweenBlockEntityAndItem(input, this, level, worldPosition, (newOriginalStack, newContainerStack) -> {
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
                        SDNetwork.CHANNEL.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                new SDNetwork.PotFluidSyncS2CPacket(worldPosition, ForgeRegistries.FLUIDS.getKey(tank.getFluid().getFluid()), tank.getFluidAmount())
                        );

                    }
                });
            });
        }
    }

}
