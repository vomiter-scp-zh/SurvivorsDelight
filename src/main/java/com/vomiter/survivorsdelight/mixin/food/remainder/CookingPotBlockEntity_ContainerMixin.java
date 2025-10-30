package com.vomiter.survivorsdelight.mixin.food.remainder;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

import static vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity.CONTAINER_SLOT;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntity_ContainerMixin {
    @Shadow private ItemStack mealContainerStack;

    @Shadow @Final private ItemStackHandler inventory;

    @Shadow public abstract ItemStack getMeal();

    @ModifyExpressionValue(method = "isContainerValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = true)
    private boolean expandValidContainer(boolean original, @Local(argsOnly = true) ItemStack container){
        if(original) return true;
        if(mealContainerStack.is(Items.BOWL) && container.is(TFCBlocks.CERAMIC_BOWL.get().asItem())) return true;
        else return mealContainerStack.is(Items.GLASS_BOTTLE) && container.is(TFCItems.SILICA_GLASS_BOTTLE.get());
    }

    @ModifyExpressionValue(method = "useHeldItemOnMeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"), remap = true)
    private ItemStack applyContainer(ItemStack original){
        ItemStack container = inventory.getStackInSlot(CONTAINER_SLOT);
        if(ItemStack.isSameItem(mealContainerStack, container)) return original;
        original.getOrCreateTag().put("Container", container.copyWithCount(1).serializeNBT());
        return original;
    }

    @Inject(method = "useHeldItemOnMeal", at = @At("HEAD"), cancellable = true)
    private void checkContainerItemRotten(ItemStack container, CallbackInfoReturnable<ItemStack> cir){
        IFood containerFood = FoodCapability.get(container);
        if(containerFood != null && containerFood.isRotten()){
            cir.setReturnValue(container);
        }
    }

    @Inject(method = "useStoredContainersOnMeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", remap = true), cancellable = true)
    private void applyContainerStored(CallbackInfo ci){
        ItemStack mealStack = inventory.getStackInSlot(6);
        ItemStack containerInputStack = inventory.getStackInSlot(7);
        ItemStack outputStack = inventory.getStackInSlot(8);

        IFood containerFood = FoodCapability.get(containerInputStack);
        if(containerFood != null && containerFood.isRotten()){
            ci.cancel();
        }

        if(ItemStack.isSameItem(mealStack.getCraftingRemainingItem(), containerInputStack)) return;

        int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
        int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            ItemStack mealToPut = mealStack.split(mealCount);
            mealToPut.getOrCreateTag().put("Container", containerInputStack.copyWithCount(1).serializeNBT());
            containerInputStack.shrink(mealCount);
            inventory.setStackInSlot(8, mealToPut);
        } else if (outputStack.getItem() == mealStack.getItem()) {
            ItemStack simMeal = mealStack.copy();
            simMeal.getOrCreateTag().put("Container", containerInputStack.copyWithCount(1).serializeNBT());
            if(FoodCapability.areStacksStackableExceptCreationDate(simMeal, outputStack)){
                ItemStack mealToPut = mealStack.split(mealCount);
                mealToPut.getOrCreateTag().put("Container", containerInputStack.copyWithCount(1).serializeNBT());
                containerInputStack.shrink(mealCount);
                FoodCapability.mergeItemStacks(outputStack, mealToPut);
            }
        }
        ci.cancel();
    }

}
