package com.vomiter.survivorsdelight.mixin.farming.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

@Mixin(RichSoilFarmlandBlock.class)
public class RichSoilFarmland_BlockEntity extends FarmBlock implements EntityBlock {
    public RichSoilFarmland_BlockEntity(Properties p_53247_) {
        super(p_53247_);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new net.dries007.tfc.common.blockentities.FarmlandBlockEntity(pos, state);
    }

}
