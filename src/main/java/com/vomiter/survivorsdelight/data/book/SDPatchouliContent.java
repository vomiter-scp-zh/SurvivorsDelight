package com.vomiter.survivorsdelight.data.book;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.SDItems;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;
import com.vomiter.survivorsdelight.util.RLUtils;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.Objects;

public final class SDPatchouliContent {
    private SDPatchouliContent() {}

    public static void accept(SDPatchouliCategoryProvider cats, SDPatchouliEntryProvider entries) {

        // Category
        cats.category(
                CategoryJson.builder("survivors_delight")
                        .setName("Survivor's Delight")
                        .setDescription("Survive in Terrafirmacraft world with Farmer's Delight.")
                        .setIcon(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getId()).toString())
                        .setSortnum(10)
                        .build()
        );

        int sortNum = 0;

        // Entry 1
        var text1_1 = TextBuilder.of("You can use a skillet to ").link("mechanics/heating", "heat things up.")
                .appendWithSpace("Skillets can be used as a placed block or as a held item.")
                .appendWithSpace("Skillets made of steel and better material can be used as a melee weapon to deal ").link(TFCGuide.Mechanics.MECHANICS_DAMAGE_TYPES, "crushing damage.");

        var text1_2 = TextBuilder.of("To use a skillet as a held item, hold a skillet in your main hand and a food ingredient in your off-hand, and stand close to a heat source.")
                .appendWithSpace("Then hold $(item)$(k:key.use)$() to heat one item from the ingredient stack.")
                .appendWithSpace("This process consumes the skillet's durability.");

        var text1_3 = TextBuilder.of("A placed skillet can hold up to 8 of the same ingredient and heat them all.")
                .appendWithSpace("This is a more efficient way to cook food.")
                .appendWithSpace("However, it may consume extra durability if the heat source temperature is close to the skillet's ").link(TFCGuide.Mechanics.MECHANICS_ANVILS_WORKING, "melting point");


        var text1_4 = TextBuilder.of("Valid metal material for a skillet includes ")
                        .thing("copper, cast iron, steel, black steel, red steel and blue steel.")
                        .appendWithSpace("A skillet made of better material is more durable.")
                        .appendWithSpace("A skillet made of ")
                        .thing("copper").appendWithSpace("requires extra steps before it becomes a usable cooking utensil.");

        var text1_5 = TextBuilder.of("A skillet stops being usable as a cooking utensil when only one point of durability remains.")
                        .appendWithSpace("A placed skillet will also drop as an item in this state.")
                        .appendWithSpace("Most of skillets can be reforged to restore their full durability, or melted back into their source material without any loss.")
                        .appendWithSpace("Unlike others, a copper skillet only needs re-welding with a layer of lining to become usable again.");

        entries.entry(
                EntryJson.builder("skillet")
                        .setName("Skillet")
                        .setCategory("tfc:survivors_delight")
                        .setIcon(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getId()))
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .addTextPage(text1_1.toString())
                        .addTextPage(text1_2.toString())
                        .addTextPage(text1_3.toString())
                        .addSingleBlockPage("Skillet variants", "#survivorsdelight:skillets")

                    .addTextPage("Make A Skillet", "make_a_skillet", text1_4.toString())
                        .addAnvilRecipe(RLUtils.build(SurvivorsDelight.MODID, "anvil/skillet_head/steel"), "A metal double sheet is forged into a skillet head.")
                        .addWeldingRecipe(RLUtils.build(SurvivorsDelight.MODID, "welding/unfinished_skillet/steel"), "The skillet head is welded with a rod of the same metal.")
                        .addCraftingRecipe(RLUtils.build(SurvivorsDelight.MODID, "crafting/skillet/steel"), RLUtils.build(SurvivorsDelight.MODID, "crafting/skillet/farmer"), "Assemble A Skillet")
                        .addAnvilRecipe(RLUtils.build(SurvivorsDelight.MODID, "anvil/skillet_lining/silver"), "A special lining for copper skillets prevents toxic copper ions from leaching into food. This can be made with $(thing)silver$() or $(thing)tin$(). A copper skillet must be welded with a lining before it becomes a usable cooking utensil.")

