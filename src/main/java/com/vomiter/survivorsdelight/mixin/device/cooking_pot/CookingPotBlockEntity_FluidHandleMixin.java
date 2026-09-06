package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle.CookingPotFluidIO;
import com.vomiter.survivorsdelight.adapter.cooking_pot.fluid_handle.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.registry.recipe.SDCookingPotRecipe;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

/*
This mixin handles how fluid works in cooking pot and how fluid requiring recipe is handled.
For TFC pot recipe bridge, please check LEGACY_CookingPotBlockEntity_PotRecipeBridgeMixin.java
 */
@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntity_FluidHandleMixin extends BlockEntity  implements ICookingPotFluidAccess  {

    public CookingPotBlockEntity_FluidHandleMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    private boolean sdtfc$warnedMissingFluidAccess = false;

    @Unique private static long sdtfc$lastServerTickWarnGameTime = -1;


    @Unique
    private void sdtfc$warnMissingFluidAccess(){
        if(!sdtfc$warnedMissingFluidAccess) {
            SurvivorsDelight.LOGGER.warn(
                    "[SD][CookingPot] Missing ICookingPotFluidAccess at {} (this={})",
                    getBlockPos(),
                    this.getClass().getName()
            );
            sdtfc$warnedMissingFluidAccess = true;
        }
    }

    @Unique
    private ICookingPotFluidAccess sdtfc$getFluidAccess(){
        var access = (Object) this instanceof ICookingPotFluidAccess a ? a : null;
        if(access == null) sdtfc$warnMissingFluidAccess();
        return access;
    }

    // ====== TFC barrel-like fluid input/output with fluid container items ======
    @Inject(method = "cookingTick", at = @At("HEAD"))
    private static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot, CallbackInfo ci) {
        if (level.isClientSide) return;
        if(cookingPot instanceof ICookingPotFluidAccess fluidAccess) {
            fluidAccess.sdtfc$updateFluidIOSlots();
        }
        else {
            long t = level.getGameTime();
            if (t - sdtfc$lastServerTickWarnGameTime > 200) { // 200 ticks = 10s
                SurvivorsDelight.LOGGER.warn(
                        "[SD][CookingPot] Cooking Pot at {} does not have proper interface: ICookingPotFluidAccess.",
                        pos
                );
                sdtfc$lastServerTickWarnGameTime = t;
            }
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
        if(cookingPot instanceof ICookingPotFluidAccess access)
            access.sdtfc$getTank()
                    .drain(
                            sdCookingPotRecipe.getFluidAmountMb(),
                            IFluidHandler.FluidAction.EXECUTE
                    );
        else sdtfc$warnMissingFluidAccess();
    }

    // ====== 方塊破壞時清除 Caps =======
    @Inject(method = "setRemoved", at = @At("TAIL"), remap = true)
    private void sdtfc$setRemoved(CallbackInfo ci) {
    }

    // ====== NBT：載入 / 儲存 ======
    @Inject(method = "loadAdditional", at = @At("TAIL"), remap = true)
    private void sdtfc$loadExtraData(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        var access = sdtfc$getFluidAccess();
        if(access == null) return;
        CookingPotFluidIO.load(registries, compound, access);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = true)
    private void sdtfc$saveExtraData(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        var access = sdtfc$getFluidAccess();
        if(access == null) return;
        CookingPotFluidIO.save(registries, compound, access);
    }

    @Inject(method = "getUpdateTag", at = @At("RETURN"), remap = true)
    private void sdtfc$appendExtraToUpdateTag(
            HolderLookup.Provider registries,
            CallbackInfoReturnable<CompoundTag> cir
    ) {
        ICookingPotFluidAccess access = sdtfc$getFluidAccess();
        if (access == null) return;

        CookingPotFluidIO.appendToUpdateTag(
                registries,
                cir.getReturnValue(),
                access
        );
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        var access = sdtfc$getFluidAccess();
        if(access == null) return;
        CookingPotFluidIO.handleUpdateTag(registries, tag, access);
    }

    // ====== Players To Send Pkt =======
    @Unique
    private final List<ServerPlayer> sdtfc$players = new ArrayList<>();

    @Override public List<ServerPlayer> sdtfc$getPlayer(){return sdtfc$players;}
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

    @Unique
    @Override
    public void sdtfc$updateFluidIOSlots() {
        if(level == null) return;
        if(level.getBlockEntity(getBlockPos()) instanceof CookingPotBlockEntity cookingPot){
            CookingPotFluidIO.updateFluidIOSlots(cookingPot);
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
