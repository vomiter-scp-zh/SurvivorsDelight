package com.vomiter.survivorsdelight.mixin.foodBlock;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.core.foodBlock.FeastDecayingBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.block.FeastBlock;

import java.util.function.Supplier;

@Mixin(value = FeastBlock.class, remap = false)
public abstract class FeastBlock_ServingMixin extends Block {
    @Shadow @Final public Supplier<Item> servingItem;

    public FeastBlock_ServingMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @ModifyVariable(
            method = "takeServing",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lvectorwing/farmersdelight/common/block/FeastBlock;getServingItem(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack sdtfc$patchServingAfterBuilt(
            ItemStack serving,
            @Local(argsOnly = true) LevelAccessor level,
            @Local(argsOnly = true) BlockPos pos,
            @Local(argsOnly = true) BlockState state
    ) {
        if (serving.isEmpty() || serving.getItem() != servingItem.get()) return serving;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FeastDecayingBlockEntity feastDecayingBlockEntity)) return serving;

        ItemStack src = feastDecayingBlockEntity.getStack();
        IFood srcFood = FoodCapability.get(src);
        IFood servingFood = FoodCapability.get(serving);

        if (srcFood == null || servingFood == null) return serving;

        servingFood.setCreationDate(srcFood.getCreationDate());
        servingFood.getTraits().addAll(srcFood.getTraits());
        return serving;
    }}
