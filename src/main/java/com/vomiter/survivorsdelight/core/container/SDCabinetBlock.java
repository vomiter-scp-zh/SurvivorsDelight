package com.vomiter.survivorsdelight.core.container;

import com.vomiter.survivorsdelight.core.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
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

    @Unique
    private boolean sdtfc$checkCanTreat(Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand){
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) return false;
        if(cabinet.isTreated()) return false;
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof FluidContainerItem) {
            IFluidHandlerItem itemHandler = Helpers.getCapability(mainHandItem, Capabilities.FLUID_ITEM);
            if (itemHandler != null) {
                boolean cantTreat = itemHandler.getFluidInTank(0).getFluid().isSame(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.TALLOW).getSource());
                if(cantTreat) {
                    itemHandler.drain(100, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                }
            }
        }
        else if(mainHandItem.is(SDTags.ItemTags.WOOD_PRESERVATIVES)){
            if(mainHandItem.isDamageableItem()) mainHandItem.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
            else mainHandItem.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) return InteractionResult.PASS;
        if(sdtfc$checkCanTreat(level, pos, player, hand)) {
            cabinet.setTreated(true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer sp) {

                    NetworkHooks.openScreen(sp, cabinet, buf -> buf.writeBlockPos(pos));

            }
        }

        return InteractionResult.SUCCESS;
    }

    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    FoodCapability.removeTrait(container.getItem(i), SDFoodTraits.CABINET_STORED);
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
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof SDCabinetBlockEntity cabinet && !drops.isEmpty()) {
            for (ItemStack drop : drops) {
                if (drop.getItem() == this.asItem() && cabinet.isTreated()) {
                    drop.getOrCreateTagElement("BlockEntityTag")
                            .putBoolean(SDCabinetBlockEntity.TAG_TREATED, cabinet.isTreated());
                    if (cabinet.hasCustomName()) drop.setHoverName(cabinet.getCustomName());
                }
            }
        }
        return drops;
    }
}
