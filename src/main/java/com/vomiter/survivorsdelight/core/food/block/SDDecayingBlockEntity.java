package com.vomiter.survivorsdelight.core.food.block;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class SDDecayingBlockEntity extends DecayingBlockEntity {
    private boolean foodRotten = false;

    public SDDecayingBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SDDecayingBlockEntity blockEntity) {
        if (level.getGameTime() % 20L == 0L && blockEntity.isRotten() && !blockEntity.foodRotten) {
            blockEntity.foodRotten = true;
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

}
