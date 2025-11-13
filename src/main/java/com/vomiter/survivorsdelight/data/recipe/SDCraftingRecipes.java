package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput; // 修正 import
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.registry.ModItems;

// 移除 import java.util.function.Consumer;
// 移除 import net.minecraft.data.recipes.FinishedRecipe;

import static com.vomiter.survivorsdelight.SurvivorsDelight.MODID;
import static com.vomiter.survivorsdelight.core.registry.SDBlocks.CABINETS;

public class SDCraftingRecipes {
    public void save(RecipeOutput out){ // 修正簽章
        misc(out);
    }

    public void misc(RecipeOutput out){ // 修正簽章
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HORSE_FEED.get())
                .pattern("AC")
                .pattern("SA")
                .define('A', SDTags.ItemTags.APPLE_FOR_CIDER)
                .define('C', TFCItems.FOOD.get(Food.CARROT).get())
                .define('S', TFCBlocks.THATCH.get().asItem())
                .unlockedBy("has_thatch", InventoryChangeTrigger.TriggerInstance.hasItems(TFCBlocks.THATCH.get()))
                .save(out, SDUtils.RLUtils.build("crafting/misc/horse_feed"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DOG_FOOD.get())
                .pattern(" B ")
                .pattern("MRM")
                .pattern(" b ")
                .define('B', Items.BONE)
                .define('b', Items.BOWL)
                .define('M', SDTags.ItemTags.MEATS_FOR_SHEPHERDS_PIE)
                .define('R', Items.ROTTEN_FLESH)
                .unlockedBy("has_rotten_flesh", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ROTTEN_FLESH))
                .save(out, SDUtils.RLUtils.build("crafting/misc/dog_food"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORGANIC_COMPOST.get())
                .requires(ModItems.TREE_BARK.get(), 2)
                .requires(SDTags.ItemTags.create("minecraft", "dirt"))
                .requires(TFCItems.COMPOST.get(), 2)
                .requires(TFCItems.ROTTEN_COMPOST.get())
                .requires(Items.ROTTEN_FLESH, 3)
                .unlockedBy("has_rotten_flesh", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ROTTEN_FLESH))
                .save(out, SDUtils.RLUtils.build("crafting/misc/organic_compost"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORGANIC_COMPOST.get())
                .requires(ModItems.TREE_BARK.get(), 5) // 修正為 5
                .requires(SDTags.ItemTags.create("minecraft", "dirt"))
                .requires(Items.BROWN_MUSHROOM, 2)
                .requires(Items.RED_MUSHROOM)
                .unlockedBy("has_organic_compost", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ORGANIC_COMPOST.get()))
                .save(out, SDUtils.RLUtils.build("crafting/misc/organic_compost_with_mushroom"));
    }


    public void fishRoll(RecipeOutput out, Item result, Item fish){ // 修正簽章
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, result)
                .pattern("F")
                .pattern("R")
                .define('F', SDUtils.SDNotRottenIngredient.of(fish))
                .define('R', SDUtils.SDNotRottenIngredient.of(TFCItems.FOOD.get(Food.COOKED_RICE).get()))
                .unlockedBy(
                        "has_fish_slice",
                        InventoryChangeTrigger.TriggerInstance.hasItems(fish))
                .save(out, SDUtils.RLUtils.build(MODID, "crafting/food/" + BuiltInRegistries.ITEM.getKey(result).getPath()));
    }

    public void cabinetForWood(Wood wood, RecipeOutput out) { // 修正簽章
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
                .unlockedBy("has_" + wood.getSerializedName() + "_lumber",
                        InventoryChangeTrigger.TriggerInstance.hasItems(lumber))
                .unlockedBy("has_" + wood.getSerializedName() + "_trapdoor",
                        InventoryChangeTrigger.TriggerInstance.hasItems(trapdoor))
                .save(out, SDUtils.RLUtils.build(
                        "crafting/cabinet/" + wood.getSerializedName()
                ));
    }
}
