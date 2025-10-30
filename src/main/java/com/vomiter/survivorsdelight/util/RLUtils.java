package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("all")
public class RLUtils {
    public static ResourceLocation build(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation build(String path){
        return new ResourceLocation(SurvivorsDelight.MODID, path);
    }
}