package com.vomiter.survivorsdelight.common.food.block;

import com.vomiter.survivorsdelight.compat.firmalife.FLCompatHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public final class DecayFoodTransfer {
    private DecayFoodTransfer() {}

    /**
     * Copy TFC food state from src to dst.
     * - dynamic food data, optionally nutrient-scaled
     * - traits, replaced
     * - optional Firmalife trait stripping
     * - creation date, copied last
     *
     * Returns dst for chaining.
     */
    public static ItemStack copyFoodState(ItemStack src, ItemStack dst, boolean stripFirmalifeShelvedTraits, float factor) {
        IFood srcFood = FoodCapability.get(src);
        IFood dstFood = FoodCapability.get(dst);
        if (srcFood == null || dstFood == null) {
            return dst;
        }

        /*
         * IMPORTANT:
         * setFoodForDynamicItemOnCreate() resets creation date to now,
         * so it must run before we copy src creation date.
         */
        FoodCapability.setFoodForDynamicItemOnCreate(dst, scaleNutrients(srcFood.getData(), factor));

        /*
         * IFood#getTraits() returns a non-mutable view / immutable component list in TFC 1.21.1.
         * Never call clear(), add(), or addAll() on it.
         *
         * Snapshot both sides first, because apply/remove creates replacement food components.
         */
        List<FoodTrait> dstTraits = new ArrayList<>(dstFood.getTraits());
        List<FoodTrait> srcTraits = new ArrayList<>(srcFood.getTraits());

        // Replace traits: remove current dst traits first.
        for (FoodTrait trait : dstTraits) {
            FoodCapability.removeTrait(dst, holderOf(trait));
        }

        // Then copy src traits.
        for (FoodTrait trait : srcTraits) {
            FoodCapability.applyTrait(dst, holderOf(trait));
        }

        // Remove Firmalife-specific traits if requested.
        if (stripFirmalifeShelvedTraits && ModList.get().isLoaded("firmalife")) {
            for (Holder<FoodTrait> trait : FLCompatHelpers.getPossibleShelvedFoodTraits()) {
                FoodCapability.removeTrait(dst, trait);
            }
        }

        /*
         * Must be last.
         * applyTrait/removeTrait both adjust creation date to preserve decay proportion.
         * Here we want to copy the source state exactly.
         */
        FoodCapability.setCreationDate(dst, srcFood.getCreationDate());

        return dst;
    }

    public static ItemStack copyFoodState(ItemStack src, ItemStack dst, boolean stripFirmalifeShelvedTraits) {
        return copyFoodState(src, dst, stripFirmalifeShelvedTraits, 1.0f);
    }

    private static Holder<FoodTrait> holderOf(FoodTrait trait) {
        return FoodTraits.REGISTRY.wrapAsHolder(trait);
    }

    private static FoodData scaleNutrients(FoodData src, float factor) {
        factor = Math.max(0.0f, factor);

        float[] nutrients = src.nutrients();
        for (int i = 0; i < nutrients.length; i++) {
            nutrients[i] *= factor;
        }

        return new FoodData(
                src.hunger(),
                src.water(),
                src.saturation(),
                src.intoxication(),
                nutrients,
                src.decayModifier()
        );
    }
}