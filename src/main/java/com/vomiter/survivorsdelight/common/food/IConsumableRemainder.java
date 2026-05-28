package com.vomiter.survivorsdelight.common.food;

import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface IConsumableRemainder extends IForgeItem {
    @Override
    default ItemStack getCraftingRemainingItem(ItemStack itemStack){
        CompoundTag tag = itemStack.getTag();
        if(tag != null){
            return FoodItemContainerApply.getRemainder(itemStack);
        }
        return IForgeItem.super.getCraftingRemainingItem(itemStack);
    }
}