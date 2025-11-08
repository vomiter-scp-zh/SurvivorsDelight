package com.vomiter.survivorsdelight.data.food;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.Collections;
import java.util.Map;

public final class SDFallbackFoodData {
    private SDFallbackFoodData() { }

    /** 對外只讀視圖；實際內容存於 INTERNAL。 */
    public static final Map<Item, FoodData> MAP;
    private static final Object2ObjectOpenHashMap<Item, FoodData> INTERNAL = new Object2ObjectOpenHashMap<>();

    /** 便捷查詢：若無對應，回傳 FoodData.EMPTY。 */
    public static FoodData get(ItemLike item) {
        return MAP.getOrDefault(item.asItem(), FoodData.EMPTY);
    }

    /** 動態擴充：在其他載入點加入條目。 */
    public static void register(ItemLike item, FoodData data) {
        INTERNAL.put(item.asItem(), data);
    }

    private static FoodData fd(double h, double w, double s,
                               double gr, double fr, double ve, double pr, double da,
                               double decay) {
        return new FoodData(
                (int) Math.round(h), // hunger
                (float) w,           // water
                (float) s,           // saturation
                (float) gr,          // grain
                (float) fr,          // fruit
                (float) ve,          // vegetables
                (float) pr,          // protein
                (float) da,          // dairy
                (float) decay        // decayModifier
        );
    }

