package com.vomiter.survivorsdelight.adapter.isp;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ISPConverter {
    /**
     * { item, count?, nbt?, modifiers }  ->  { stack:{item,count?,nbt?}, modifiers:[...] }
     */
    public static ItemStackProvider providerFromLooseJson(JsonObject obj) {
        JsonObject wrapper = new JsonObject();

        // modifiers（必須）
        wrapper.add("modifiers", obj.getAsJsonArray("modifiers"));

        // stack（由 item/count/nbt 組出）
        if (obj.has("stack") && obj.get("stack").isJsonObject()) {
            // 已經是 ISP 標準結構
            wrapper.add("stack", obj.getAsJsonObject("stack"));
        } else {
            JsonObject stack = new JsonObject();
            if (obj.has("item")) stack.add("item", obj.get("item"));
            if (obj.has("count")) stack.add("count", obj.get("count"));
            if (obj.has("nbt")) stack.add("nbt", obj.get("nbt")); // 如需 NBT
            wrapper.add("stack", stack);
        }

        return ItemStackProvider.fromJson(wrapper);
    }
}
