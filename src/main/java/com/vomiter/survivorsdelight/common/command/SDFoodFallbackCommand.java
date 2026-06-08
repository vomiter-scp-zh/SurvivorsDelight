package com.vomiter.survivorsdelight.common.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SDFoodFallbackCommand {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String PACK_FOLDER_NAME = "survivors-delight-food-fallback";
    private static final String DATA_FOLDER = "survivorsdelight";
    private static final String FOOD_FALLBACK_FOLDER = "food_fallback";

    private SDFoodFallbackCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sd_food_fallback")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("export")
                                .executes(context -> exportMainHandFoodFallback(context.getSource()))
                        )
        );
    }

    private static JsonObject foodDataToJson(FoodData data) {
        JsonObject json = new JsonObject();

        json.addProperty("hunger", data.hunger());
        json.addProperty("saturation", data.saturation());
        json.addProperty("water", data.water());
        json.addProperty("decay_modifier", data.decayModifier());

        json.addProperty("grain", data.grain());
        json.addProperty("fruit", data.fruit());
        json.addProperty("vegetables", data.vegetables());
        json.addProperty("protein", data.protein());
        json.addProperty("dairy", data.dairy());

        return json;
    }

    private static int exportMainHandFoodFallback(CommandSourceStack source) {
        final Player player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("This command must be executed by a player."));
            return 0;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.literal("Main hand item is empty."));
            return 0;
        }

        var food = FoodCapability.get(stack);
        if (food == null) {
            source.sendFailure(Component.literal("Main hand item has no TFC food data."));
            return 0;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null || itemId == BuiltInRegistries.ITEM.getKey(Items.AIR)) {
            source.sendFailure(Component.literal("Cannot resolve item registry id."));
            return 0;
        }

        MinecraftServer server = source.getServer();

        Path datapacksDir = server.getWorldPath(LevelResource.DATAPACK_DIR);
        Path packDir = datapacksDir.resolve(PACK_FOLDER_NAME);

        Path outputFile = packDir
                .resolve("data")
                .resolve(itemId.getNamespace())
                .resolve(DATA_FOLDER)
                .resolve(FOOD_FALLBACK_FOLDER)
                .resolve(itemId.getPath() + ".json");

        try {
            ensurePack(packDir);
            Files.createDirectories(outputFile.getParent());

            JsonObject json = foodDataToJson(food.getData());

            Files.writeString(
                    outputFile,
                    GSON.toJson(json) + "\n",
                    StandardCharsets.UTF_8
            );

            source.sendSuccess(
                    () -> Component.literal("Exported food fallback for " + itemId + " to " + PACK_FOLDER_NAME)
                            .withStyle(ChatFormatting.GREEN),
                    true
            );

            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.literal("Failed to export food fallback: " + e.getMessage()));
            return 0;
        }
    }

    private static void ensurePack(Path packDir) throws IOException {
        Files.createDirectories(packDir);

        Path mcmeta = packDir.resolve("pack.mcmeta");

        int expectedPackFormat = SharedConstants.getCurrentVersion()
                .getPackVersion(PackType.SERVER_DATA);

        if (!Files.exists(mcmeta) || !hasCorrectMetadata(mcmeta, expectedPackFormat)) {
            JsonObject root = new JsonObject();
            JsonObject pack = new JsonObject();

            pack.addProperty("pack_format", expectedPackFormat);
            pack.addProperty("description", "Survivor's Delight generated food fallback datapack");

            root.add("pack", pack);

            Files.writeString(
                    mcmeta,
                    GSON.toJson(root) + "\n",
                    StandardCharsets.UTF_8
            );
        }
    }

    private static boolean hasCorrectMetadata(Path mcmeta, int expectedPackFormat) {
        try {
            String raw = Files.readString(mcmeta, StandardCharsets.UTF_8);
            JsonObject root = GSON.fromJson(raw, JsonObject.class);

            if (root == null || !root.has("pack") || !root.get("pack").isJsonObject()) {
                return false;
            }

            JsonObject pack = root.getAsJsonObject("pack");

            if (!pack.has("pack_format") || !pack.get("pack_format").isJsonPrimitive()) {
                return false;
            }

            if (!pack.has("description")) {
                return false;
            }

            return pack.get("pack_format").getAsInt() == expectedPackFormat;
        } catch (Exception e) {
            return false;
        }
    }
}