    static {
        // farmersdelight/apple_cider.json
        INTERNAL.put(ModItems.APPLE_CIDER.get(), fd(4, 5, 2, 0, 2, 0, 0, 0, 1));
        // farmersdelight/apple_pie.json
        INTERNAL.put(ModItems.APPLE_PIE.get(), fd(8, 0, 2, 2, 2, 0, 0, 0, 1.5));
        // farmersdelight/apple_pie_slice.json
        INTERNAL.put(ModItems.APPLE_PIE_SLICE.get(), fd(2, 0, 1, 0.5, 0.5, 0, 0, 0, 1.5));
        // farmersdelight/bacon.json
        INTERNAL.put(ModItems.BACON.get(), fd(2, 0, 0, 0, 0, 0, 0.5, 0, 3));
        // farmersdelight/bacon_and_eggs.json
        INTERNAL.put(ModItems.BACON_AND_EGGS.get(), fd(5, 0, 3, 0, 0, 0, 3.5, 0.6, 1.5));
        // farmersdelight/bacon_sandwich.json
        INTERNAL.put(ModItems.BACON_SANDWICH.get(), fd(5, 0, 3, 1.5, 0, 2, 1.5, 0, 1.25));
        // farmersdelight/baked_cod.json
        INTERNAL.put(ModItems.BAKED_COD_STEW.get(), fd(6, 0, 2, 0, 0, 3, 2, 0, 1.25));
        // farmersdelight/barbecue_stick.json
        INTERNAL.put(ModItems.BARBECUE_STICK.get(), fd(6, 0, 2.5, 0, 0, 2.5, 1, 0, 2));
        // farmersdelight/beef_patty.json
        INTERNAL.put(ModItems.BEEF_PATTY.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 1));
        // farmersdelight/beef_stew.json
        INTERNAL.put(ModItems.BEEF_STEW.get(), fd(6, 0, 3, 0, 0, 2, 1.5, 0, 1));
        // farmersdelight/beetroot_crate.json
        INTERNAL.put(ModItems.BEETROOT_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 0.5));
        // farmersdelight/bone_broth.json
        INTERNAL.put(ModItems.BONE_BROTH.get(), fd(6, 0, 2, 0, 0, 0.5, 1, 0, 1));
        // farmersdelight/cabbage.json
        INTERNAL.put(ModItems.CABBAGE.get(), fd(4, 0, 0.5, 0, 0, 1, 0, 0, 1.2));
        // farmersdelight/cabbage_crate.json
        INTERNAL.put(ModItems.CABBAGE_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 1.2));
        // farmersdelight/cabbage_leaf.json
        INTERNAL.put(ModItems.CABBAGE_LEAF.get(), fd(2, 0, 0.5, 0, 0, 0.5, 0, 0, 1.2));
        // farmersdelight/cabbage_rolls.json
        INTERNAL.put(ModItems.CABBAGE_ROLLS.get(), fd(4, 0, 2, 0, 0, 1, 1, 0, 1.5));
        // farmersdelight/cake_slice.json
        INTERNAL.put(ModItems.CAKE_SLICE.get(), fd(2, 0, 1, 0.5, 0.5, 0, 0, 0, 1.5));
        // farmersdelight/carrot_crate.json
        INTERNAL.put(ModItems.CARROT_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 0.5));
        // farmersdelight/chicken_cuts.json
        INTERNAL.put(ModItems.CHICKEN_CUTS.get(), fd(2, 0, 0, 0, 0, 0, 0.75, 0, 3));
        // farmersdelight/chicken_sandwich.json
        INTERNAL.put(ModItems.CHICKEN_SANDWICH.get(), fd(5, 0, 2, 1.5, 0, 2, 1.5, 0, 1.25));
        // farmersdelight/chicken_soup.json
        INTERNAL.put(ModItems.CHICKEN_SOUP.get(), fd(5, 0, 2, 0, 0, 3, 1.5, 0, 1));
        // farmersdelight/chocolate_pie.json
        INTERNAL.put(ModItems.CHOCOLATE_PIE.get(), fd(8, 0, 1, 2, 2, 0, 0, 0, 1.5));
        // farmersdelight/chocolate_pie_slice.json
        INTERNAL.put(ModItems.CHOCOLATE_PIE_SLICE.get(), fd(2, 0, 1, 0.5, 0.5, 0, 0, 0, 1.5));
        // farmersdelight/cod_roll.json
        INTERNAL.put(ModItems.COD_ROLL.get(), fd(2, 0, 1, 0.5, 0, 0, 1, 0, 2.25));
        // farmersdelight/cod_slice.json
        INTERNAL.put(ModItems.COD_SLICE.get(), fd(1, 0, 0, 0, 0, 0, 0.5, 0, 3));
        // farmersdelight/cooked_bacon.json
        INTERNAL.put(ModItems.COOKED_BACON.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 2));
        // farmersdelight/cooked_chicken_cuts.json
        INTERNAL.put(ModItems.COOKED_CHICKEN_CUTS.get(), fd(4, 0, 1, 0, 0, 0, 1, 0, 1));
        // farmersdelight/salmon_slice.json
        INTERNAL.put(ModItems.COOKED_COD_SLICE.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 2));
        // farmersdelight/cooked_mutton_chop.json
        INTERNAL.put(ModItems.COOKED_MUTTON_CHOPS.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 2));
        // farmersdelight/cooked_rice.json
        INTERNAL.put(ModItems.COOKED_RICE.get(), fd(4, 0, 2, 1, 0, 0, 0, 0, 1.5));
        // farmersdelight/cooked_salmon_slice.json
        INTERNAL.put(ModItems.COOKED_SALMON_SLICE.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 2));
        // farmersdelight/dog_food.json
        INTERNAL.put(ModItems.DOG_FOOD.get(), fd(1, 0, 0, 0, 0, 0, 0, 0, 1));
        // farmersdelight/dumplings.json
        INTERNAL.put(ModItems.DUMPLINGS.get(), fd(4, 0, 2, 1.5, 0, 2, 1.5, 0, 1.25));
        // farmersdelight/egg_sandwich.json
        INTERNAL.put(ModItems.EGG_SANDWICH.get(), fd(5, 0, 3, 1.5, 0, 0, 3, 0.6, 1.25));
        // farmersdelight/fish_stew.json
        INTERNAL.put(ModItems.FISH_STEW.get(), fd(5, 0, 2, 0, 0, 2, 2.5, 0, 1.25));
        // farmersdelight/cooked_egg.json
        INTERNAL.put(ModItems.FRIED_EGG.get(), fd(4, 0, 0.5, 0, 0, 0, 1.5, 0.25, 4));
        // farmersdelight/fried_rice.json
        INTERNAL.put(ModItems.FRIED_RICE.get(), fd(5, 0, 2, 2.5, 0, 2, 1, 0, 1.25));
        // farmersdelight/fruit_salad.json
        INTERNAL.put(ModItems.FRUIT_SALAD.get(), fd(5, 0, 1, 0, 3.5, 0, 0, 0, 2));
        // farmersdelight/glow_berry_custrad.json
        INTERNAL.put(ModItems.GLOW_BERRY_CUSTARD.get(), fd(4, 3, 1, 0, 1, 0, 0, 0, 1.5));
        // farmersdelight/grilled_salmon.json
        INTERNAL.put(ModItems.GRILLED_SALMON.get(), fd(6, 0, 3, 0, 1, 2, 1, 0, 1.25));
        // farmersdelight/ham.json
        INTERNAL.put(ModItems.HAM.get(), fd(5, 0, 0, 0, 0, 0, 2.5, 0, 3));
        // farmersdelight/hamburger.json
        INTERNAL.put(ModItems.HAMBURGER.get(), fd(6, 0, 2, 1.5, 0, 3, 2, 0, 1.25));
        // farmersdelight/honey_cookie.json
        INTERNAL.put(ModItems.HONEY_COOKIE.get(), fd(2, 0, 1, 1, 0, 0, 0, 0, 1.25));
        // farmersdelight/honey_glazed_ham.json
        INTERNAL.put(ModItems.HONEY_GLAZED_HAM.get(), fd(12, 0, 2, 0, 0, 4, 3, 1, 1));
        // farmersdelight/honey_glazed_ham_block.json
        INTERNAL.put(ModItems.HONEY_GLAZED_HAM_BLOCK.get(), fd(12, 0, 2, 0, 0, 4, 3, 1, 1));
        // farmersdelight/hot_cocoa.json
        INTERNAL.put(ModItems.HOT_COCOA.get(), fd(2, 3, 2, 0, 0, 0, 0, 2, 2));
        // farmersdelight/kelp_roll.json
        INTERNAL.put(ModItems.KELP_ROLL.get(), fd(5, 0, 2, 1.5, 0, 1.5, 3, 0, 2));
        // farmersdelight/kelp_roll_slice.json
        INTERNAL.put(ModItems.KELP_ROLL_SLICE.get(), fd(2, 0, 1, 0.5, 0, 0.5, 1, 0, 2.25));
        // farmersdelight/melon_juice.json
        INTERNAL.put(ModItems.MELON_JUICE.get(), fd(2, 20, 1, 0, 2, 0, 0, 0, 2));
        // farmersdelight/melon_popsicle.json
        INTERNAL.put(ModItems.MELON_POPSICLE.get(), fd(2, 5, 1, 0, 1, 0, 0, 0, 2));
        // farmersdelight/minced_beef.json
        INTERNAL.put(ModItems.MINCED_BEEF.get(), fd(2, 0, 0, 0, 0, 0, 0.5, 0, 3));
        // farmersdelight/mixed_salad.json
        INTERNAL.put(ModItems.MIXED_SALAD.get(), fd(5, 0, 1, 0, 0, 3.5, 0, 0, 1.25));
        // farmersdelight/mushroom_rice.json
        INTERNAL.put(ModItems.MUSHROOM_RICE.get(), fd(5, 0, 2, 2, 0, 1, 1, 0, 1.25));
        // farmersdelight/mutton_chop.json
        INTERNAL.put(ModItems.MUTTON_CHOPS.get(), fd(2, 0, 0, 0, 0, 0, 0.5, 0, 3));
        // farmersdelight/mutton_wrap.json
        INTERNAL.put(ModItems.MUTTON_WRAP.get(), fd(5, 0, 2, 1.5, 0, 2, 2, 0, 1.25));
        // farmersdelight/nether_salad.json
        INTERNAL.put(ModItems.NETHER_SALAD.get(), fd(4, 0, 2, 0, 0, 0, 0, 0, 1));
        // farmersdelight/noodle_soup.json
        INTERNAL.put(ModItems.NOODLE_SOUP.get(), fd(5, 0, 2, 1, 0, 1, 2, 1, 1));
        // farmersdelight/onion.json
        INTERNAL.put(ModItems.ONION.get(), fd(4, 0, 0.5, 0, 0, 1, 0, 0, 0.5));
        // farmersdelight/onion_crate.json
        INTERNAL.put(ModItems.ONION_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 0.35));
        // farmersdelight/pasta_with_meatballs.json
        INTERNAL.put(ModItems.PASTA_WITH_MEATBALLS.get(), fd(6, 0, 2, 2, 0, 2, 2, 0, 1.5));
        // farmersdelight/pasta_with_mutton_chop.json
        INTERNAL.put(ModItems.PASTA_WITH_MUTTON_CHOP.get(), fd(6, 0, 3, 2, 0, 2, 2, 0, 1.5));
        // farmersdelight/pie_crust.json
        INTERNAL.put(ModItems.PIE_CRUST.get(), fd(4, 0, 0, 0, 0, 0, 0, 0, 2.5));
        // farmersdelight/potato_crate.json
        INTERNAL.put(ModItems.POTATO_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 0.4));
        // farmersdelight/pumpkin_slice.json
        INTERNAL.put(ModItems.PUMPKIN_SLICE.get(), fd(1, 0, 0.25, 0, 0, 0, 0, 0, 1.5));
        // farmersdelight/pumpkin_soup.json
        INTERNAL.put(ModItems.PUMPKIN_SOUP.get(), fd(6, 0, 2, 0, 0, 3, 2, 0, 1.25));
        // farmersdelight/ratatouille.json
        INTERNAL.put(ModItems.RATATOUILLE.get(), fd(6, 0, 2, 0, 0, 4, 0, 0, 1.25));
        // farmersdelight/raw_pasta.json
        INTERNAL.put(ModItems.RAW_PASTA.get(), fd(2, 0, 0, 0.5, 0, 0, 0, 0, 3));
        // farmersdelight/rice_grain.json
        INTERNAL.put(ModItems.RICE.get(), fd(2, 0, 0.5, 0, 0, 0, 0, 0, 0.25));
        // farmersdelight/rice_bag.json
        INTERNAL.put(ModItems.RICE_BAG.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 0.2));
        // farmersdelight/rice_roll_medley_block.json
        INTERNAL.put(ModItems.RICE_ROLL_MEDLEY_BLOCK.get(), fd(8, 0, 2, 2, 0, 2, 3, 0, 1));
        // farmersdelight/roast_chicken.json
        INTERNAL.put(ModItems.ROAST_CHICKEN.get(), fd(5, 0, 2, 1.5, 0, 3.5, 3, 0.3, 1.5));
        // farmersdelight/roast_chicken_block.json
        INTERNAL.put(ModItems.ROAST_CHICKEN_BLOCK.get(), fd(8, 0, 2, 1.5, 0, 3.5, 3, 0.3, 1));
        // farmersdelight/roasted_mutton_chops.json
        INTERNAL.put(ModItems.ROASTED_MUTTON_CHOPS.get(), fd(6, 0, 2, 1, 0, 2, 1, 0, 1.25));
        // farmersdelight/salmon_roll.json
        INTERNAL.put(ModItems.SALMON_ROLL.get(), fd(4, 0, 2, 0.5, 0, 0, 1, 0, 2.25));
        // farmersdelight/cooked_cod_slice.json
        INTERNAL.put(ModItems.SALMON_SLICE.get(), fd(2, 0, 1, 0, 0, 0, 1, 0, 2));
        // farmersdelight/shepherds_pie.json
        INTERNAL.put(ModItems.SHEPHERDS_PIE.get(), fd(6, 0, 2, 0, 0, 4, 3, 1, 1.25));
        // farmersdelight/shepherds_block.json
        INTERNAL.put(ModItems.SHEPHERDS_PIE_BLOCK.get(), fd(8, 0, 2, 0, 0, 4, 3, 1, 1));
        // farmersdelight/smoked_ham.json
        INTERNAL.put(ModItems.SMOKED_HAM.get(), fd(5, 0, 2, 0, 0, 0, 5, 0, 1.5));
        // farmersdelight/squid_ink_pasta.json
        INTERNAL.put(ModItems.SQUID_INK_PASTA.get(), fd(6, 0, 2.5, 1, 0, 1, 1, 0, 1.25));
        // farmersdelight/steak_and_potatoes.json
        INTERNAL.put(ModItems.STEAK_AND_POTATOES.get(), fd(6, 0, 2.5, 1, 0, 2, 2, 0, 1.25));
        // farmersdelight/stuffed_potato.json
        INTERNAL.put(ModItems.STUFFED_POTATO.get(), fd(5, 0, 2, 0, 0, 1, 1, 1, 1.5));
        // farmersdelight/stuffed_pumpkin.json
        INTERNAL.put(ModItems.STUFFED_PUMPKIN.get(), fd(6, 0, 2, 1.5, 1.5, 3, 0, 0, 1.5));
        // farmersdelight/stuffed_pumpkin_block.json
        INTERNAL.put(ModItems.STUFFED_PUMPKIN_BLOCK.get(), fd(8, 0, 2, 1.5, 1, 3, 0, 0, 1.5));
        // farmersdelight/sweet_berry_cheesecake.json
        INTERNAL.put(ModItems.SWEET_BERRY_CHEESECAKE.get(), fd(8, 0, 1, 2, 2, 0, 0, 0, 1.5));
        // farmersdelight/sweet_berry_cheesecake_slice.json
        INTERNAL.put(ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get(), fd(2, 0, 1, 0.5, 0.5, 0, 0, 0, 1.5));
        // farmersdelight/sweet_berry_cookie.json
        INTERNAL.put(ModItems.SWEET_BERRY_COOKIE.get(), fd(2, 0, 1, 1, 1, 0, 0, 0, 1.25));
        // farmersdelight/tomato.json
        INTERNAL.put(ModItems.TOMATO.get(), fd(4, 5, 0.5, 0, 0, 1.5, 0, 0, 3.5));
        // farmersdelight/tomato_crate.json
        INTERNAL.put(ModItems.TOMATO_CRATE.get(), fd(0, 0, 0, 0, 0, 0, 0, 0, 3.5));
        // farmersdelight/tomato_sauce.json
        INTERNAL.put(ModItems.TOMATO_SAUCE.get(), fd(2, 0, 0.5, 0, 0, 1, 0, 0, 1.25));
        // farmersdelight/vegetable_noodles.json
        INTERNAL.put(ModItems.VEGETABLE_NOODLES.get(), fd(5, 0, 2, 1, 0, 4, 0, 0, 1));
        // farmersdelight/vegetable_soup.json
        INTERNAL.put(ModItems.VEGETABLE_SOUP.get(), fd(5, 0, 2, 0, 0, 3.5, 0, 0, 1.25));
        // farmersdelight/dough.json
        INTERNAL.put(ModItems.WHEAT_DOUGH.get(), fd(4, 0, 0, 0, 0, 0, 0, 0, 3));
        MAP = Collections.unmodifiableMap(INTERNAL);
    }
}