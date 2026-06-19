package com.vomiter.survivorsdelight.mixin.food.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import com.vomiter.survivorsdelight.SDConfig;
import com.vomiter.survivorsdelight.common.food.block.DecayFoodTransfer;
import com.vomiter.survivorsdelight.common.food.block.DecayingPieBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.utility.ItemUtils;

@Mixin(value = PieBlock.class, remap = false)
public abstract class PieBlock_SliceMixin{

    @Shadow public abstract ItemStack getPieSliceItem();

    @Unique
    private ItemStack cachedStack = ItemStack.EMPTY;
    @Inject(method = "use", at = @At("HEAD"), remap = true)
    private void sdtfc$cachePie(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        if (!cachedStack.isEmpty()) return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DecayingPieBlockEntity decay)) return;
        cachedStack = decay.getStack();
    }


    @Shadow
    public abstract int getMaxBites();

    @Inject(method = "cutSlice", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), cancellable = true, require = 0)
    private void sdtfc$cutDecaySlice2(Level level, BlockPos pos, BlockState state, Player player, Item knife, CallbackInfoReturnable<InteractionResult> cir){
        if(cachedStack.isEmpty()) return;
        Direction direction = player.getDirection().getOpposite();
        ItemStack slice = getPieSliceItem();
        sdtfc$applyFoodFromDecay(slice);
        ItemUtils.spawnItemEntity(level, slice, (double)pos.getX() + (double)0.5F, (double)pos.getY() + 0.3, (double)pos.getZ() + (double)0.5F, (double)direction.getStepX() * 0.15, 0.05, (double)direction.getStepZ() * 0.15);
        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }


    @ModifyVariable(method = "consumeBite", at = @At(value = "STORE"), name = "sliceStack")
    private ItemStack sdtfc$applyDecayToSlice(
            ItemStack value,
            @Local(argsOnly = true, name = "arg1") Level level,
            @Local(argsOnly = true, name = "arg2") BlockPos pos
    ){
        if(cachedStack.isEmpty()) return value;
        sdtfc$applyFoodFromDecay(value);
        return value;
    }

    @ModifyVariable(method = "consumeBite", at = @At(value = "STORE"), name = "sliceFood")
    private FoodProperties sdtfc$modifyAddedEffects(
            FoodProperties value,
            @Local(argsOnly = true, name = "arg1") Level level,
            @Local(argsOnly = true, name = "arg2") BlockPos pos
    ){
        IFood decay = FoodCapability.get(cachedStack);
        if(decay != null) {
            if(decay.isRotten()){
                FoodProperties.Builder fakeFoodBuilder = new FoodProperties.Builder();
                for(Pair<MobEffectInstance, Float> pair : value.getEffects()){
                    if(!pair.getFirst().getEffect().isBeneficial()) fakeFoodBuilder.effect(pair::getFirst, pair.getSecond());
                }
                return fakeFoodBuilder.build();
            }
        }
        return value;
    }


    @Unique
    private ItemStack sdtfc$applyFoodFromDecay(ItemStack slice) {
        float factor;
        if (SDConfig.REBALANCING_FEAST) factor = 1f / (float) getMaxBites();
        else factor = 1f;

        return DecayFoodTransfer.copyFoodState(cachedStack, slice, true, factor);
    }
}
