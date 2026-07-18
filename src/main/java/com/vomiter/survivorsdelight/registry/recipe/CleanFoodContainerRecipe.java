package com.vomiter.survivorsdelight.registry.recipe;

import com.mojang.serialization.MapCodec;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.registry.SDRecipeSerializers;
import com.vomiter.survivorsdelight.util.FoodItemContainerApply;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.InstantBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.util.Helpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.function.Function;

public class CleanFoodContainerRecipe extends InstantBarrelRecipe {
    public static final MapCodec<CleanFoodContainerRecipe> CODEC = BarrelRecipe.CODEC.xmap(CleanFoodContainerRecipe::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, CleanFoodContainerRecipe> STREAM_CODEC = BarrelRecipe.STREAM_CODEC.map(CleanFoodContainerRecipe::new, Function.identity());


    public CleanFoodContainerRecipe(BarrelRecipe parent) {
        super(parent);
    }

    public RecipeSerializer<?> getSerializer() {
        return SDRecipeSerializers.CLEAN_FOOD_CONTAINER.get();
    }

    public RecipeType<?> getType() {
        return TFCRecipeTypes.BARREL_INSTANT.get();
    }

    public void assembleOutputs(BarrelInventory inventory) {
        inventory.whileMutable(() -> {
            ItemStack stack = Helpers.removeStack(inventory, 2);
            FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            int multiplier = Math.min(fluid.getAmount() / this.inputFluid.amount(), stack.getCount() / ((SizedIngredient)this.inputItem.get()).count());
            inventory.drain(multiplier * inputFluid.amount(), IFluidHandler.FluidAction.EXECUTE);
            var remainder = FoodItemContainerApply.getRemainder(stack).copyWithCount(multiplier);
            inventory.insertItemWithOverflow(remainder);
            inventory.insertItemWithOverflow(stack.copyWithCount(stack.getCount() - multiplier));
            int retainAmount = fluid.getAmount() - multiplier * this.inputFluid.amount();
            if (retainAmount > 0) {
                FluidStack retainedFluid = fluid.copy();
                retainedFluid.setAmount(retainAmount);
                inventory.fill(retainedFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    public ItemStack getResultItem() {
        return Items.BOWL.getDefaultInstance();
    }


}
