package com.vomiter.survivorsdelight.core.food.block;

import com.vomiter.survivorsdelight.compat.firmalife.FLCompatHelpers;
import com.vomiter.survivorsdelight.compat.firmalife.SDClimateReceiver;
import com.vomiter.survivorsdelight.compat.firmalife.SDClimateType;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

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


}
