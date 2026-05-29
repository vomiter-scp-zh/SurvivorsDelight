package com.vomiter.survivorsdelight.util;

import com.vomiter.survivorsdelight.common.food.FoodContainerExpansion;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SimpleCraftingContainer implements CraftingContainer {
    private final CraftingInput container;
    private final List<ItemStack> items;

    public SimpleCraftingContainer(CraftingInput container) {
        this.container = container;
        this.items = new ArrayList<>(container.size());

        for (int i = 0; i < container.size(); i++) {
            this.items.add(container.getItem(i).copy());
        }
    }

    public void replaceContainers() {
        for (int i = 0; i < items.size(); i++) {
            ItemStack original = items.get(i);

            if (original.isEmpty()) {
                continue;
            }

            ItemStack replaced = FoodContainerExpansion.replaceStack(original.copy());
            items.set(i, replaced);
        }
    }

    public void replaceContainers(Item container) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack original = items.get(i);

            if (original.isEmpty()) {
                continue;
            }

            ItemStack replaced = FoodContainerExpansion.replaceStack(container, original.copy());
            items.set(i, replaced);
        }
    }


    @Override
    public int getWidth() {
        return container.width();
    }

    @Override
    public int getHeight() {
        return container.height();
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(items, index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        items.set(index, stack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents contents) {
        for (ItemStack stack : items) {
            contents.accountSimpleStack(stack);
        }
    }
}