package com.vomiter.survivorsdelight.mixin.foodBlock.basic;

import com.vomiter.survivorsdelight.core.foodBlock.FDDecayingBlockEntityRegistry;
import com.vomiter.survivorsdelight.core.foodBlock.FDIDecayingBlock;
import com.vomiter.survivorsdelight.core.foodBlock.FeastDecayingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.block.FeastBlock;

@Mixin(FeastBlock.class)
public abstract class FeastBlock_BlockEntityMixin extends Block implements EntityBlock, FDIDecayingBlock {
    public FeastBlock_BlockEntityMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FeastDecayingBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == FDDecayingBlockEntityRegistry.FEAST_DECAYING.get()
                ? (l, p, st, be) -> FeastDecayingBlockEntity.serverTick(l, p, st, (FeastDecayingBlockEntity) be)
                : null;
    }

}
