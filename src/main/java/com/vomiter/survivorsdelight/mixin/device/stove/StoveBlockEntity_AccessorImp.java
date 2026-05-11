package com.vomiter.survivorsdelight.mixin.device.stove;

import com.vomiter.survivorsdelight.adapter.stove.IStoveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(value = StoveBlockEntity.class, remap = false)
public abstract class StoveBlockEntity_AccessorImp extends AbstractStoveBlockEntity implements IStoveBlockEntity {

    protected StoveBlockEntity_AccessorImp(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, blockPos, blockState, recipeType);
    }

    public AbstractStoveBlockEntity sdtfc$getBlockEntity(){
        return this;
    };

    public ItemStackHandler sdtfc$getInventory(){
        return ((StoveBlockEntity)(Object)this).getItems();
    };

    public int[] sdtfc$getCookingTimes(){
        if(this instanceof StoveBlockEntity_Accessor acc){
            return acc.getCookingProgress();
        }
        return null;
    };
    public int[] sdtfc$getCookingTimesTotal(){
        if(this instanceof StoveBlockEntity_Accessor acc){
            return acc.getCookingTime();
        }
        return null;
    };
}
