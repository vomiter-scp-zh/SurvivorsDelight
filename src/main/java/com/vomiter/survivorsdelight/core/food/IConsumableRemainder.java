package com.vomiter.survivorsdelight.core.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface IConsumableRemainder extends IForgeItem {
    @Override
    default ItemStack getCraftingRemainingItem(ItemStack itemStack){
        CompoundTag tag = itemStack.getTag();
        if(tag != null){
            return ItemStack.of(tag.getCompound("Container"));
        }
        return IForgeItem.super.getCraftingRemainingItem(itemStack);
    }
}