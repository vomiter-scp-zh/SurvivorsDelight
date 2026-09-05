package com.vomiter.survivorsdelight.util;

import net.minecraft.world.item.ItemStack;

public class SDThreadLocals {
    public static final ThreadLocal<ItemStack> finishUsedItem = ThreadLocal.withInitial(()->ItemStack.EMPTY);
    public static final ThreadLocal<Boolean> shouldApplyEating = ThreadLocal.withInitial(()->false);
}
