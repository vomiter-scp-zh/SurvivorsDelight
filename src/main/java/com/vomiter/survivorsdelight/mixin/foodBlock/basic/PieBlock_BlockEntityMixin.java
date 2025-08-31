package com.vomiter.survivorsdelight.mixin.foodBlock.basic;

import com.vomiter.survivorsdelight.core.foodBlock.FDDecayingBlockEntityRegistry;
import com.vomiter.survivorsdelight.core.foodBlock.FDIDecayingBlock;
import com.vomiter.survivorsdelight.core.foodBlock.PieDecayingBlockEntity;
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
public abstract class PieBlock_BlockEntityMixin extends Block implements EntityBlock, FDIDecayingBlock {
    public PieBlock_BlockEntityMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PieDecayingBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == FDDecayingBlockEntityRegistry.PIE_DECAYING.get()
                ? (l, p, st, be) -> PieDecayingBlockEntity.serverTick(l, p, st, (PieDecayingBlockEntity) be)
                : null;
    }

}
