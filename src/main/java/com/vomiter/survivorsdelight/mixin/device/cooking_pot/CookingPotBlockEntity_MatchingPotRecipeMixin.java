package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.cooking_pot.TFCPotRecipeBridgeFD;
import com.vomiter.survivorsdelight.core.device.cooking_pot.ICookingPotFluidAccess;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public class CookingPotBlockEntity_MatchingPotRecipeMixin extends SyncedBlockEntity {
    @Shadow @Final private ItemStackHandler inventory;
    @Shadow private ResourceLocation lastRecipeID;
    @Shadow private int cookTime;
    @Unique private @Nullable TFCPotRecipeBridgeFD sdtfc$cachedBridge = null;

    @ModifyVariable(method = "getMatchingRecipe", at = @At("STORE"))
    private Optional<CookingPotRecipe> sdtfc$fillWithTfcBridgeWhenEmpty(
            Optional<CookingPotRecipe> original,
            @Local(argsOnly = true) RecipeWrapper inventoryWrapper
    ) {
        if (original.isPresent()) {
            return original; // 原生就有，尊重 FD
        }
        if(level != null && sdtfc$cachedBridge != null){
            if(sdtfc$cachedBridge.matches(inventoryWrapper, level)){
                return (Optional.of(sdtfc$cachedBridge));
            }
        }

        this.sdtfc$cachedBridge = null;

        var bridge = TFCPotRecipeBridgeFD.bridge(this.level, this.inventory, ((ICookingPotFluidAccess) this).sd$getFluidHandler());
        if(bridge == null) return Optional.empty();

        if (this.lastRecipeID != null && !this.lastRecipeID.equals(bridge.getId())) {
            cookTime = 0;
        }

        this.lastRecipeID = bridge.getId();
        SurvivorsDelight.LOGGER.info(lastRecipeID.getPath());

        // 更新本地快取（供下一輪“manager miss”時使用）
        this.sdtfc$cachedBridge = bridge;
        return Optional.of(bridge);
    }

    @Redirect(method = "canCook(Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            ),
            remap = true
    )
    private boolean sdtfc$compareStacks(ItemStack a, ItemStack b) {
        return FoodCapability.areStacksStackableExceptCreationDate(a, b);
    }

    @Redirect(
            method = "processCooking",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;grow(I)V"),
            remap = true)
    private void changeGrowToMerge(ItemStack instance, int p_41770_, @Local(argsOnly = true) CookingPotRecipe recipe){
        assert this.level != null;
        ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
        FoodCapability.mergeItemStacks(instance, resultStack);
    }

    @Inject(
            method = "processCooking",
            at = @At("RETURN")
    )
    private void resetCached(CookingPotRecipe recipe, CookingPotBlockEntity cookingPot, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()){
            sdtfc$cachedBridge = null;
        }
    }

    /*
    @ModifyExpressionValue(method = "getMatchingRecipe", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/mixin/accessor/RecipeManagerAccessor;getRecipeMap(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/Map;"))
    private Map<ResourceLocation, Recipe<?>> sdtfc$useCachedBridgeWhenManagerMiss(Map<ResourceLocation, Recipe<?>> original, @Local(argsOnly = true) RecipeWrapper inv) {
        if (this.lastRecipeID != null && sdtfc$cachedBridge != null) {
            return Map.of(sdtfc$cachedBridge.getId(), sdtfc$cachedBridge);
        }
        return original;
    }

     */

    public CookingPotBlockEntity_MatchingPotRecipeMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }
}
