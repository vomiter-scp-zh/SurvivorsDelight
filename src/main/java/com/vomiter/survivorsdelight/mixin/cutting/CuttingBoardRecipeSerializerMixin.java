package com.vomiter.survivorsdelight.mixin.cutting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.core.recipe.cutting.CuttingProvidersHandler;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = CuttingBoardRecipe.Serializer.class, remap = false)
public abstract class CuttingBoardRecipeSerializerMixin {

    @Inject(
            method = "fromJson(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonObject;)Lvectorwing/farmersdelight/common/crafting/CuttingBoardRecipe;",
            at = @At("RETURN")
    )
    private void sdtfc$attachTfcProviders(net.minecraft.resources.ResourceLocation id, JsonObject json, CallbackInfoReturnable<CuttingBoardRecipe> cir) {
        final CuttingBoardRecipe recipe = cir.getReturnValue();
        if (!(recipe instanceof CuttingProvidersHandler cuttingProvidersHandler) || !json.has("result")) return;

        final JsonArray arr = JsonHelpers.getAsJsonArray(json, "result");
        final List<ItemStackProvider> providers = new ArrayList<>(arr.size());

        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            boolean hasMods = obj.has("modifiers");
            boolean hasStack = obj.has("stack");

            if (hasMods) {
                JsonObject providerJson = new JsonObject();

                if (hasStack) {
                    providerJson.add("stack", obj.getAsJsonObject("stack"));
                } else {
                    JsonObject stackObj = new JsonObject();
                    if (obj.has("item")) stackObj.add("item", obj.get("item"));
                    if (obj.has("count")) stackObj.add("count", obj.get("count"));
                    providerJson.add("stack", stackObj);
                }

                providerJson.add("modifiers", obj.getAsJsonArray("modifiers"));
                providers.add(ItemStackProvider.fromJson(providerJson));
            } else if (hasStack) {
                providers.add(ItemStackProvider.fromJson(obj));
            } else {
                providers.add(null); // FD 原生結果，不用 provider
            }
        }

        cuttingProvidersHandler.sdtfc$setProviders(providers);
    }
}