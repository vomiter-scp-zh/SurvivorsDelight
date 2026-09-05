package com.vomiter.survivorsdelight.mixin.device.skillet;

import com.vomiter.survivorsdelight.adapter.skillet.ISkilletItemCookingData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class SkilletData_ItemStackMixin implements ISkilletItemCookingData {

    @Unique
    ItemStack sdtfc$cookingStack = ItemStack.EMPTY;

    @Unique
    float sdtfc$targetTemperature = 0f;

    @Override
    public ItemStack getCooking() {
        return sdtfc$cookingStack;
    }

    @Override
    public void setCooking(ItemStack stack) {
        sdtfc$cookingStack = stack;
    }

    @Override
    public float getTargetTemp() {
        return sdtfc$targetTemperature;
    }

    @Override
    public void setTargetTemp(float f) {
        sdtfc$targetTemperature = f;
    }
}
