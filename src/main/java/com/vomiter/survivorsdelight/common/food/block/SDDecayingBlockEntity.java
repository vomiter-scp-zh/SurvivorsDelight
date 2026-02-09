package com.vomiter.survivorsdelight.common.food.block;

import com.vomiter.survivorsdelight.compat.firmalife.FLCompatHelpers;
import com.vomiter.survivorsdelight.compat.firmalife.SDClimateReceiver;
import com.vomiter.survivorsdelight.compat.firmalife.SDClimateType;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SDDecayingBlockEntity extends DecayingBlockEntity implements SDClimateReceiver {
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

    @Override public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override public void loadAdditional(@NotNull CompoundTag tag) {
        super.loadAdditional(tag);
    }

    public void updatePreservation(boolean preserved) {
        if(!ModList.get().isLoaded("firmalife")) return;
        if (preserved)
        {
            FoodCapability.applyTrait(getStack(), FLCompatHelpers.getShelvedFoodTrait(this));
        }
        else
        {
            for (FoodTrait trait : FLCompatHelpers.getPossibleShelvedFoodTraits())
            {
                FoodCapability.removeTrait(getStack(), trait);
            }
        }
    }


    @Override public void setValid(Level level, BlockPos pos, boolean valid, int tier, SDClimateType climate){
        boolean climateValid = climate.equals(SDClimateType.CELLAR) && valid;
        updatePreservation(climateValid);
    };

    public static List<ItemStack> modifyDecayDrop(@Nullable BlockEntity blockEntity, List<ItemStack> drops){
        if(blockEntity instanceof SDDecayingBlockEntity decay){
            ItemStack srcStack = decay.getStack();
            IFood srcFood = FoodCapability.get(srcStack);
            if(srcFood == null) return drops;
            drops.forEach(drop -> {
                IFood dropFood = FoodCapability.get(drop);
                if(dropFood == null) return;
                if(dropFood instanceof FoodHandler.Dynamic dynamic && srcFood instanceof FoodHandler.Dynamic srcDynamic){
                    dynamic.setFood(srcFood.getData());
                    dynamic.setIngredients(srcDynamic.getIngredients());
                }
                if(ModList.get().isLoaded("firmalife")){
                    for (FoodTrait trait : FLCompatHelpers.getPossibleShelvedFoodTraits()) {
                        FoodCapability.removeTrait(srcFood, trait);
                    }
                }
                dropFood.setCreationDate(srcFood.getCreationDate());
                dropFood.getTraits().addAll(srcFood.getTraits());
            });
            if(drops.get(0).is(Items.BOWL)){
                CompoundTag tag = srcStack.getTag();
                if(tag != null && tag.get("Container") instanceof CompoundTag container){
                    drops.remove(0);
                    drops.add(ItemStack.of(container));
                }
            }
        }

        return drops;
    }


}
