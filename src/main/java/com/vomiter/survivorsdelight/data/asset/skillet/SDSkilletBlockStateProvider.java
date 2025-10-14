package com.vomiter.survivorsdelight.data.asset.skillet;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletBlocks;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.SkilletBlock;

public class SDSkilletBlockStateProvider extends BlockStateProvider {
    public SDSkilletBlockStateProvider(PackOutput out, ExistingFileHelper helper) {
        super(out, "survivorsdelight", helper);
    }

    @Override
    public @NotNull String getName() {
        return "Block States (Skillets): " + SurvivorsDelight.MODID;
    }

    @Override
    protected void registerStatesAndModels() {
        for (SkilletMaterial m : SkilletMaterial.values()){
            Block skillet = SDSkilletBlocks.SKILLETS.get(m).get();
            genSkilletModel(skillet,
                    modLoc("block/skillet/" + m.material),
                    modLoc("block/skillet/tray"));
        }
    }

    private void genSkilletModel(Block block, ResourceLocation baseModel, ResourceLocation trayModel) {
        var builder = getMultipartBuilder(block);

        builder
            .part()
            .modelFile(models().getExistingFile(baseModel))
            .rotationY(0)
            .addModel()
            .condition(HorizontalDirectionalBlock.FACING, Direction.NORTH)
            .end()

            .part()
            .modelFile(models().getExistingFile(baseModel))
            .rotationY(180)
            .addModel()
            .condition(HorizontalDirectionalBlock.FACING, Direction.SOUTH)
            .end()

            .part()
            .modelFile(models().getExistingFile(baseModel))
            .rotationY(90)
            .addModel()
            .condition(HorizontalDirectionalBlock.FACING, Direction.EAST)
            .end()

            .part()
            .modelFile(models().getExistingFile(baseModel))
            .rotationY(270)
            .addModel()
            .condition(HorizontalDirectionalBlock.FACING, Direction.WEST)
            .end()

            .part()
            .modelFile(models().getExistingFile(trayModel))
            .addModel()
            .condition(SkilletBlock.SUPPORT, true)
            .end();
    }
}