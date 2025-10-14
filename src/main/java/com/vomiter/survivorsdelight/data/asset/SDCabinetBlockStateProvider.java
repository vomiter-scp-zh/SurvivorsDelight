package com.vomiter.survivorsdelight.data.asset;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.registry.SDBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SDCabinetBlockStateProvider extends BlockStateProvider {
    ExistingFileHelper existingFileHelper;
    public SDCabinetBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SurvivorsDelight.MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String getName() {
        return "Block States (Cabinets): " + SurvivorsDelight.MODID;
    }

    private void trackTexture(String pathNoExt) {
        existingFileHelper.trackGenerated(
                modLoc("block/" + pathNoExt),
                PackType.CLIENT_RESOURCES,
                ".png",
                "textures"
        );
    }

    private void trackModel(String pathNoExt) {
        existingFileHelper.trackGenerated(
                modLoc("block/" + pathNoExt),
                PackType.CLIENT_RESOURCES,
                ".json",
                "models"
        );
    }


    @Override
    protected void registerStatesAndModels() {
        for (Map.Entry<Wood, ? extends net.minecraftforge.registries.RegistryObject<Block>> e : SDBlocks.CABINETS.entrySet()) {
            Wood wood = e.getKey();
            Block block = e.getValue().get();
            registerCabinet(wood, block);
        }
    }

    private void registerCabinet(Wood wood, Block block) {
        String basePath = "planks/cabinet/" + wood.getSerializedName();

        trackTexture(basePath + "_front");
        trackTexture(basePath + "_front_open");
        trackTexture(basePath + "_side");
        trackTexture(basePath + "_top");

        ModelFile closed = orientableModel(basePath,
                modLoc("block/" + basePath + "_front"),
                modLoc("block/" + basePath + "_side"),
                modLoc("block/" + basePath + "_top"));

        ModelFile open = orientableModel(basePath + "_open",
                modLoc("block/" + basePath + "_front_open"),
                modLoc("block/" + basePath + "_side"),
                modLoc("block/" + basePath + "_top"));

        getVariantBuilder(block).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean isOpen = state.getValue(BlockStateProperties.OPEN);
            int y = yFromHorizontal(dir);
            ModelFile model = isOpen ? open : closed;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(y)
                    .build();
        });

        itemModels().withExistingParent("item/" + basePath, modLoc("block/" + basePath));
    }

    private ModelFile orientableModel(String name, ResourceLocation front, ResourceLocation side, ResourceLocation top) {
        return models()
                .withExistingParent("block/" + name, mcLoc("block/orientable"))
                .texture("front", front)
                .texture("side", side)
                .texture("top",  top);
    }

    private static int yFromHorizontal(Direction dir) {
        return switch (dir) {
            case NORTH -> 0;
            case EAST  -> 90;
            case SOUTH -> 180;
            case WEST  -> 270;
            default -> 0;
        };
    }
}
