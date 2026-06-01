package com.vomiter.survivorsdelight.mixin.food.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.common.food.block.DecayingPieBlockEntity;
import com.vomiter.survivorsdelight.compat.firmalife.FLCompatHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
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
public abstract class PieBlock_SliceMixin implements EntityBlock {

    @Shadow public abstract ItemStack getPieSliceItem();

    @Inject(method = "cutSlice", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), cancellable = true)
    private void sdtfc$cutDecaySlice(Level level, BlockPos pos, BlockState state, Player player, Item knife, CallbackInfoReturnable<ItemInteractionResult> cir){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DecayingPieBlockEntity decay)) return;
        Direction direction = player.getDirection().getOpposite();
        ItemStack slice = getPieSliceItem();
        SurvivorsDelight.LOGGER.info("[SD] foodSlice = {}", FoodCapability.get(slice).getData().nutrients());
        sdtfc$applyFoodFromDecay(decay, slice);
        SurvivorsDelight.LOGGER.info("[SD] foodSlice = {}", FoodCapability.get(slice).getData().nutrients());

        ItemUtils.spawnItemEntity(level, slice, (double)pos.getX() + (double)0.5F, (double)pos.getY() + 0.3, (double)pos.getZ() + (double)0.5F, (double)direction.getStepX() * 0.15, 0.05, (double)direction.getStepZ() * 0.15);
        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }

    @WrapOperation(method = "consumeBite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V", remap = true))
    private void sdtfc$eat(
            FoodData instance,
            FoodProperties foodProperties,
            Operation<Void> original,
            @Local(argsOnly = true) Player player,
            @Local(argsOnly = true, name = "arg1") Level level,
            @Local(argsOnly = true, name = "arg2") BlockPos pos
    ){
        original.call(instance, foodProperties);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof DecayingPieBlockEntity decay) {
            var sliceStack = getPieSliceItem();
            sdtfc$applyFoodFromDecay(decay, sliceStack);
            IPlayerInfo.get(player).eat(sliceStack);
        }
    }

    @ModifyVariable(method = "consumeBite", at = @At(value = "STORE"), name = "sliceFood")
    private FoodProperties sdtfc$modifyAddedEffects(
            FoodProperties sliceFood,
            @Local(argsOnly = true, name = "arg1") Level level,
            @Local(argsOnly = true, name = "arg2") BlockPos pos
    ){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof DecayingPieBlockEntity decay) {
            if(decay.isRotten()){
                FoodProperties.Builder fakeFoodBuilder = new FoodProperties.Builder();
                sliceFood.effects().forEach(pe -> {
                    if(!pe.effect().getEffect().value().isBeneficial()) fakeFoodBuilder.effect(pe::effect, pe.probability());
                });
                return fakeFoodBuilder.build();
            }
        }
        return sliceFood;
    }


    @Unique
    private static void sdtfc$applyFoodFromDecay(DecayingPieBlockEntity decay, ItemStack slice) {
        ItemStack src    = decay.getStack();
        IFood srcFood    = FoodCapability.get(src);
        if (srcFood == null) return;

        FoodCapability.setFoodForDynamicItemOnCreate(slice, srcFood.getData());
        FoodCapability.setCreationDate(slice, srcFood.getCreationDate());
        if(ModList.get().isLoaded("firmalife")){
            for (Holder<FoodTrait> possibleShelvedFoodTrait : FLCompatHelpers.getPossibleShelvedFoodTraits()) {
                FoodCapability.removeTrait(slice, possibleShelvedFoodTrait);
            }
        }

    }
}