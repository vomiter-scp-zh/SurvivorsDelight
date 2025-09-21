package com.vomiter.survivorsdelight.mixin.farming.soil;

import com.vomiter.survivorsdelight.Config;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.RichSoilBlock;
import vectorwing.farmersdelight.common.utility.MathUtils;

@Mixin(value = RichSoilBlock.class)
public class RichSoil_TreeSpalingMixin {
    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BonemealableBlock;performBonemeal(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"),
            cancellable = true
    )
    private void tfc_tree_growth_boost(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci){
        if(level.getBlockEntity(pos.above()) instanceof TickCounterBlockEntity tickCounter){
            tickCounter.reduceCounter(-1L * Config.COMMON.richSoilGrowthBoostTick.get());
            level.levelEvent(2005, pos.above(), 0);
            level.sendBlockUpdated(pos.above(), level.getBlockState(pos.above()), level.getBlockState(pos.above()), 3);
            ci.cancel();
        }
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "TAIL"),
            cancellable = true
    )
    private void unbonemeable_growth_boost(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci){
        if(level.getBlockState(pos.above()).getBlock() instanceof BonemealableBlock) return;
        if(level.getBlockEntity(pos.above()) instanceof TickCounterBlockEntity tickCounter){
            if ((double) MathUtils.RAND.nextFloat() <= Configuration.RICH_SOIL_BOOST_CHANCE.get()) {
                tickCounter.reduceCounter(-1L * Config.COMMON.richSoilGrowthBoostTick.get());
                level.levelEvent(2005, pos.above(), 0);
                level.sendBlockUpdated(pos.above(), level.getBlockState(pos.above()), level.getBlockState(pos.above()), 3);
                ci.cancel();
            }
        }
    }

}
