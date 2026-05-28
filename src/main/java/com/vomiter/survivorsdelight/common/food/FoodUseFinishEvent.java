package com.vomiter.survivorsdelight.common.food;

import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import vectorwing.farmersdelight.common.item.ConsumableItem;

public class FoodUseFinishEvent {
    public static void onFoodUseFinish(LivingEntityUseItemEvent.Finish event){
        ItemStack stack = event.getItem();
        if(stack.getItem() instanceof ConsumableItem) return;
        CompoundTag tag = stack.getTag();
        if(tag != null){
            var container = FoodItemContainerApply.getRemainder(stack);
            if(event.getEntity() instanceof Player player){
                player.addItem(container);
            }
            else {
                event.getEntity().spawnAtLocation(container);
            }
        }
    }
}
