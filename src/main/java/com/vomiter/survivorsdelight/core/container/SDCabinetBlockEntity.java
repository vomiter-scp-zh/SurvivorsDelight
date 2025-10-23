package com.vomiter.survivorsdelight.core.container;

import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import com.vomiter.survivorsdelight.core.registry.SDContainerTypes;
import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.item.SkilletItem;

import java.util.stream.IntStream;

public class SDCabinetBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int ROWS = 2;
    public static final int COLS = 9;
    public static final String TAG_TREATED = "Treated";
    private static final int[] ALL_SLOTS = IntStream.range(0, ROWS * COLS).toArray();
    private static final FoodTrait CABINET_STORED = SDFoodTraits.CABINET_STORED;

    private boolean treated;
    public boolean isTreated() { return treated; }
    public void setTreated(boolean treated) {
        this.treated = treated;
        setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(ROWS * COLS, ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }
        if (tag.contains(TAG_TREATED)) {
            this.treated = tag.getBoolean(TAG_TREATED);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
        tag.putBoolean(TAG_TREATED, this.treated);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return isValid(stack);
    }
    
    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return ALL_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction side) {
        return isValid(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int p_19239_, @NotNull ItemStack p_19240_, @NotNull Direction p_19241_) {
        return true;
    }


    public SDCabinetBlockEntity(BlockPos pos, BlockState state) {
        super(SDBlockEntityTypes.SD_CABINET.get(), pos, state);
    }

    private NonNullList<ItemStack> items = NonNullList.withSize(ROWS * COLS, ItemStack.EMPTY);

    @Override public int getContainerSize() { return items.size(); }
    @Override protected @NotNull NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(@NotNull NonNullList<ItemStack> items) { this.items = items; }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (!stack.isEmpty() && !isValid(stack)) {
            return;
        }
        super.setItem(slot, stack);
        assert level != null;
        if (!level.isClientSide && !stack.isEmpty()) {
            FoodCapability.applyTrait(stack, CABINET_STORED);
        }
        setChanged();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack removed = super.removeItem(slot, amount);
        assert level != null;
        if (!level.isClientSide && !removed.isEmpty()) {
            FoodCapability.removeTrait(removed, CABINET_STORED);
        }
        setChanged();
        return removed;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        assert level != null;
        if (!level.isClientSide && !removed.isEmpty()) {
            FoodCapability.removeTrait(removed, CABINET_STORED);
        }
        setChanged();
        return removed;
    }

    public static boolean isValid(ItemStack stack) {
        return TFCChestBlockEntity.isValid(stack) || stack.getItem() instanceof SkilletItem;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.survivorsdelight.cabinet");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv) {
        return new SDCabinetMenu(SDContainerTypes.CABINET.get(), id, inv, this, 2);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.survivorsdelight.cabinet");
    }

    /*
    =====
     */

    private final LazyOptional<IItemHandler> upHandler =
            LazyOptional.of(() -> new TraitItemHandler(this, Direction.UP));
    private final LazyOptional<IItemHandler> downHandler =
            LazyOptional.of(() -> new TraitItemHandler(this, Direction.DOWN));
    private final LazyOptional<IItemHandler> sideHandler =
            LazyOptional.of(() -> new TraitItemHandler(this, Direction.NORTH));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) return downHandler.cast();
            if (side == Direction.UP) return upHandler.cast();
            return sideHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        upHandler.invalidate();
        downHandler.invalidate();
        sideHandler.invalidate();
    }

    private static class TraitItemHandler extends SidedInvWrapper {
        private final SDCabinetBlockEntity be;

        public TraitItemHandler(SDCabinetBlockEntity be, Direction side) {
            super(be, side);
            this.be = be;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isValid(stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            // 不接受或空堆疊就直接返回
            if (stack.isEmpty() || !isItemValid(slot, stack)) {
                return stack;
            }

            // 注意：我們忽略傳進來的 slot，會在「可存取的所有槽位」上跑 merge→填空
            ItemStack toInsert = stack.copy();

            if (be.level != null && !be.level.isClientSide) {
                FoodCapability.applyTrait(toInsert, CABINET_STORED);
            }

            // 1) 先嘗試與可存取的所有槽位中「相同物品且可疊（忽略 creation_date）」的堆疊做 merge
            for (int i = 0; i < getSlots(); i++) {
                if (toInsert.isEmpty()) break;

                ItemStack existing = getStackInSlot(i);
                if (!existing.isEmpty()
                        && FoodCapability.areStacksStackableExceptCreationDate(existing, toInsert)) {

                    // 記錄修改前數量，方便計算實際移入幾個
                    int before = toInsert.getCount();

                    if (simulate) {
                        // 模擬：用副本計算
                        ItemStack existingCopy = existing.copy();
                        ItemStack toInsertCopy = toInsert.copy();
                        FoodCapability.mergeItemStacks(existingCopy, toInsertCopy);
                        // 計算在這個槽位能移入多少
                        int moved = before - toInsertCopy.getCount();
                        if (moved > 0) {
                            toInsert.shrink(moved);
                        }
                    } else {
                        // 實際：直接在現有物品上合併
                        FoodCapability.mergeItemStacks(existing, toInsert);
                        setStackInSlot(i, existing);
                        // 伺服器端：確保保持 PRESERVED 特性
                        if (be.level != null && !be.level.isClientSide) {
                            FoodCapability.applyTrait(existing, CABINET_STORED);
                        }
                        be.setChanged();
                    }
                }
            }

            // 2) 若還有剩餘，嘗試放到可用的空槽位
            for (int i = 0; i < getSlots(); i++) {
                if (toInsert.isEmpty()) break;

                ItemStack existing = getStackInSlot(i);
                if (existing.isEmpty() && isItemValid(i, toInsert)) {
                    if (simulate) {
                        // 模擬：計算理論可放入量（受限於每槽上限與物品自身上限）
                        int limit = Math.min(getSlotLimit(i), toInsert.getMaxStackSize());
                        int moved = Math.min(limit, toInsert.getCount());
                        if (moved > 0) {
                            toInsert.shrink(moved);
                        }
                    } else {
                        // 實際：使用 FoodCapability.mergeItemStacks 讓 creation_date 規則一致
                        ItemStack newStack = FoodCapability.mergeItemStacks(ItemStack.EMPTY, toInsert);
                        setStackInSlot(i, newStack);
                        if (be.level != null && !be.level.isClientSide) {
                            FoodCapability.applyTrait(newStack, CABINET_STORED);
                        }
                        be.setChanged();
                    }
                }
            }

            if (be.level != null && !be.level.isClientSide) {
                FoodCapability.removeTrait(toInsert, CABINET_STORED);
            }
            // 回傳剩餘沒放進去的堆疊（IItemHandler 規格）
            return toInsert;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack out = super.extractItem(slot, amount, simulate);
            if (!simulate && !out.isEmpty()) {
                assert be.level != null;
                if (!be.level.isClientSide) {
                    FoodCapability.removeTrait(out, CABINET_STORED);
                    be.setChanged();
                }
            }
            return out;
        }
    }
}
