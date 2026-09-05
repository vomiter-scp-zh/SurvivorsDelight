package com.vomiter.survivorsdelight.adapter.skillet.skillet_item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface ISkilletItemCookingData {
    ItemStack getCooking();
    void setCooking(ItemStack stack);
    float getTargetTemp();
    void setTargetTemp(float f);

    default void clear(){
        setCooking(ItemStack.EMPTY);
        setTargetTemp(0);
    }
}
