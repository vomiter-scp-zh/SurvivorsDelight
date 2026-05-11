package com.vomiter.survivorsdelight.client;

import com.vomiter.survivorsdelight.registry.skillet.SDSkilletItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class SkilletPredicates {
    public static void addPredicate() {
        SDSkilletItems.SKILLETS.values().forEach(item -> {
            ItemProperties.register(item.get(), new ResourceLocation("cooking"),
                    (stack, world, entity, s) -> stack.getTagElement("Cooking") != null ? 1 : 0);
        });
        ItemProperties.register(SDSkilletItems.FARMER.get(), new ResourceLocation("cooking"),
                (stack, world, entity, s) -> stack.getTagElement("Cooking") != null ? 1 : 0);

    }
}