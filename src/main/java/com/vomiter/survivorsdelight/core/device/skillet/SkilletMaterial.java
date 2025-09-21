package com.vomiter.survivorsdelight.core.device.skillet;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public enum SkilletMaterial {
    COPPER("copper", 1,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/copper"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/copper"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/hickory")
            )
    ),
    COPPER_SILVER("copper_silver", 600,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/copper"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/silver"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/hickory")
            )
    ),
    COPPER_TIN("copper_tin", 600,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/copper"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/tin"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/hickory")
            )

    ),
    CAST_IRON("cast_iron", 550,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/cast_iron"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/cast_iron"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/oak")
            )
    ),
    STEEL("steel", 3300, 5.75f, 1,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/steel"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/steel"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/chestnut")
            )
    ),
    BLACK_STEEL("black_steel", 4200, 7, 1.5f,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/black_steel"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/black_steel"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/wood/planks/douglas_fir")
            )
    ),
    RED_STEEL("red_steel", 6500, 9, 2f,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/red_steel"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/red_steel"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/devices/crucible/side")
            )
    ),
    BLUE_STEEL("blue_steel", 6500, 9, 2f,
            Map.of(
                    "0", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/blue_steel"),
                    "1", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/metal/smooth/blue_steel"),
                    "2", ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID, "block/devices/crucible/side")
            )
    );
    public final String material;
    public final int durability;
    public final boolean isWeapon;
    public final float attackDamage;
    public final float attackKnockback;
    public final Map<String, ResourceLocation> textures;

    SkilletMaterial(String material, int durability, Map<String, ResourceLocation> textures) {
        this.material = material;
        this.durability = durability;
        this.isWeapon = false;
        this.attackDamage = 0;
        this.attackKnockback = 0;
        this.textures = textures;
    }

    SkilletMaterial(String material, int durability, float attackDamage, float attackKnockback, Map<String, ResourceLocation> textures) {
        this.material = material;
        this.durability = durability;
        this.isWeapon = true;
        this.attackDamage = attackDamage;
        this.attackKnockback = attackKnockback;
        this.textures = textures;
    }

    public String path() { return "skillet/" + material; }
    public ResourceLocation location() { return ResourceLocation.fromNamespaceAndPath(SurvivorsDelight.MODID, path()); }

    public String path_head(){ return "skillet_head/" + material;}
    public String path_uf(){ return "unfinished_skillet/" + material;}

}

