package com.vomiter.survivorsdelight.data.recipe;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

import static com.vomiter.survivorsdelight.SurvivorsDelight.MODID;
import static com.vomiter.survivorsdelight.core.registry.SDBlocks.CABINETS;

public class CabinetRecipes {

    public void cabinetForWood(Wood wood, Consumer<FinishedRecipe> out) {
        ItemLike result = CABINETS.get(wood).get().asItem(); // 你的櫃子成品
        ItemLike lumber  = TFCItems.LUMBER.get(wood).get();
        ItemLike trapdoor = wood.getBlock(Wood.BlockType.TRAPDOOR).get().asItem();

        // LLL
        // T T
        // LLL
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result)
                .pattern("LLL")
                .pattern("T T")
                .pattern("LLL")
                .define('L', lumber)
                .define('T', trapdoor)
                .group(MODID + ":cabinet")
                // 解鎖條件：擁有該樹種的木板條或活板門其一
                .unlockedBy("has_" + wood.getSerializedName() + "_lumber",
                        InventoryChangeTrigger.TriggerInstance.hasItems(lumber))
                .unlockedBy("has_" + wood.getSerializedName() + "_trapdoor",
                        InventoryChangeTrigger.TriggerInstance.hasItems(trapdoor))
                // 自訂輸出路徑：data/<modid>/recipes/crafting/cabinet/<wood>.json
                .save(out, new ResourceLocation(
                        MODID, "crafting/cabinet/" + wood.getSerializedName()
                ));
    }
}
