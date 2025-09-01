package com.vomiter.survivorsdelight.mixin.food.block.basic;

import com.vomiter.survivorsdelight.core.food.block.SDDecayingBlockEntityRegistry;
import com.vomiter.survivorsdelight.core.food.block.ISDDecayingBlock;
import com.vomiter.survivorsdelight.core.food.block.DecayingFeastBlockEntity;
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
public abstract class FeastBlock_BlockEntityMixin extends Block implements EntityBlock, ISDDecayingBlock {
    public FeastBlock_BlockEntityMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DecayingFeastBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == SDDecayingBlockEntityRegistry.FEAST_DECAYING.get()
                ? (l, p, st, be) -> DecayingFeastBlockEntity.serverTick(l, p, st, (DecayingFeastBlockEntity) be)
                : null;
    }

}
