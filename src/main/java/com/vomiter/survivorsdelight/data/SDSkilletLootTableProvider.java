package com.vomiter.survivorsdelight.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SDSkilletLootTableProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;

    public SDSkilletLootTableProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var pathProvider = this.output.createPathProvider(
                PackOutput.Target.DATA_PACK, "loot_tables/blocks"
        );

        CompletableFuture<?>[] tasks = java.util.Arrays.stream(SkilletMaterial.values())
                .map(metal -> {
                    ResourceLocation id = RLUtils.build(
                            SurvivorsDelight.MODID, "skillet/" + metal.material
                    );
                    Path path = pathProvider.json(id);
                    JsonObject json = buildSkilletLootJson(metal.material);
                    return DataProvider.saveStable(cache, json, path);
                })
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(tasks);
    }

    @Override
    public String getName() {
        return "SurvivorsDelight Skillet Block LootTables";
    }

    private static JsonObject buildSkilletLootJson(String material) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("name", "pool1");
        pool.addProperty("rolls", 1);

        // entries
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "survivorsdelight:skillet/"+material);

        // functions
        JsonArray functions = new JsonArray();
        JsonObject func = new JsonObject();
        func.addProperty("function", "farmersdelight:copy_skillet");
        func.addProperty("source", "block_entity");
        functions.add(func);
        entry.add("functions", functions);

        entries.add(entry);
        pool.add("entries", entries);

        // conditions
        JsonArray conditions = new JsonArray();
        JsonObject cond = new JsonObject();
        cond.addProperty("condition", "minecraft:survives_explosion");
        conditions.add(cond);
        pool.add("conditions", conditions);

        pools.add(pool);
        root.add("pools", pools);

        return root;
    }
}
