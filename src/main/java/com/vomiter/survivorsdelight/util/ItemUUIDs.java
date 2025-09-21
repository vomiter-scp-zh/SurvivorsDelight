package com.vomiter.survivorsdelight.util;

import net.minecraft.world.item.Item;

import java.util.UUID;

public abstract class ItemUUIDs extends Item {
    public ItemUUIDs(Properties p_41383_) {
        super(p_41383_);
    }

    public static UUID getBaseAttackDamageUUID(){
        return BASE_ATTACK_DAMAGE_UUID;
    }

    public static UUID getBaseAttackSpeedUUID(){
        return BASE_ATTACK_SPEED_UUID;
    }

}
