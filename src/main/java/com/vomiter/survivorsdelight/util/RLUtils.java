package com.vomiter.survivorsdelight.util;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("all")
public class RLUtils {
    public static ResourceLocation build(String namespace, String path){
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}