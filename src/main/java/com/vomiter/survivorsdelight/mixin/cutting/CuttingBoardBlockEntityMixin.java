package com.vomiter.survivorsdelight.mixin.cutting;

import com.vomiter.survivorsdelight.recipe.cutting.CuttingContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(value = CuttingBoardBlockEntity.class, remap = false)
public abstract class CuttingBoardBlockEntityMixin {
    @Inject(
            method = "processStoredItemUsingTool(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD")
    )
    private void sdl$setInputAtHead(ItemStack toolStack, @Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack input = ((CuttingBoardBlockEntity)(Object)this).getStoredItem();
        if (input != null && !input.isEmpty()) {
            CuttingContext.set(input.copy());
        }
    }

    @Inject(
            method = "processStoredItemUsingTool(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("RETURN")
    )
    private void sdl$clearInputAtReturn(ItemStack toolStack, @Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingContext.clear();
    }
}