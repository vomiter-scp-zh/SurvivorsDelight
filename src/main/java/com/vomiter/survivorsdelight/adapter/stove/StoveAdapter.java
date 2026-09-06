package com.vomiter.survivorsdelight.adapter.stove;

import com.vomiter.survivorsdelight.HeatSourceBlockEntity;
import com.vomiter.survivorsdelight.mixin.device.stove.StoveBlockEntity_Accessor;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.util.data.Fuel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class StoveAdapter {
    public static boolean addFuel(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof IStoveBlockEntity stoveEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            Fuel fuel = Fuel.get(heldItem);
            float logBonus = heldItem.is(TFCTags.Items.FIREPIT_LOGS)? 1.2f: 1;
            if(fuel != null){
                if(stoveEntity.sdtfc$getLeftBurnTick() > IStoveBlockEntity.sdtfc$getMaxDuration()) return false;
                if(!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
                stoveEntity.sdtfc$addLeftBurnTick(Math.round(fuel.duration() * logBonus * fuel.temperature() * 6 / IStoveBlockEntity.sdtfc$getStaticTemperature()));
                return true;
            }
        }
        return false;
    }

    public static boolean addItem(ItemStack itemStackIn, int slot, AbstractStoveBlockEntity stove, Player player) {
        var inventory = stove.getItems();
        if (0 <= slot && slot < inventory.getSlots()) {
            ItemStack slotStack = inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                var recipe = HeatingRecipe.getRecipe((itemStackIn));
                if(recipe == null) return false;
                if(recipe.getTemperature() > 500) return false;
                assert stove.getLevel() != null;
                if(recipe.getResultItem(stove.getLevel().registryAccess()).isEmpty()) return false;
                var acc = (StoveBlockEntity_Accessor)stove;
                acc.getCookingTimesTotal()[slot] = 24 * 60 * 60 * 20;
                acc.getCookingTimes()[slot] = 0;
                inventory.setStackInSlot(slot,
                        player.isCreative()?
                                new ItemStack(itemStackIn.getItem()) :
                                itemStackIn.split(1)
                );
                stove.setChanged();
                Level level = stove.getLevel();
                if(level != null){
                    level.sendBlockUpdated(stove.getBlockPos(), stove.getBlockState(), stove.getBlockState(), 3);
                }
                return true;
            }
        }

        return false;
    }

    private static void ejectItem(AbstractStoveBlockEntity stove, ItemStack item){
        Level level = stove.getLevel();
        BlockPos pos = stove.getBlockPos();
        if(level == null) return;
        ItemUtils.spawnItemEntity(
                level,
                item,
                (double)pos.getX() + (double)0.5F,
                (double)pos.getY() + (double)1.0F,
                (double)pos.getZ() + (double)0.5F,
                level.random.nextGaussian() * (double)0.01F, 0.1F,
                level.random.nextGaussian() * (double)0.01F);
    }

    public static void cookTFCFoodInSlot(AbstractStoveBlockEntity stove, int slot){
        var inventory = stove.getItems();
        Level level = stove.getLevel();
        BlockPos pos = stove.getBlockPos();
        if(level == null) return;
        ItemStack slotStack = inventory.getStackInSlot(slot);
        IHeat heat = HeatCapability.get(slotStack);
        if(heat != null){
            float heatingTemp = ((HeatSourceBlockEntity)stove).sdtfc$getTemperature();
            HeatCapability.addTemp(heat, heatingTemp);
            HeatingRecipe[] cachedRecipes = ((IStoveBlockEntity)stove).sdtfc$getCachedRecipes();
            if(cachedRecipes[slot] == null){
                var recipe = HeatingRecipe.getRecipe((slotStack));
                cachedRecipes[slot] = recipe;
            }
            if(cachedRecipes[slot] == null){
                int cookingTotalTime = ((StoveBlockEntity_Accessor)stove).getCookingTimesTotal()[slot];
                if(cookingTotalTime == 0){
                    ejectItem(stove, inventory.extractItem(slot, 1, false));
                    stove.setChanged();
                    level.sendBlockUpdated(pos, stove.getBlockState(), stove.getBlockState(), 3);
                }
            }
            else if(cachedRecipes[slot].isValidTemperature(heat.getTemperature())){
                assert stove.getLevel() != null;
                final ItemStack result = cachedRecipes[slot].assembleItem((slotStack));
                FoodCapability.applyTrait(result, FoodTraits.WOOD_GRILLED);
                ejectItem(stove, result.copy());
                inventory.extractItem(slot, 1, false);
                stove.setChanged();
                cachedRecipes[slot] = null;
                level.sendBlockUpdated(pos, stove.getBlockState(), stove.getBlockState(), 3);
            }
        }



    }
}
