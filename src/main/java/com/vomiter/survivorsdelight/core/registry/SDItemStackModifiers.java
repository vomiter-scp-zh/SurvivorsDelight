package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.core.registry.recipe.outputs.ReturnFoodContainerModifier;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class SDItemStackModifiers {

    public static void register() {
        ItemStackModifiers.register(RLUtils.build("return_food_container"), ReturnFoodContainerModifier.INSTANCE);
    }

    public static void onCommonSetUp(FMLCommonSetupEvent event){
        event.enqueueWork(SDItemStackModifiers::register);
    }
}
