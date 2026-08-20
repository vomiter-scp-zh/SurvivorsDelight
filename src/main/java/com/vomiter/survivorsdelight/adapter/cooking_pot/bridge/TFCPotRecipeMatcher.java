package com.vomiter.survivorsdelight.adapter.cooking_pot.bridge;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TFCPotRecipeMatcher {

    private TFCPotRecipeMatcher() {}

    public static Optional<PotRecipe> findFirstMatch(
            Level level,
            IItemHandler items,
            IFluidHandler fluids,
            int[] ingredientSlots
    ) {
        // 蒐集候選配方
        final var recipes = level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.POT.get());
        if (recipes.isEmpty()) return Optional.empty();

        // 讀容器內容
        final FluidStack fluidInTank0 = fluids.getTanks() > 0 ? fluids.getFluidInTank(0) : FluidStack.EMPTY;
        final List<ItemStack> stacks = new ArrayList<>();
        for (int slot : ingredientSlots) {
            ItemStack s = items.getStackInSlot(slot);
            if (!s.isEmpty()) stacks.add(s);
        }

        for (PotRecipe r : recipes) {
            // 先比對流體
            final FluidStackIngredient needed = r.getFluidIngredient();
            if (!needed.test(fluidInTank0)) continue;

            // 再比對原料（完全配對）
            if (!matchesItemsExactly(stacks, r.getItemIngredients())) continue;

            return Optional.of(r);
        }
        return Optional.empty();
    }

    private static boolean matchesItemsExactly(List<ItemStack> present, List<Ingredient> needed) {
        if (present.isEmpty() && needed.isEmpty()) return true;
        return Helpers.perfectMatchExists(present, needed);
    }
}
