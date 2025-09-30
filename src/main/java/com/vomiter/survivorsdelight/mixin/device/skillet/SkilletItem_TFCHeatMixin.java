package com.vomiter.survivorsdelight.mixin.device.skillet;

import com.vomiter.survivorsdelight.core.device.skillet.SDSkilletItem;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.device.skillet.itemcooking.SkilletCookingCap;
import com.vomiter.survivorsdelight.data.tags.SDItemTags;
import com.vomiter.survivorsdelight.util.HeatHelper;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletUtil;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.SkilletItem;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.utility.TextUtils;

@Mixin(value = SkilletItem.class)
public abstract class SkilletItem_TFCHeatMixin {
    @Unique private static final String KEY_COOKING = "Cooking";

    @Unique
    private float sdtfc$getTemperatureNearby(Player player, LevelReader level) {
        BlockPos pos = player.blockPosition();
        float temperature = 0;
        for (BlockPos posNearby : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            temperature = Math.max(temperature, HeatHelper.getTemperature(posNearby, level, HeatHelper.GetterType.IN_HAND));
        }
        return temperature;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void sdtfc$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack skilletStack = player.getItemInHand(hand);
        InteractionHand otherHand = (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack heatingStack = player.getItemInHand(otherHand);
        if(skilletStack.getItem() instanceof SDSkilletItem sdSkilletItem){
            if(!sdSkilletItem.canCook(skilletStack)){
                cir.setReturnValue(InteractionResultHolder.fail(skilletStack));
                return;
            }
        }

        if (heatingStack.isEmpty()) return;
        float temperatureNearby = sdtfc$getTemperatureNearby(player, level);
        if (temperatureNearby <= 0) {
            if(skilletStack.getEnchantmentLevel(SDUtils.getEnchantHolder(level, Enchantments.FIRE_ASPECT)) >= 1){
                temperatureNearby = 300;
            }
            else {
                return;
            }
        }

        if (player.isUnderWater()) {
            player.displayClientMessage(TextUtils.getTranslation("item.skilletStack.underwater"), true);
            return;
        }
        HeatingRecipe recipe = HeatingRecipe.getRecipe((heatingStack));
        if (recipe == null) return;
        if (!level.isClientSide) {
            ItemStack unit = heatingStack.split(1);
            if (unit.isEmpty()) return;
            IHeat heat = HeatCapability.get(unit);
            if (heat == null) {
                heatingStack.grow(1);
                return;
            }
            HeatCapability.addTemp(heat, temperatureNearby);
            skilletStack.set(ModDataComponents.SKILLET_INGREDIENT, new ItemStackWrapper(unit));
            var data = SkilletCookingCap.get(player);
            data.setCooking(unit);
            data.setTargetTemperature(recipe.getTemperature());
            data.setHand(hand);
        }
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.pass(skilletStack));
    }

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void sdtfc$onUseTick(Level level, LivingEntity living, ItemStack skilletStack, int remainingUseTicks, CallbackInfo ci) {
        if (!(living instanceof Player player)) return;
        EquipmentSlot equipmentSlot = player.getUsedItemHand().equals(InteractionHand.MAIN_HAND)? EquipmentSlot.MAINHAND: EquipmentSlot.OFFHAND;
        var data = SkilletCookingCap.get(player);
        ItemStack cooking = data.getCooking();
        if (cooking.isEmpty()) return;
        float temperatureNearby = sdtfc$getTemperatureNearby(player, level);
        if (temperatureNearby <= 0) {
            if(skilletStack.getEnchantmentLevel(SDUtils.getEnchantHolder(level, Enchantments.FIRE_ASPECT)) >= 1){
                temperatureNearby = 300;
            }
            else {
                return;
            }
        }
        IHeat heat = HeatCapability.get(cooking);
        if (!level.isClientSide) {
            if (heat == null) {
                if (!player.addItem(cooking)) player.drop(cooking, false);
                data.clear();
                if (player.isUsingItem()) player.stopUsingItem();
                return;
            }

            HeatCapability.addTemp(heat, temperatureNearby);
            if(heat.getTemperature() < data.getTargetTemperature()) return;

            HeatingRecipe recipe = HeatingRecipe.getRecipe((cooking));
            if (recipe != null && recipe.isValidTemperature(heat.getTemperature())) {
                ItemStack result = recipe.assembleItem(cooking);
                if (!result.isEmpty()) {
                    FoodCapability.applyTrait(result, SkilletUtil.skilletCooked);
                    if (!player.addItem(result)) {
                        player.drop(result, false);
                    }
                }
                skilletStack.remove(ModDataComponents.SKILLET_INGREDIENT);

                if(skilletStack.getItem() instanceof SDSkilletItem){
                    skilletStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                    if(skilletStack.is(SDItemTags.RETURN_COPPER_SKILLET) && !(((SDSkilletItem) skilletStack.getItem()).canCook(skilletStack))){
                        InteractionHand hand = player.getUsedItemHand();
                        var lookup = level.registryAccess(); // RegistryAccess implements HolderLookup.Provider
                        CompoundTag tag = (CompoundTag) skilletStack.save(lookup);
                        tag.putString("id", SkilletMaterial.COPPER.location().toString());
                        ItemStack newSkilletStack = ItemStack.parseOptional(lookup, tag);
                        newSkilletStack.setDamageValue(0);
                        player.onEquippedItemBroken(skilletStack.getItem(), equipmentSlot);
                        player.setItemInHand(hand, newSkilletStack);
                    }
                }
                data.clear();
                if (player.isUsingItem()) player.stopUsingItem();
            }
        }
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void sdtfc$releaseUsing(ItemStack skilletStack, Level level, LivingEntity living, int timeLeft, CallbackInfo ci) {
        if (!(living instanceof Player player)) return;
        var data = SkilletCookingCap.get(player);
        ItemStackWrapper storedStack = skilletStack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY);

        if(!storedStack.getStack().isEmpty()){
            ItemStack fakeCookingStack = storedStack.getStack();
            HeatingRecipe recipe = HeatingRecipe.getRecipe((fakeCookingStack));
            if (recipe == null) return;
            if(!level.isClientSide){
                ItemStack cooking = data.getCooking();
                if (!player.addItem(cooking)) player.drop(cooking, false);
            }
            data.clear();
            skilletStack.remove(ModDataComponents.SKILLET_INGREDIENT);
            skilletStack.remove(ModDataComponents.COOKING_TIME_LENGTH);
            ci.cancel();
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void sdtfc$getUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(20 * 60 * 60 * 3);
    }
}
