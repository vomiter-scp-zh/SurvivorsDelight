package com.vomiter.survivorsdelight.core.device.cooking_pot;

import com.vomiter.survivorsdelight.core.registry.SDContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import javax.annotation.Nullable;

public class SDCookingPotFluidMenu extends AbstractContainerMenu {
    public static final MenuType<SDCookingPotFluidMenu> TYPE = SDContainerTypes.POT_FLUID_MENU.get();
    public final BlockPos pos;
    @Nullable private ICookingPotFluidAccess be;
    public static final int X_DEVIATION = 22;
    public static final int Y_DEVIATION = -3;

    private final DataSlot fluidAmount = DataSlot.standalone();
    private final DataSlot fluidCapacity = DataSlot.standalone();
    private int clientFluidCapacity = 4000;
    private FluidStack clientFluid = FluidStack.EMPTY;

    public SDCookingPotFluidMenu(int id, Inventory inv, FriendlyByteBuf buf) { this(id, inv, buf.readBlockPos()); }

    public SDCookingPotFluidMenu(int id, Inventory inv, BlockPos pos) {
        super(TYPE, id);
        this.pos = pos;

        final Level level = inv.player.level();
        final BlockEntity maybe = level.getBlockEntity(pos);
        if (maybe instanceof CookingPotBlockEntity pot) {
            this.be = (ICookingPotFluidAccess) pot;

            pot.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(h -> {
                setClientFluid(h.getFluidInTank(0));
                fluidCapacity.set(h.getTankCapacity(0));
                fluidAmount.set(h.getFluidInTank(0).getAmount());
            });
        }

        // === 綁定兩個「桶子 I/O」槽，直接連到方塊實體的 ItemStackHandler ===
        // slot index: 0 = input, 1 = output
        if (be != null) {
            var aux = be.sdtfc$getAuxInv();
            // 輸入：允許放入（驗證交由 ItemStackHandler#isItemValid），UI座標自行調整
            this.addSlot(new BucketInputSlot(aux, 0, 35 + X_DEVIATION, 20 + Y_DEVIATION));
            // 輸出：拒收放入，允許取出
            this.addSlot(new BucketOutputSlot(aux, 1, 35 + X_DEVIATION, 54 + Y_DEVIATION));
        } else {
            // 理論上不會發生；保底避免 NPE（給一個假的 1x1 容器）
            this.addSlot(new Slot(new SimpleContainer(1), 0, 35, 20));
            this.addSlot(new Slot(new SimpleContainer(1), 0, 35, 54));
        }

        // === 玩家背包槽 ===
        final int yBase = 84;
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, yBase + row * 18));
        for (int col = 0; col < 9; ++col)
            this.addSlot(new Slot(inv, col, 8 + col * 18, yBase + 58));

        // === 用 DataSlot 同步「容量」 ===
        this.addDataSlot(new DataSlot() {
            @Override public int get() {
                if (be != null) return be.sdtfc$getTank().getCapacity();
                return 0;
            }
            @Override public void set(int value) {
                clientFluidCapacity = value;
            }
        });
    }

    public BlockPos getPos() { return pos; }

    @Override public boolean stillValid(@NotNull Player player) {
        return be != null && player.distanceToSqr(Vec3.atCenterOf(pos)) < 64.0;
    }

    public int getFluidCapacity() {
        return be != null ? be.sdtfc$getTank().getCapacity() : clientFluidCapacity;
    }

    public void setClientFluid(net.minecraftforge.fluids.FluidStack fs) {
        this.clientFluid = fs.copy();
    }

    public FluidStack getClientFluid() { return clientFluid; }


    /**
     * 0..1: 我們的兩格（input=0, output=1）
     * 2..28: 玩家背包 (27 格)
     * 29..37: 玩家快捷列 (9 格)
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();

        final int AUX_START = 0;
        final int AUX_END_EXCL = 2; // 0..1
        final int INV_START = 2;
        final int INV_END_EXCL = 29; // 2..28
        final int HOTBAR_START = 29;
        final int HOTBAR_END_EXCL = 38; // 29..37

        if (index < AUX_END_EXCL) {
            // 來自 aux（input/output） -> 移到玩家背包+快捷列
            if (!this.moveItemStackTo(stackInSlot, INV_START, HOTBAR_END_EXCL, true)) return empty;
        } else {
            // 來自玩家背包/快捷列 -> 試圖放進 aux 的「輸入格」(slot 0)
            // 只嘗試輸入槽（輸出槽拒收）
            if (!this.moveItemStackTo(stackInSlot, AUX_START, AUX_START + 1, false)) {
                // 如果塞不進輸入槽，嘗試在背包/快捷列內部整理
                if (index < HOTBAR_START) {
                    // 背包 -> 快捷列
                    if (!this.moveItemStackTo(stackInSlot, HOTBAR_START, HOTBAR_END_EXCL, false)) return empty;
                } else {
                    // 快捷列 -> 背包
                    if (!this.moveItemStackTo(stackInSlot, INV_START, INV_END_EXCL, false)) return empty;
                }
            }
        }

        if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    // --- 自訂 Slot：輸入/輸出 ---
    static class BucketInputSlot extends SlotItemHandler {
        public BucketInputSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override public boolean mayPlace(@NotNull ItemStack stack) {
            return super.mayPlace(stack);
        }

        @Override public int getMaxStackSize() { return 1; }
    }

    static class BucketOutputSlot extends SlotItemHandler {
        public BucketOutputSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override public boolean mayPickup(@NotNull Player player) { return true; }
        @Override public int getMaxStackSize() { return 1; }
    }
}