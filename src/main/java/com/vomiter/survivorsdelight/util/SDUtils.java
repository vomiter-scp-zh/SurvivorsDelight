package com.vomiter.survivorsdelight.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class SDUtils {
    public static boolean isFromMod(ItemStack stack, String modid) {
        return stack.getItem()
                .builtInRegistryHolder()
                .key()
                .location()
                .getNamespace()
                .equals(modid);
    }

    public static Holder<Enchantment> getEnchantHolder(Level level, ResourceKey<Enchantment> key) {
        return level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
    }
}
