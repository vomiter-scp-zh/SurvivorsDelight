package com.vomiter.survivorsdelight.util;

import net.minecraft.world.item.ItemStack;

public class SDUtils {
    public static boolean isFromMod(ItemStack stack, String modid) {
        return stack.getItem()
                .builtInRegistryHolder()
                .key()
                .location()
                .getNamespace()
                .equals(modid);
    }
}
