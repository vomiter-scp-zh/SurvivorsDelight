package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;


public class SDUtils {

    public static Item getTFCFoodItem(Food food){
        return TFCItems.FOOD.get(food).get();
    }

    public static class TagUtils{

        public static TagKey<Item> itemTag(String namespace, String path) {
            return TagKey.create(Registries.ITEM, RLUtils.build(namespace, path));
        }

        public static TagKey<Block> blockTag(String namespace, String path) {
            return TagKey.create(Registries.BLOCK, RLUtils.build(namespace, path));
        }

        public static TagKey<Fluid> fluidTag(String namespace, String path) {
            return TagKey.create(Registries.FLUID, RLUtils.build(namespace, path));
        }
    }

    public static class RLUtils {
        public static ResourceLocation build(String namespace, String path){
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }

        public static ResourceLocation build(String path){
            return ResourceLocation.fromNamespaceAndPath(SurvivorsDelight.MODID, path);
        }
    }
}
