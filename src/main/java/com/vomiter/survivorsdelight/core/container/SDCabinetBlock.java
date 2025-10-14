package com.vomiter.survivorsdelight.core.container;

import com.vomiter.survivorsdelight.core.registry.SDBlockEntityTypes;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.CabinetBlock;

import javax.annotation.Nullable;

public class SDCabinetBlock extends CabinetBlock {
    public SDCabinetBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ((BlockEntityType<?>) SDBlockEntityTypes.SD_CABINET.get()).create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer sp) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof SDCabinetBlockEntity cabinet) {
                    NetworkHooks.openScreen(sp, cabinet, buf -> buf.writeBlockPos(pos));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    FoodCapability.removeTrait(container.getItem(i), FoodTraits.PRESERVED);
                }
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }

    }

}
