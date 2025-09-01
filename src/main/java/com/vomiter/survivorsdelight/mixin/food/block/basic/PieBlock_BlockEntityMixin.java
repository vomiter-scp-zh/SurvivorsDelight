package com.vomiter.survivorsdelight.mixin.food.block.basic;

import com.vomiter.survivorsdelight.core.food.block.SDDecayingBlockEntityRegistry;
import com.vomiter.survivorsdelight.core.food.block.ISDDecayingBlock;
import com.vomiter.survivorsdelight.core.food.block.DecayingPieBlockEntity;
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
import vectorwing.farmersdelight.common.block.PieBlock;

@Mixin(PieBlock.class)
public abstract class PieBlock_BlockEntityMixin extends Block implements EntityBlock, ISDDecayingBlock {
    public PieBlock_BlockEntityMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DecayingPieBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == SDDecayingBlockEntityRegistry.PIE_DECAYING.get()
                ? (l, p, st, be) -> DecayingPieBlockEntity.serverTick(l, p, st, (DecayingPieBlockEntity) be)
                : null;
    }

}
