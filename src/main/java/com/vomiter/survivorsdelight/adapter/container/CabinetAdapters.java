package com.vomiter.survivorsdelight.adapter.container;

import com.vomiter.survivorsdelight.common.container.SDCabinetBlockEntity;
import com.vomiter.survivorsdelight.common.food.trait.SDFoodTraits;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.item.SkilletItem;

public class CabinetAdapters {

    private static final FoodTrait CABINET_STORED = SDFoodTraits.CABINET_STORED;
    public static void setStored(ItemStack food){
        FoodCapability.applyTrait(food, CABINET_STORED);
    }
    public static void removeStored(ItemStack food){
        FoodCapability.removeTrait(food, CABINET_STORED);
    }


    public static boolean checkCanTreat(Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand){
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SDCabinetBlockEntity cabinet)) return false;
        if(cabinet.isTreated()) return false;
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof FluidContainerItem) {
            IFluidHandlerItem itemHandler = Helpers.getCapability(mainHandItem, Capabilities.FLUID_ITEM);
            if (itemHandler != null) {
                boolean cantTreat = itemHandler.getFluidInTank(0).getFluid().isSame(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.TALLOW).getSource());
                if(cantTreat) {
                    itemHandler.drain(100, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                }
            }
        }
        else if(mainHandItem.is(SDTags.ItemTags.WOOD_PRESERVATIVES)){
            if(mainHandItem.isDamageableItem()) mainHandItem.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
            else mainHandItem.shrink(1);
            return true;
        }
        return false;
    }

    public static boolean isValidItemInCabinet(ItemStack stack) {
        return TFCChestBlockEntity.isValid(stack) || stack.getItem() instanceof SkilletItem;
    }


}
