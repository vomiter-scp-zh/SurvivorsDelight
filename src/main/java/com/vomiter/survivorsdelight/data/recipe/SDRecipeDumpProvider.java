package com.vomiter.survivorsdelight.data.recipe;

import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class SDRecipeDumpProvider implements DataProvider {
    private final String modid;

    private final Map<ResourceLocation, JsonObject> entries = new LinkedHashMap<>();
    private final PackOutput.PathProvider recipePathProvider;

    public SDRecipeDumpProvider(PackOutput output) {
        this.modid = SurvivorsDelight.MODID;
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
    }

    private void put(ResourceLocation id, JsonObject json) {
        entries.put(id, json);
    }

    /** 把要輸出的 JSON 都塞進 entries */
    private void build() {
        new SDHeatingRecipes(modid).generate(this::put);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        entries.clear();
        build();

        List<CompletableFuture<?>> futures = new ArrayList<>(entries.size());
        for (Map.Entry<ResourceLocation, JsonObject> e : entries.entrySet()) {
            ResourceLocation id = e.getKey();
            JsonObject json = e.getValue();
            Path path = recipePathProvider.json(id); // data/<ns>/recipes/<path>.json
            futures.add(DataProvider.saveStable(cache, json, path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Recipe Direct Dump: " + modid;
    }
}
