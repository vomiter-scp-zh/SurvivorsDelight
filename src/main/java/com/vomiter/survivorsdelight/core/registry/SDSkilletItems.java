package com.vomiter.survivorsdelight.core.registry;

import com.google.common.collect.ImmutableMultimap;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SDSkilletItem;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.item.SkilletItem;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class SDSkilletItems {
    static final ResourceLocation KNOCKBACK_UUID = SDSkilletItem.getKnockbackUUID();

    private SDSkilletItems() {}
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.createItems(SurvivorsDelight.MODID);

    public static final Map<SkilletMaterial, Supplier<Item>> SKILLETS = new EnumMap<>(SkilletMaterial.class);
    public static Supplier<Item> get(SkilletMaterial m){
        return SKILLETS.get(m);
    }
    public static ResourceKey<Item> getKey(SkilletMaterial m){
        return SKILLETS.get(m).get().builtInRegistryHolder().getKey();
    }
    public static Supplier<Item> FARMER;
    static {
        for (SkilletMaterial m : SkilletMaterial.values()) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            Item.Properties properties = new Item.Properties();
            properties.durability(m.durability).stacksTo(1);
            if(m.equals(SkilletMaterial.RED_STEEL) || m.equals(SkilletMaterial.BLUE_STEEL)){
                properties = properties.fireResistant();
            }
            if(m.isWeapon){
                properties.attributes(
                        SDSkilletItem.createAttributes(
                                SkilletItem.SKILLET_TIER,
                                m.attackDamage,
                                m.attackKnockback
                        )
                );
            }
            Item.Properties finalProperties = properties;
            Supplier<Item> ro = ITEMS.register(m.path(), () ->
                    new SDSkilletItem(SDSkilletBlocks.SKILLETS.get(m).get(), finalProperties, m.isWeapon)
            );
            SKILLETS.put(m, ro);
            if(m.equals(SkilletMaterial.STEEL)){
                FARMER = ITEMS.register("skillet/farmer", () ->
                                new SDSkilletItem(SDSkilletBlocks.FARMER.get(), finalProperties, true)
                );
            }
        }
    }
}
