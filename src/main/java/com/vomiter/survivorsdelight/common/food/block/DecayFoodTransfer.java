package com.vomiter.survivorsdelight.common.food.block;

import com.vomiter.survivorsdelight.compat.firmalife.FLCompatHelpers;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public final class DecayFoodTransfer {
    private DecayFoodTransfer() {}

    /**
     * Copy TFC food state from src to dst.
     * - creation date
     * - traits (replace)
     * - dynamic food data (replace)
     * - optional Firmalife trait stripping
     *
     * Returns dst for chaining.
     */
    public static ItemStack copyFoodState(ItemStack src, ItemStack dst, boolean stripFirmalifeShelvedTraits) {
        IFood srcFood = FoodCapability.get(src);
        IFood dstFood = FoodCapability.get(dst);
        if (srcFood == null || dstFood == null) return dst;

        // creation date
        dstFood.setCreationDate(srcFood.getCreationDate());

        // traits: replace (avoid duplication)
        dstFood.getTraits().clear();
        dstFood.getTraits().addAll(srcFood.getTraits());

        // remove Firmalife-specific traits if requested
        if (stripFirmalifeShelvedTraits && ModList.get().isLoaded("firmalife")) {
            for (FoodTrait t : FLCompatHelpers.getPossibleShelvedFoodTraits()) {
                FoodCapability.removeTrait(dstFood, t);
            }
        }

        // dynamic food data: replace
        if (dstFood instanceof FoodHandler.Dynamic dynamic) {
            dynamic.setFood(srcFood.getData());
        }

        return dst;
    }
}
