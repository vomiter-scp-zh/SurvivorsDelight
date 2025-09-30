package com.vomiter.survivorsdelight.core.device.skillet.data;

import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.SDSkilletBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vectorwing.farmersdelight.common.block.SkilletBlock;

public class SDSkilletBlockStateProvider extends BlockStateProvider {
    public SDSkilletBlockStateProvider(PackOutput out, ExistingFileHelper helper) {
        super(out, "survivorsdelight", helper);
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