package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.registry.recipe.outputs.ReturnFoodContainerModifier;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class SDItemStackModifiers {

    public static void register() {
        ItemStackModifiers.register(SDUtils.RLUtils.build("return_food_container"), ReturnFoodContainerModifier.INSTANCE);
    }

    public static void onCommonSetUp(FMLCommonSetupEvent event){
        event.enqueueWork(SDItemStackModifiers::register);
    }
}
