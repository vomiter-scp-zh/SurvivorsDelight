package com.vomiter.survivorsdelight.core.device.cutting;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CuttingProvidersFromRecipes extends SimpleJsonResourceReloadListener {
    public static final String RECIPES_DIR = "recipes";

    private Map<ResourceLocation, List<ItemStackProvider>> providers = Map.of();

    public CuttingProvidersFromRecipes() {
        super(new GsonBuilder().create(), RECIPES_DIR);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons,
                         ResourceManager rm, ProfilerFiller pf) {
        Map<ResourceLocation, List<ItemStackProvider>> out = new HashMap<>();

        jsons.forEach((fullPath, rootEl) -> {
            // fullPath 例：minecraft:recipes/food/cut_beef.json（已去掉前綴 "recipes/"）
            // SimpleJsonResourceReloadListener 已把目錄切到 "recipes" 下，
            // 這裡的 fullPath 其實已經是像 "minecraft:food/cut_beef"
            try {
                JsonObject obj = GsonHelper.convertToJsonObject(rootEl, "root");

                // 只處理 FD cutting
                String type = GsonHelper.getAsString(obj, "type", "");
                if (!"farmersdelight:cutting".equals(type)) return;

                // 取得 result 欄位（可能是 array 或 object；我們最後轉成 array）
                JsonArray resultArr = asArray(obj.get("result"));
                if (resultArr == null || resultArr.isEmpty()) return;

                List<ItemStackProvider> list = new ArrayList<>(resultArr.size());

                for (JsonElement e : resultArr) {
                    if (!e.isJsonObject()) {
                        // 純 FD 項（ChanceResult 簡寫）→ 不用 provider
                        list.add(null);
                        continue;
                    }

                    JsonObject r = e.getAsJsonObject();
                    boolean hasMods  = r.has("modifiers");
                    boolean hasStack = r.has("stack");

                    if (hasMods) {
                        // 組 provider JSON，優先沿用 "stack" 物件；缺少時兼容 {item,count}
                        JsonObject providerJson = new JsonObject();

                        if (hasStack) {
                            providerJson.add("stack", r.getAsJsonObject("stack"));
                        } else {
                            JsonObject stackObj = new JsonObject();
                            if (r.has("item"))  stackObj.add("item",  r.get("item"));
                            if (r.has("count")) stackObj.add("count", r.get("count"));
                            providerJson.add("stack", stackObj);
                        }

                        providerJson.add("modifiers", GsonHelper.getAsJsonArray(r, "modifiers"));

                        // 1.21 建議用 CODEC；若你的 TFC 還留有 fromJson 也可改那個
                        ItemStackProvider isp = ItemStackProvider.CODEC
                                .parse(JsonOps.INSTANCE, providerJson)
                                .getOrThrow(msg -> new JsonParseException("ItemStackProvider parse failed: " + msg));
                        list.add(isp);
                    } else if (hasStack) {
                        // 有人把 provider 整顆塞在這層（或你想把純 stack 也走 provider）
                        ItemStackProvider isp = ItemStackProvider.CODEC
                                .parse(JsonOps.INSTANCE, r)
                                .getOrThrow(msg -> new JsonParseException("ItemStackProvider parse failed: " + msg));
                        list.add(isp);
                    } else {
                        // 純 FD 結果，不交給 provider
                        list.add(null);
                    }
                }

                // key 要對應實際的 recipe id（SimpleJsonResourceReloadListener 已去掉 ".json"）
                out.put(fullPath, List.copyOf(list));
            } catch (Exception ex) {
                // 建議加上你的 MODID log
                // SurvivorsDelight.LOGGER.warn("Failed parsing providers from {}", fullPath, ex);
            }
        });

        this.providers = Map.copyOf(out);
    }

    private static JsonArray asArray(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) return new JsonArray();
        if (el.isJsonArray()) return el.getAsJsonArray();
        JsonArray one = new JsonArray();
        one.add(el);
        return one;
    }

    public List<ItemStackProvider> get(ResourceLocation recipeId) {
        return providers.getOrDefault(recipeId, List.of());
    }
}
