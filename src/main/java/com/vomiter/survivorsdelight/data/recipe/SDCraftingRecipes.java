package com.vomiter.survivorsdelight.data.recipe;

import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Consumer;

import static com.vomiter.survivorsdelight.SurvivorsDelight.MODID;
import static com.vomiter.survivorsdelight.core.registry.SDBlocks.CABINETS;

public class SDCraftingRecipes {
    public void save(Consumer<FinishedRecipe> out){
        pie(out);
        fishRoll(out, ModItems.COD_ROLL.get(), ModItems.COD_SLICE.get());
        fishRoll(out, ModItems.SALMON_ROLL.get(), ModItems.SALMON_SLICE.get());
        misc(out);
    }

    public void misc(Consumer<FinishedRecipe> out){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HORSE_FEED.get())
                .pattern("AC")
                .pattern("SA")
                .define('A', SDTags.ItemTags.APPLE_FOR_CIDER)
                .define('C', TFCItems.FOOD.get(Food.CARROT).get())
                .define('S', TFCBlocks.THATCH.get().asItem())
                .unlockedBy("has_thatch", InventoryChangeTrigger.TriggerInstance.hasItems(TFCBlocks.THATCH.get()))
                .save(out, RLUtils.build("crafting/misc/horse_feed"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DOG_FOOD.get())
                .pattern(" B ")
                .pattern("MRM")
                .pattern(" b ")
                .define('B', Items.BONE)
                .define('b', Items.BOWL)
                .define('M', SDTags.ItemTags.MEATS_FOR_SHEPHERDS_PIE)
                .define('R', Items.ROTTEN_FLESH)
                .unlockedBy("has_rotten_flesh", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ROTTEN_FLESH))
                .save(out, RLUtils.build("crafting/misc/dog_food"));
    }

    public void pie(Consumer<FinishedRecipe> out){
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.SWEET_BERRY_CHEESECAKE.get())
                .pattern("FFF")
                .pattern("CCC")
                .pattern("SPS")
                .define('F', SDTags.ItemTags.FRUIT_FOR_CHEESECAKE)
                .define('C', SDTags.ItemTags.CHEESE_FOR_CHEESECAKE)
                .define('S', SDTags.ItemTags.TFC_SWEETENER)
                .define('P', NotRottenIngredient.of(Ingredient.of(ModItems.PIE_CRUST.get())))
                .unlockedBy("has_pie_crust", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PIE_CRUST.get()))
                .save(out, new ResourceLocation(MODID, "crafting/food/cherry_cheesecake"));

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.CHOCOLATE_PIE.get())
                .pattern("ccc")
                .pattern("CCC")
                .pattern("SPS")
                .define('c', SDTags.ItemTags.CHOCOLATE_FOR_CHEESECAKE)
                .define('C', SDTags.ItemTags.CHEESE_FOR_CHEESECAKE)
                .define('S', SDTags.ItemTags.TFC_SWEETENER)
                .define('P', NotRottenIngredient.of(Ingredient.of(ModItems.PIE_CRUST.get())))
                .unlockedBy("has_pie_crust", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PIE_CRUST.get()))
                .save(out, new ResourceLocation(MODID, "crafting/food/chocolate_pie"));

    }

    public void fishRoll(Consumer<FinishedRecipe> out, Item result, Item fish){
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, result)
                .pattern("F")
                .pattern("R")
                .define('F', NotRottenIngredient.of(fish))
                .define('R', NotRottenIngredient.of(TFCItems.FOOD.get(Food.COOKED_RICE).get()))
                .unlockedBy(
                        "has_fish_slice",
                        InventoryChangeTrigger.TriggerInstance.hasItems(fish))
                .save(out, new ResourceLocation(MODID, "crafting/food/" + ForgeRegistries.ITEMS.getKey(result).getPath()));
    }

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