                    .addTextPage("Repair A Skillet", "repair", text1_5.toString())
                        .build()
        );

        // Entry 2
        entries.entry(
                EntryJson.builder("stove")
                        .setName("Stove")
                        .setCategory("tfc:survivors_delight")
                        .setIcon("farmersdelight:stove")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .addTextPage(
                                "A stove is an advanced way to harness $(l:mechanics/heating)heat$(). "
                                + "This block accepts $(thing)logs$(), $(thing)coal$(), and $(thing)charcoal$() as fuel for cooking or for providing heat to other cooking devices. "
                                + "When it's lit but not actively cooking, fuel consumption is minimal."
                        )
                        .addSingleBlockPage("Stove", "farmersdelight:stove")
                        .build()
                        );

        // Entry 3
        TextBuilder text3_1 = TextBuilder.of("A cabinet is a wooden container used to preserve food and store cooking utensils.")
                                .appendWithSpace("Use $(item)$(k:key.use)$() on it with a ")
                                .link(TFCGuide.Mechanics.MECHANICS_LAMPS_TALLOW, "bucket of tallow")
                                .appendWithSpace("to treat it.")
                                .appendWithSpace("A treated cabinet can ")
                                .link(TFCGuide.Mechanics.MECHANICS_DECAY, "preserve food")
                                .appendWithSpace("for longer.");
        entries.entry(
                EntryJson.builder("cabinet")
                        .setName("Cabinet")
                        .setCategory("tfc:survivors_delight")
                        .setIcon("survivorsdelight:planks/cabinet/ash")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .addTextPage(text3_1.toString())
                        .build()
        );

        // Entry 4
        var text4_1 = TextBuilder
                .of("A cooking pot is an alternative to the ceramic pot.")
                .appendWithSpace("This device is designed to process food more efficiently.")
                .appendWithSpace("It can cook soup and boil eggs faster.")
                .appendWithSpace("However, it cannot be used for recipes that produce fluid.")
                .appendWithSpace("Besides ceramic pot recipes, there are also several recipes exclusive to the metal cooking pot.");

        var text4_2 = TextBuilder
                .of("A bucket button is located on the left side of the cooking pot's interaction menu.")
                .appendWithSpace("The button opens a barrel‑like menu, letting you put fluid into the cooking pot.")
                .appendWithSpace("The fluid can later be used in a pot or cooking‑pot recipe.");

        var text4_3 = TextBuilder
                .of("Some cooking pot outputs require a food container, which is often a bowl or a glass bottle.")
                .appendWithSpace("Both ceramic and wooden bowls are valid for bowl foods.")
                .appendWithSpace("All four types of glass bottles are valid for bottled drinks as well.");

        entries.entry(
                EntryJson.builder("cooking_pot")
                        .setCategory("tfc:survivors_delight")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .setName("Cooking Pot")
                        .setIcon("farmersdelight:cooking_pot")
                        .addTextPage(text4_1.toString())
                        .addSingleBlockPage("Cooking Pot", "farmersdelight:cooking_pot")
                        .addTextPage(text4_2.toString())
                        .addTextPage(text4_3.toString())
                        .build());

        //Entry 5
        var text5_1 = TextBuilder.of("Farmer's Delight dishes sometimes grant special effects that help you survive in the harsh world.");
        var text5_2 = TextBuilder.of("Nourishment prevents your hunger and thirst from decreasing.")
                .appendWithSpace("However, it stops working if you take damage and natural passive healing is active.")
                .appendWithSpace("It resumes when you return to full health, or when your hunger or thirst is too low for natural healing.");
        var text5_3 = TextBuilder.of("Comfort lets you recover health even when your hunger or thirst is too low for natural regeneration.")
                .appendWithSpace("It also boosts your healing rate to what you'd get at full hunger and thirst.")
                .appendWithSpace("It can work with Nourishment to help you survive difficult situations.");
        var text5_4 = TextBuilder.of("Workhorse allows horses or players to carry more very large or heavy items without becoming overburdened.")
                .append("It can be applied to a horse by feeding it a horse‑feed item if its familiarity is at least 35%.");

        entries.entry(
                EntryJson.builder("effects")
                        .setCategory("tfc:survivors_delight")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .setName("Special Effects")
                        .setIcon(SDItems.EFFECT_NOURISHMENT.getId())
                        .addTextPage(text5_1.toString())
                        .addSpotlightPage(SDItems.EFFECT_NOURISHMENT.getId().toString(), text5_2.toString())
                        .addSpotlightPage(SDItems.EFFECT_COMFORT.getId().toString(), text5_3.toString())
                        .addSpotlightPage(SDItems.EFFECT_WORKHORSE.getId().toString(), text5_4.toString())
                        .build()
        );

        // Entry: Rich Soil
        var text6_1 = TextBuilder.of("Rich Soil is a special type of soil that accelerates natural growth.")
                .appendWithSpace("It shortens the preparation time for saplings to grow.")
                .appendWithSpace("If the block above Rich Soil is air, it has a chance to randomly generate brown or red mushrooms.")
                .appendWithSpace("These mushrooms can later grow into full mushroom colonies over time.");


        var text6_2 = TextBuilder.of("Rich Soil can be tilled with a hoe to create Rich Soil Farmland.")
                .appendWithSpace("Crops planted on Rich Soil Farmland continues growing even when the ambient temperature or moisture slightly deviated from their normal growth range.");


        assert ModBlocks.RICH_SOIL_FARMLAND.getId() != null;
        assert ModBlocks.RICH_SOIL.getId() != null;
        entries.entry(
                EntryJson.builder("rich_soil")
                        .setCategory("tfc:survivors_delight")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .setName("Rich Soil")
                        .setIcon(ModBlocks.RICH_SOIL_FARMLAND.getId())
                        .addTextPage(text6_1.toString())
                        .addSingleBlockPage("Rich Soil", ModBlocks.RICH_SOIL.getId().toString())
                        .addTextPage(text6_2.toString())
                        .addSingleBlockPage("Rich Soil Farmland", ModBlocks.RICH_SOIL_FARMLAND.getId().toString())
                        .build()
        );

        /*
                entries.entry(
                EntryJson.builder("")
                        .setCategory("tfc:survivors_delight")
                        .setReadByDefault(true)
                        .setSortnum(++sortNum)
                        .setName("")
                        .setIcon("")
                        .addTextPage(
                                "A farmer's cabinet is an advanced way to $(l:mechanics/decay)store food$(). "
                                    + "You may press $(item)$(k:key.use)$() on it with tallow bucket or beeswax to treat it."
                                    + "A treated cabinet can preserve food for a longer period."
                        )
                        .build());

         */
    }
}
