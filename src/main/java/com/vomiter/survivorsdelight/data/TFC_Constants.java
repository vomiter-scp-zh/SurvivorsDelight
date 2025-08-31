package com.vomiter.survivorsdelight.data;

import java.util.Map;

public abstract class TFC_Constants {

    public static String MODID = "tfc";

    public enum RockCategory {
        IGNEOUS_INTRUSIVE,
        IGNEOUS_EXTRUSIVE,
        SEDIMENTARY,
        METAMORPHIC
    }

    public enum SandColor {
        WHITE,
        BLACK,
        BROWN,
        GREEN,
        YELLOW,
        RED
    }

    public static final record Rock(RockCategory category, SandColor sand) {}

    public static final Map<String, Rock> ROCKS = Map.ofEntries(
            Map.entry("granite",      new Rock(RockCategory.IGNEOUS_INTRUSIVE, SandColor.WHITE)),
            Map.entry("diorite",      new Rock(RockCategory.IGNEOUS_INTRUSIVE, SandColor.WHITE)),
            Map.entry("gabbro",       new Rock(RockCategory.IGNEOUS_INTRUSIVE, SandColor.BLACK)),
            Map.entry("shale",        new Rock(RockCategory.SEDIMENTARY,       SandColor.BLACK)),
            Map.entry("claystone",    new Rock(RockCategory.SEDIMENTARY,       SandColor.BROWN)),
            Map.entry("limestone",    new Rock(RockCategory.SEDIMENTARY,       SandColor.WHITE)),
            Map.entry("conglomerate", new Rock(RockCategory.SEDIMENTARY,       SandColor.GREEN)),
            Map.entry("dolomite",     new Rock(RockCategory.SEDIMENTARY,       SandColor.BLACK)),
            Map.entry("chert",        new Rock(RockCategory.SEDIMENTARY,       SandColor.YELLOW)),
            Map.entry("chalk",        new Rock(RockCategory.SEDIMENTARY,       SandColor.WHITE)),
            Map.entry("rhyolite",     new Rock(RockCategory.IGNEOUS_EXTRUSIVE, SandColor.RED)),
            Map.entry("basalt",       new Rock(RockCategory.IGNEOUS_EXTRUSIVE, SandColor.RED)),
            Map.entry("andesite",     new Rock(RockCategory.IGNEOUS_EXTRUSIVE, SandColor.RED)),
            Map.entry("dacite",       new Rock(RockCategory.IGNEOUS_EXTRUSIVE, SandColor.YELLOW)),
            Map.entry("quartzite",    new Rock(RockCategory.METAMORPHIC,       SandColor.WHITE)),
            Map.entry("slate",        new Rock(RockCategory.METAMORPHIC,       SandColor.YELLOW)),
            Map.entry("phyllite",     new Rock(RockCategory.METAMORPHIC,       SandColor.BROWN)),
            Map.entry("schist",       new Rock(RockCategory.METAMORPHIC,       SandColor.GREEN)),
            Map.entry("gneiss",       new Rock(RockCategory.METAMORPHIC,       SandColor.GREEN)),
            Map.entry("marble",       new Rock(RockCategory.METAMORPHIC,       SandColor.YELLOW))
    );
}
