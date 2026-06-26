package com.vomiter.survivorsdelight.common.food;

import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import net.dries007.tfc.common.TFCTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import vectorwing.farmersdelight.common.item.ConsumableItem;

public class FoodUseFinishEvent {
    public static void onFoodUseFinish(LivingEntityUseItemEvent.Finish event){
        ItemStack stack = event.getItem();
        if(stack.getItem() instanceof ConsumableItem) return;
        if(stack.is(TFCTags.Items.SOUPS) || stack.is(TFCTags.Items.SALADS)) return;
        var container = FoodItemContainerApply.getRemainder(stack);
        if(event.getEntity() instanceof Player player){
            player.addItem(container);
        }
        else {
            event.getEntity().spawnAtLocation(container);
        }
    }
}
