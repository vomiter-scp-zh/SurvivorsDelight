package com.vomiter.survivorsdelight.core.container;

import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;
import vectorwing.farmersdelight.common.block.CabinetBlock;

import javax.annotation.Nullable;
import java.util.List;

public class SDCabinetBlock extends CabinetBlock {
    public SDCabinetBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ((BlockEntityType<?>) SDBlockEntityTypes.SD_CABINET.get()).create(pos, state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 已處理就讓其它互動流程繼續
        if (cabinet.isTreated()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 嘗試以手上物品進行「木材防腐處理」
        if (sdtfc$tryTreatWithItem(level, pos, player, hand, stack)) {
            cabinet.setTreated(true);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // 不屬於處理劑的物品 -> 交回「預設方塊互動」(例如開櫃、擺放物品等)
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /* ========= 空手互動：開啟 GUI ========= */
    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                                     @NotNull Player player, @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            player.openMenu(cabinet, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Unique
    private boolean sdtfc$tryTreatWithItem(Level level, BlockPos pos, Player player,
                                           InteractionHand hand, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) return false;
        if (cabinet.isTreated()) return false;

        if (stack.getItem() instanceof FluidContainerItem) {
            IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null && handler.getTanks() > 0) {
                boolean isTallow = handler.getFluidInTank(0).getFluid()
                        .isSame(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.TALLOW).getSource());
                if (isTallow) {
                    handler.drain(100, IFluidHandlerItem.FluidAction.EXECUTE);
                    return true;
                }
            }
        }
        else if (stack.is(SDTags.ItemTags.WOOD_PRESERVATIVES)) {
            if (stack.isDamageableItem()) {
                EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                        ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);
            } else {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof SDCabinetBlockEntity container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    container.removeStored(container.getItem(i));
                }
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof SDCabinetBlockEntity cabinet && !drops.isEmpty()) {
            for (ItemStack drop : drops) {
                if (drop.getItem() == this.asItem()) {
                    // 1) 構好要帶到物品上的方塊實體資料
                    CompoundTag tag = new CompoundTag();
                    tag.putBoolean(SDCabinetBlockEntity.TAG_TREATED, cabinet.isTreated());

                    // 2) 寫入 Data Component（等同於舊 BlockEntityTag）
                    drop.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));

                    // 3) 名稱也建議走 component（setHoverName 仍可用）
                    if (cabinet.hasCustomName()) {
                        drop.set(DataComponents.CUSTOM_NAME, cabinet.getCustomName());
                    }
                }
            }
        }
        return drops;
    }
    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof SDCabinetBlockEntity sdCabinetBlockEntity) {
            sdCabinetBlockEntity.recheckOpen();
        }
    }

}
