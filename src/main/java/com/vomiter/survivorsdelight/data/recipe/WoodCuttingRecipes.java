package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ToolActions;
import vectorwing.farmersdelight.common.crafting.ingredient.ToolActionIngredient;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public class WoodCuttingRecipes extends RecipeProvider {

    public WoodCuttingRecipes(PackOutput output) {
        super(output);
    }

    private void stripForBark(Wood wood, Consumer<FinishedRecipe> out){
        Block log = wood.getBlock(Wood.BlockType.LOG).get();
        Block strippedLog = wood.getBlock(Wood.BlockType.STRIPPED_LOG).get();
        Block woodBlock = wood.getBlock(Wood.BlockType.WOOD).get();
        Block strippedWood = wood.getBlock(Wood.BlockType.STRIPPED_WOOD).get();

        ItemLike bark = ModItems.TREE_BARK.get();

        final String woodName = wood.getSerializedName();
        final ResourceLocation logRecipeId = RLUtils.build(
                SurvivorsDelight.MODID,
                "cutting/tfc/strip_wood/" + woodName + "_log"
        );
        final ResourceLocation woodRecipeId = RLUtils.build(
                SurvivorsDelight.MODID,
                "cutting/tfc/strip_wood/" + woodName + "_wood"
        );

        CuttingBoardRecipeBuilder
                .cuttingRecipe(
                        Ingredient.of(log),
                        new ToolActionIngredient(ToolActions.AXE_STRIP),
                        strippedLog, 1
                )
                .addResult(bark, 1)
                .addSound("minecraft:item.axe.strip")
                .build(out, logRecipeId);

        CuttingBoardRecipeBuilder
                .cuttingRecipe(
                        Ingredient.of(woodBlock),
                        new ToolActionIngredient(ToolActions.AXE_STRIP),
                        strippedWood, 1
                )
                .addResult(bark, 1)
                .addResultWithChance(bark, 0.5f, 1)
                .addSound("minecraft:item.axe.strip")
                .build(out, woodRecipeId);

    }

    private void salvageWoodFurnitureType(Wood wood, Wood.BlockType type, int count, Consumer<FinishedRecipe> out){
        Item lumber = TFCItems.LUMBER.get(wood).get();
        CuttingBoardRecipeBuilder.cuttingRecipe(
                Ingredient.of(wood.getBlock(type).get()),
                Ingredient.of(new TagKey<>(ResourceKey.createRegistryKey(RLUtils.build("minecraft", "item")), RLUtils.build(TerraFirmaCraft.MOD_ID, "saws"))),
                lumber,
                count
        ).build(
                out,
                RLUtils.build(SurvivorsDelight.MODID, "cutting/tfc/salvage/wood_furniture/" + wood.getSerializedName() + "_" + type.name().toLowerCase(Locale.ROOT))
        );
    }

    private void salvageHangingSign(Wood wood, Metal.Default metal, Consumer<FinishedRecipe> out){
        Item lumber = TFCItems.LUMBER.get(wood).get();
        Block chain = TFCBlocks.METALS.get(metal).get(Metal.BlockType.CHAIN).get();
        CuttingBoardRecipeBuilder.cuttingRecipe(
                Ingredient.of(TFCItems.HANGING_SIGNS.get(wood).get(metal).get()),
                Ingredient.of(new TagKey<>(ResourceKey.createRegistryKey(RLUtils.build("minecraft", "item")), RLUtils.build(TerraFirmaCraft.MOD_ID, "saws"))),
                lumber,
                2
                )
                .addResultWithChance(chain, 0.5f, 1)
                .build(out,
                RLUtils.build(SurvivorsDelight.MODID, "cutting/tfc/salvage/hanging_sign/" + wood.getSerializedName() + "_" + metal.getSerializedName())
        );

    }

    private void salvageWoodFurniture(Wood wood, Consumer<FinishedRecipe> out){
        salvageWoodFurnitureType(wood, Wood.BlockType.DOOR, 3, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.TRAPDOOR, 2, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.FENCE, 2, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.LOG_FENCE, 2, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.FENCE_GATE, 2, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.BUTTON, 4, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.PRESSURE_PLATE, 2, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.TOOL_RACK, 6, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.WORKBENCH, 16, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.CHEST, 8, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.TRAPPED_CHEST, 8, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.LOOM, 7, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.SLUICE, 3, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.BARREL, 7, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.BOOKSHELF, 6, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.LECTERN, 10, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.SCRIBING_TABLE, 14, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.SEWING_TABLE, 24, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.AXLE, 4, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.ENCASED_AXLE, 4, out);
        salvageWoodFurnitureType(wood, Wood.BlockType.SIGN, 2, out);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> out) {

        TFCBlocks.WOODS.forEach((wood, blockTypes) -> {
            stripForBark(wood, out);
            salvageWoodFurniture(wood, out);
            Arrays.stream(Metal.Default.values()).filter(Metal.Default::hasUtilities).forEach(
                    m -> salvageHangingSign(wood, m, out)
            );
        });
    }
}
