package com.vomiter.survivorsdelight.data.book;

import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;

import java.util.Objects;

public final class SDPatchouliContent {
    private SDPatchouliContent() {}

    public static void accept(SDPatchouliCategoryProvider cats, SDPatchouliEntryProvider entries) {
        // Category
        cats.category(
                CategoryJson.builder("survivors_delight")
                        .setName("Survivor's Delight")
                        .setDescription("Survive in Terrafirmacraft world as a farmer with delight.")
                        .setIcon(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getId()).toString())
                        .setSortnum(10)
                        .build()
        );

        // Entry 1
        entries.entry(
                EntryJson.builder("skillet")
                        .setName("Skillet")
                        .setCategory("tfc:survivors_delight")
                        .setIcon(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getId()))
                        .setReadByDefault(true)
                        .setSortnum(1)
                        .addTextPage("You can use a skillet to $(l:mechanics/heating)heat$() things up. Skillets can be used as a placed block or a held item.")
                        .addTextPage(
                                "To use a skillet as a held item, hold skillet in the main hand and food ingredient in the other, and stand close to a heat source. "
                                        + "Then you can hold $(item)$(k:key.use)$() to heat 1 item of the ingredient stack. "
                                        + "This process consumes durability of the skillet. "
                        )
                        .addTextPage(
                                "A skillet placed on a valid heat source can cook a stack of 8 item at once. "
                                        + "However, skillets in this form take extra damage if the heat source is hotter than its $(l:mechanics/anvils#working)workable$() temperature. "
                        )
                        .addSingleBlockPage("Skillet variants", "#survivorsdelight:skillets")

                        .addTextPage(
                                "Valid metal material for a skillet includes copper, cast iron, steel, black steel, red steel and blue steel. "
                                        + "A copper skillet needs to be $(l:mechanics/anvils#welding)welded with extra lining. "
                                        + "Skillets made of steel and better material can be used as a melee weapon to deal $(l:mechanics/damage_types)crushing damage$(). "
                        )
                        .addTextPage(
                                "Red steel and blue steel skillets are somehow special. You may try using it when targeting a lava source."
                        )
                        .build()
        );

        // Entry 2
        entries.entry(
                EntryJson.builder("stove")
                        .setName("Stove")
                        .setCategory("tfc:survivors_delight")
                        .setIcon("farmersdelight:stove")
                        .setReadByDefault(true)
                        .setSortnum(2)
                        .addTextPage(
                                "A farmer's stove is an advanced way to utilize the $(l:mechanics/heating)heat$(). "
                                + "This block can store logs, coal and charcoal as fuel for cooking or providing heat for other cooking devices."
                                + "When it's lit but not actively used in cooking, the heat consumption is minimal."
                        )
                        .addSingleBlockPage("Stove", "farmersdelight:stove")
                        .addTextPage(
                                "If you have firmalife installed, a farmer's stove can also be used as the oven bottom."
                                + "This includes heating up the oven top or a ceramic pot."
                        )
                        .build()
                        );

        // Entry 3
        entries.entry(
                EntryJson.builder("cabinet")
                        .setName("Cabinet")
                        .setCategory("tfc:survivors_delight")
                        .setIcon("survivorsdelight:planks/cabinet/ash")
                        .setReadByDefault(true)
                        .setSortnum(3)
                        .addTextPage(
                                "A farmer's cabinet is an advanced way to $(l:mechanics/decay)store food$(). "
                                    + "You may press $(item)$(k:key.use)$() on it with tallow bucket or beeswax to treat it."
                                    + "A treated cabinet can preserve food for a longer period."
                        )
                        .build()
        );

        //TODO: cooking pot, food container, farming (rich soil and mushrooms), farmer's dishes(food, block food and rotten tomato)
    }
}
