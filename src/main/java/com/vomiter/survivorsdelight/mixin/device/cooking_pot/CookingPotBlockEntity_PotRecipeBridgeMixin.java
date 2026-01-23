package com.vomiter.survivorsdelight.mixin.device.cooking_pot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.adapter.cooking_pot.bridge.ICookingPotRecipeBridge;
import com.vomiter.survivorsdelight.adapter.cooking_pot.bridge.TFCPotRecipeBridgeFD;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.ICookingPotFluidAccess;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.IFluidRequiringRecipe;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.capabilities.food.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModItems;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntity_PotRecipeBridgeMixin extends SyncedBlockEntity implements ICookingPotRecipeBridge {
    @Shadow @Final private ItemStackHandler inventory;
    @Shadow private ResourceLocation lastRecipeID;
    @Shadow private int cookTime;
    @Shadow private boolean checkNewRecipe;

    @Shadow protected abstract void ejectIngredientRemainder(ItemStack remainderStack);

    @Unique private @Nullable TFCPotRecipeBridgeFD sdtfc$cachedBridge = null;
    @Unique private ItemStack sdtfc$cachedDynamicFoodResult = ItemStack.EMPTY;


    // ====== Get matching recipe and cache it for later comparison
    @ModifyVariable(method = "getMatchingRecipe", at = @At("STORE"))
    private Optional<CookingPotRecipe> fillWithTfcBridgeWhenEmpty(
            Optional<CookingPotRecipe> original,
            @Local(argsOnly = true) RecipeWrapper inventoryWrapper
    ) {
        if (original.isPresent()) {
            var r = original.get();
            if (this.lastRecipeID == null || !this.lastRecipeID.equals(r.getId())) {
                // 配方 ID 改變 -> 清兩種快取
                sdtfc$cachedBridge = null;
                sdtfc$cachedDynamicFoodResult = ItemStack.EMPTY;
                cookTime = 0;
                this.lastRecipeID = r.getId();
            }

            return original;
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

        this.sdtfc$cachedBridge = bridge;
        return Optional.of(bridge);
    }

    @Inject(method = "cookingTick", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;canCook(Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;)Z"))
    private static void handleDynamicCookingPotRecipe(
            Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot, CallbackInfo ci,
            @Local Optional<CookingPotRecipe> recipe
    ){

        if(!((ICookingPotRecipeBridge)cookingPot).sdtfc$getCachedDynamicFoodResult().isEmpty()) return;
        recipe.ifPresent(r -> {
            if(r instanceof TFCPotRecipeBridgeFD) return;
            var originalResult = r.getResultItem(level.registryAccess()).copy();
            if(FoodCapability.get(originalResult) instanceof FoodHandler.Dynamic dynamicFood){
                NonNullList<Ingredient> inputItems = NonNullList.create();
                List<ItemStack> foodIngredients = new ArrayList<>();
                FoodData baseFood = FoodData.decayOnly(4.5f);
                float[] nutrition = baseFood.nutrients();
                float saturation = baseFood.saturation();
                float water = baseFood.water();
                int foodIngCount = 0;
                int hunger = 0;
                int resultCount = r.getResultItem(level.registryAccess()).getCount();
                for (int i = 0; i < cookingPot.getInventory().getSlots(); i++) {
                    if (i > 5) continue;
                    var stack = cookingPot.getInventory().getStackInSlot(i);
                    if(!stack.isEmpty()) {
                        if(FoodCapability.get(stack) == null) continue;
                        foodIngCount ++;
                    }
                }
                TagKey<Fluid> MILKS_TAG = TagKey.create(Registries.FLUID, SDUtils.RLUtils.build("tfc", "milks"));
                var fluid = ((IFluidRequiringRecipe)r).sdtfc$getFluidIngredient();
                if(SDUtils.TagUtils.fluidIngredientMatchesTag(fluid, MILKS_TAG)) nutrition[Nutrient.DAIRY.ordinal()] += 1;

                for (int i = 0; i < cookingPot.getInventory().getSlots(); i++) {
                    if(i > 5) continue;
                    var stack = cookingPot.getInventory().getStackInSlot(i);
                    if(!stack.isEmpty()) {
                        inputItems.add(Ingredient.of(stack.getItem()));
                        if(FoodCapability.get(stack) == null) continue;
                        FoodData data = Objects.requireNonNull(FoodCapability.get(stack)).getData();
                        foodIngredients.add(stack.getItem().getDefaultInstance());
                        for (Nutrient nutrient : Nutrient.VALUES)
                        {
                            float extra = 0f;
                            if(stack.is(ModItems.RAW_PASTA.get()) && nutrient.equals(Nutrient.GRAIN)) extra = 1.0f;
                            else if(stack.is(SDTags.ItemTags.create("tfc", "foods/grains")) && nutrient.equals(Nutrient.GRAIN)) extra = 1.0f;
                            else if(stack.is(SDTags.ItemTags.create("firmalife", "foods/extra_dough")) && nutrient.equals(Nutrient.GRAIN)) extra = 1.5f;
                            else if(stack.is(SDTags.ItemTags.TFC_DOUGHS) && nutrient.equals(Nutrient.GRAIN)){
                                extra = SDUtils.getExtraNutrientAfterCooking(stack, Nutrient.GRAIN, level) + data.nutrient(nutrient) * 0.2f;
                            }
                            else if(stack.is(SDTags.ItemTags.TFC_RAW_MEATS) && nutrient.equals(Nutrient.PROTEIN)) {
                                extra = SDUtils.getExtraNutrientAfterCooking(stack, Nutrient.PROTEIN, level) + data.nutrient(nutrient) * 0.2f;
                            }
                            nutrition[nutrient.ordinal()] += (data.nutrient(nutrient) * (1f - 0.04f * (float)foodIngCount) + extra) / resultCount;
                        }
                        water += data.water() / resultCount;
                        saturation += data.saturation() / resultCount;
                        hunger = Math.max(hunger, data.hunger());
                    }
                }

                foodIngredients.sort(Comparator.comparing(ItemStack::getCount)
                        .thenComparing(item -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.getItem()))));
                dynamicFood.setIngredients(foodIngredients);
                dynamicFood.setFood(FoodData.create((5 + hunger) / 2, water, saturation, nutrition, 4.5f));
                ((ICookingPotRecipeBridge)cookingPot).sdtfc$setCachedDynamicFoodResult(originalResult);
            }

        });

    }

    @ModifyExpressionValue(method = "processCooking", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;getResultItem(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", remap = true))
    private ItemStack applyDynamicResult(ItemStack original){
        if(sdtfc$cachedDynamicFoodResult.isEmpty()) return original;
        return sdtfc$cachedDynamicFoodResult;
    }

    @Inject(
            method = "processCooking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V",
                    remap = true
            )
    )
    private void survivorsdelight$beforeIngredientShrink(
            CookingPotRecipe recipe,
            CookingPotBlockEntity cookingPot,
            CallbackInfoReturnable<Boolean> cir,
            @Local(name = "slotStack") ItemStack slotStack
    ) {
        if(slotStack.hasCraftingRemainingItem()) return;
        final ItemStack output = FoodCapability.get(slotStack) instanceof DynamicBowlHandler handler ? handler.getBowl() : ItemStack.EMPTY;
        ejectIngredientRemainder(output);
    }


    //====== Prevent soup with 4 fruits cooking when there's soup with 5 fruits stored in the pot
    @Redirect(method = "canCook(Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            ),
            remap = true
    )
    private boolean compareStacks(ItemStack a, ItemStack b) {
        if(FoodCapability.get(a) == null) return ItemStack.isSameItem(a, b);
        if(!sdtfc$cachedDynamicFoodResult.isEmpty()) return FoodCapability.areStacksStackableExceptCreationDate(a, sdtfc$cachedDynamicFoodResult);
        return FoodCapability.areStacksStackableExceptCreationDate(a, b);
    }

    @Redirect(
            method = "processCooking",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;grow(I)V"),
            remap = true)
    private void changeGrowToMerge(ItemStack instance, int p_41770_, @Local(argsOnly = true) CookingPotRecipe recipe){
        assert this.level != null;
        if(sdtfc$cachedDynamicFoodResult.isEmpty()){
            ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
            FoodCapability.mergeItemStacks(instance, resultStack.copy());
        }
        else{
            FoodCapability.mergeItemStacks(instance, sdtfc$cachedDynamicFoodResult);
        }
    }

    @Inject(
            method = "processCooking",
            at = @At("RETURN")
    )
    private void resetCached(CookingPotRecipe recipe, CookingPotBlockEntity cookingPot, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()){
            sdtfc$cachedBridge = null;
            sdtfc$cachedDynamicFoodResult = ItemStack.EMPTY;
        }
    }

    public CookingPotBlockEntity_PotRecipeBridgeMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void sdtfc$setCachedBridge(TFCPotRecipeBridgeFD recipe) {
        sdtfc$cachedBridge = recipe;
    }

    @Override
    public TFCPotRecipeBridgeFD sdtfc$getCachedBridge() {
        return sdtfc$cachedBridge;
    }

    @Override
    public void sdtfc$setCachedDynamicFoodResult(ItemStack item){
        this.sdtfc$cachedDynamicFoodResult = item;
    }

    @Override
    public ItemStack sdtfc$getCachedDynamicFoodResult(){
        return sdtfc$cachedDynamicFoodResult;
    }


    @Inject(method = "createHandler", at = @At("RETURN"), cancellable = true)
    private void wrapHandler(CallbackInfoReturnable<ItemStackHandler> cir) {
        ItemStackHandler wrapped = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            sdtfc$beforeCheckNewRecipe(slot);
            if (slot >= 0 && slot < 6) {
                checkNewRecipe = true;
            }
            inventoryChanged();
        }

        private void sdtfc$beforeCheckNewRecipe(int slot) {
            sdtfc$cachedBridge = null;
            sdtfc$cachedDynamicFoodResult = ItemStack.EMPTY;
        }
    };

        cir.setReturnValue(wrapped);
    }

    @ModifyExpressionValue(
            method = "canCook(Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/items/ItemStackHandler;getSlotLimit(I)I")
    )
    private int sd$capFix$limit(int originalLimit) {
        return Math.min(originalLimit, inventory.getStackInSlot(6).getMaxStackSize());
    }

}

