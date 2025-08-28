package com.vomiter.survivorsdelight.mixin.skillet;

import com.vomiter.survivorsdelight.util.SkilletUtil;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.SkilletItem;
import vectorwing.farmersdelight.common.utility.TextUtils;

@Mixin(value = SkilletItem.class)
public abstract class SkilletItem_TFCHeatMixin {
    @Unique private ItemStack sdtfc$cookingStackUnit = ItemStack.EMPTY;
    @Unique private HeatingRecipe sdtfc$cachedHeatingRecipe = null;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void sdtfc$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack skilletStack = player.getItemInHand(hand);

        InteractionHand otherHand = (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack heatingStack = player.getItemInHand(otherHand);
        if (heatingStack.isEmpty()) return;

        float temperatureNearby = sdtfc$getTemperatureNearby(player, level);
        if (temperatureNearby <= 0) return;

        if (player.isUnderWater()) {
            player.displayClientMessage(TextUtils.getTranslation("item.skillet.underwater"), true);
            return;
        }

        HeatingRecipe recipe = HeatingRecipe.getRecipe(new ItemStackInventory(heatingStack));
        if (recipe == null) return;

        ItemStack heatingStackUnit = heatingStack.split(1);
        if (heatingStackUnit.isEmpty()) return;

        IHeat heat = HeatCapability.get(heatingStackUnit);
        if (heat == null) {
            heatingStack.grow(1);
            return;
        }

        if (!level.isClientSide) {
            HeatCapability.addTemp(heat, temperatureNearby);
        }

        this.sdtfc$cookingStackUnit = heatingStackUnit;
        this.sdtfc$cachedHeatingRecipe = recipe;
        skilletStack.getOrCreateTag().put("Cooking", sdtfc$cookingStackUnit.serializeNBT());
        skilletStack.getOrCreateTag().putInt("CookTimeHandheld", 20 * 60 * 60 * 3);

        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.pass(skilletStack));
    }

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void sdtfc$onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (!(living instanceof Player player)) return;
        if (this.sdtfc$cookingStackUnit.isEmpty() || this.sdtfc$cachedHeatingRecipe == null) return;

        float temperatureNearby = sdtfc$getTemperatureNearby(player, level);
        if (temperatureNearby <= 0) return;

        IHeat heat = HeatCapability.get(this.sdtfc$cookingStackUnit);
        if (heat == null) return;

        if (!level.isClientSide) {
            HeatCapability.addTemp(heat, temperatureNearby);

            if (this.sdtfc$cachedHeatingRecipe.isValidTemperature(heat.getTemperature())) {
                ItemStack result = this.sdtfc$cachedHeatingRecipe.assemble(
                        new ItemStackInventory(this.sdtfc$cookingStackUnit), level.registryAccess()
                );

                if (!result.isEmpty()) {
                    FoodCapability.applyTrait(result, SkilletUtil.skilletCooked);
                    FoodCapability.updateFoodDecayOnCreate(result);
                    if (!player.addItem(result)) {
                        player.drop(result, false);
                    }
                }

                this.sdtfc$cookingStackUnit = ItemStack.EMPTY;
                this.sdtfc$cachedHeatingRecipe = null;

                if (player.isUsingItem()) {
                    player.stopUsingItem();
                }
            }
        }
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void sdtfc$releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft, CallbackInfo ci) {
        if (!(living instanceof Player player)) return;
        if (!this.sdtfc$cookingStackUnit.isEmpty()) {
            ItemStack giveBack = this.sdtfc$cookingStackUnit.copy();
            this.sdtfc$cookingStackUnit = ItemStack.EMPTY;
            this.sdtfc$cachedHeatingRecipe = null;

            if (!giveBack.isEmpty()) {
                if (!player.addItem(giveBack)) {
                    player.drop(giveBack, false);
                }
            }
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void sdtfc$getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(20 * 60 * 60 * 3);
    }

    @Unique
    private float sdtfc$getTemperatureNearby(Player player, LevelReader level) {
        BlockPos pos = player.blockPosition();
        float temperature = 0;
        for (BlockPos posNearby : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            temperature = Math.max(temperature, SkilletUtil.getTemperature(posNearby, level));
        }
        return temperature;
    }
}
