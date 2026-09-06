package com.vomiter.survivorsdelight.adapter.stove;

import com.vomiter.survivorsdelight.adapter.HeatSourceBlockEntity;
import com.vomiter.survivorsdelight.mixin.device.stove.StoveBlockEntity_Accessor;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Fuel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
                stoveEntity.sdtfc$addLeftBurnTick(Math.round(fuel.getDuration() * logBonus * fuel.getTemperature() * 6 / IStoveBlockEntity.sdtfc$getStaticTemperature()));
                return true;
            }
        }
        return false;
    }

    public static void ejectItem(IStoveBlockEntity iStoveBlockEntity, ItemStack item){
        Level level = iStoveBlockEntity.sdtfc$getBlockEntity().getLevel();
        BlockPos pos = iStoveBlockEntity.sdtfc$getBlockEntity().getBlockPos();
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

    public static void cookTFCFoodInSlot(IStoveBlockEntity stove, int slot){
        var inventory = stove.sdtfc$getInventory();
        Level level = stove.sdtfc$getBlockEntity().getLevel();
        BlockPos pos = stove.sdtfc$getBlockEntity().getBlockPos();
        if(level == null) return;
        ItemStack slotStack = inventory.getStackInSlot(slot);
        IHeat heat = HeatCapability.get(slotStack);
        if(heat != null){
            float heatingTemp = ((HeatSourceBlockEntity)stove).sdtfc$getTemperature();
            HeatCapability.addTemp(heat, heatingTemp);
            HeatingRecipe[] cachedRecipes = stove.sdtfc$getCachedRecipes();
            if(cachedRecipes[slot] == null){
                var recipe = HeatingRecipe.getRecipe(new ItemStackInventory(slotStack));
                cachedRecipes[slot] = recipe;
            }
            if(cachedRecipes[slot] == null){
                int cookingTotalTime = ((StoveBlockEntity_Accessor)stove).getCookingTime()[slot];
                if(cookingTotalTime == 0){
                    ejectItem(stove, inventory.extractItem(slot, 1, false));
                    stove.sdtfc$getBlockEntity().setChanged();
                    level.sendBlockUpdated(pos, stove.sdtfc$getBlockEntity().getBlockState(), stove.sdtfc$getBlockEntity().getBlockState(), 3);
                }
            }
            else if(cachedRecipes[slot].isValidTemperature(heat.getTemperature())){
                assert stove.sdtfc$getBlockEntity().getLevel() != null;
                final ItemStack result = cachedRecipes[slot].assemble(new ItemStackInventory(slotStack), stove.sdtfc$getBlockEntity().getLevel().registryAccess());
                FoodCapability.applyTrait(result, FoodTraits.WOOD_GRILLED);
                FoodCapability.updateFoodDecayOnCreate(result);
                ejectItem(stove, result.copy());
                inventory.extractItem(slot, 1, false);
                stove.sdtfc$getBlockEntity().setChanged();
                cachedRecipes[slot] = null;
                level.sendBlockUpdated(pos, stove.sdtfc$getBlockEntity().getBlockState(), stove.sdtfc$getBlockEntity().getBlockState(), 3);
            }
        }
    }
}
