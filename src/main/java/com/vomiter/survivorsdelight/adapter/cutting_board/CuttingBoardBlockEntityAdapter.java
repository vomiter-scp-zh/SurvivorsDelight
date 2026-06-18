package com.vomiter.survivorsdelight.adapter.cutting_board;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.registry.recipe.SDCuttingRecipe;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CuttingBoardBlockEntityAdapter {
    private static final TagKey<Item> COMMON_FOODS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "foods"));

    public static void cuttingBoardISP(ItemStack toolStack, @Nullable Player player, CuttingBoardBlockEntity cuttingBoard, SDCuttingRecipe recipe){
        var level = cuttingBoard.getLevel();
        assert level != null;
        int fortune = EnchantmentHelper.getTagEnchantmentLevel(
                SDUtils.getEnchantHolder(level, Enchantments.FORTUNE),
                toolStack
        );
        double fortuneBonus = Configuration.CUTTING_BOARD_FORTUNE_BONUS.get() * (double) fortune;

        List<ItemStack> out = new ArrayList<>();

        for (SDCuttingRecipe.Output r : recipe.getOutputs()) {
            // 1) 先用 ISP 算出實際要掉的東西（套好 modifier）
            ItemStack stack = r.getISPResult(level).getStack(cuttingBoard.getStoredItem().copyWithCount(1));
            if (stack.isEmpty()) {
                continue;
            }

            // 2) 決定這個 output 的 base chance
            float baseChance = 1.0f;
            if (r instanceof SDCuttingRecipe.StackOutput stackOutput) {
                baseChance = stackOutput.chance();
            }
            // ProviderOutput 就當成 1.0f

            float totalChance = (float) (baseChance + fortuneBonus);
            // 安全一點夾在 [0, 1] 之間
            if (totalChance <= 0f) {
                continue;
            }
            if (totalChance > 1f) {
                totalChance = 1f;
            }

            float roll = level.random.nextFloat();
            // 3) 如果沒過機率就跳過這個 output
            if (roll >= totalChance) {
                continue;
            }

            out.add(stack);
        }

        // TARGET
        var storedStack = cuttingBoard.getStoredItem();
        var storedFood = FoodCapability.get(storedStack);
        if (storedFood != null) {
            List<ItemStack> foodResults = new ArrayList<>();

            var storedData = storedFood.getData();

            float[] nutrients = Arrays.copyOf(storedData.nutrients(), storedData.nutrients().length);
            float saturation = storedData.saturation();
            float water = storedData.water();
            int hunger = storedData.hunger();

            int numberOfFood = 0;
            for (ItemStack itemStack : out) {
                IFood food = FoodCapability.get(itemStack);
                boolean isCommonFood = itemStack.is(COMMON_FOODS);
                if (itemStack.is(SDTags.ItemTags.DYNAMIC_CUTTING_FOOD)) {
                    foodResults.add(itemStack);
                    numberOfFood += itemStack.getCount();
                } else if (food != null) {
                    var data = food.getData();
                    int count = itemStack.getCount();
                    for (int i = 0; i < nutrients.length; i++) {
                        nutrients[i] -= data.nutrients()[i] * count;
                    }

                    saturation -= data.saturation() * count;
                    hunger -= data.hunger() * count;
                    water -= data.water() * count;
                } else {
                }
            }

            if (numberOfFood <= 0) {
                SurvivorsDelight.LOGGER.warn(
                        "[SD CuttingBoardFood] abort applying result food data: numberOfFood={}, foodResults={}, outputs={}",
                        numberOfFood,
                        foodResults,
                        out
                );
            } else {
                FoodData resultFoodData = new FoodData(
                        Math.round((float) hunger / numberOfFood),
                        water / numberOfFood,
                        saturation / numberOfFood,
                        storedData.intoxication(),
                        new float[]{
                                nutrients[Nutrient.GRAIN.ordinal()] / numberOfFood,
                                nutrients[Nutrient.FRUIT.ordinal()] / numberOfFood,
                                nutrients[Nutrient.VEGETABLES.ordinal()] / numberOfFood,
                                nutrients[Nutrient.PROTEIN.ordinal()] / numberOfFood,
                                nutrients[Nutrient.DAIRY.ordinal()] / numberOfFood
                        },
                        storedData.decayModifier()
                );

                foodResults.forEach(item -> {
                    FoodCapability.setFoodForDynamicItemOnCreate(item, resultFoodData);
                    FoodCapability.setCreationDate(item, FoodCapability.getRoundedCreationDate());
                });
            }
        }

        Direction dir = cuttingBoard.getBlockState().getValue(CuttingBoardBlock.FACING).getCounterClockWise();
        for (ItemStack resultStack : out) {
            ItemUtils.spawnItemEntity(
                    level,
                    resultStack.copy(),
                    cuttingBoard.getBlockPos().getX() + 0.5D + dir.getStepX() * 0.2D,
                    cuttingBoard.getBlockPos().getY() + 0.2D,
                    cuttingBoard.getBlockPos().getZ() + 0.5D + dir.getStepZ() * 0.2D,
                    dir.getStepX() * 0.2F, 0.0F, dir.getStepZ() * 0.2F
            );
        }

        toolStack.hurtAndBreak(1, (ServerLevel) level, player, (item) -> {});

        cuttingBoard.playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, cuttingBoard.getStoredItem());
        cuttingBoard.getInventory().extractItem(0, 1, false);
        if (!cuttingBoard.getStoredItem().isEmpty()) {
            player.displayClientMessage(TextUtils.block("cutting_board.remaining_items", cuttingBoard.getStoredItem().getCount()), true);
        } else {
            player.displayClientMessage(Component.empty(), true);
        }


        if (player instanceof ServerPlayer sp) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger(sp);
        }

    }
}
