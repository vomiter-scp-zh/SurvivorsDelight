package com.vomiter.survivorsdelight.mixin.device.skillet;

import com.vomiter.survivorsdelight.core.device.skillet.SDSkilletItem;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletUtil;
import com.vomiter.survivorsdelight.data.tags.SDTags;
import com.vomiter.survivorsdelight.data.tags.SDTags.ItemTags;
import com.vomiter.survivorsdelight.util.HeatHelper;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.Objects;
import java.util.Optional;

@Mixin(value = SkilletBlockEntity.class, remap = false)
public abstract class SkilletBlockEntity_TFCHeatMixin implements HeatableBlockEntity {

    @Unique private static final org.apache.logging.log4j.Logger survivorsDelight$LOG =
            org.apache.logging.log4j.LogManager.getLogger("SurvivorsDelight/SkilletMixin");
    
    @Final @Shadow private ItemStackHandler inventory;
    @Shadow private int cookingTime;
    @Shadow private ResourceLocation lastRecipeID;

    @Shadow private ItemStack skilletStack;

    @Shadow public abstract void setSkilletItem(ItemStack stack);

    // tfc cached recipe
    @Unique private HeatingRecipe sdtfc$cachedHeatingRecipe = null;

    /**
     Try to cache TFC HeatingRecipe if there's no vanilla campfire recipe, and allow input of heating recipe ingredients.
     */
    @Inject(method = "addItemToCook", at = @At("HEAD"), cancellable = true)
    private void sdtfc$acceptHeatingRecipeOnAdd(ItemStack addedStack, Player player, CallbackInfoReturnable<ItemStack> cir) {
        survivorsDelight$LOG.info("entered addItemToCook inject");
        final BlockEntity self = (BlockEntity) (Object) this;
        final Level lvl = self.getLevel();
        if (lvl == null || addedStack.isEmpty()) return;

        // check heating recipes
        HeatingRecipe heating = HeatingRecipe.getRecipe(new ItemStackInventory(addedStack));
        if (heating == null) return; //invalid item

        // Mimicking original behavior
        // 1) not allowed if Waterlogged
        final BlockState state = self.getBlockState();
        if (state.hasProperty(vectorwing.farmersdelight.common.block.SkilletBlock.WATERLOGGED) &&
                state.getValue(vectorwing.farmersdelight.common.block.SkilletBlock.WATERLOGGED)) {
            player.displayClientMessage(vectorwing.farmersdelight.common.utility.TextUtils.getTranslation("block.skillet.underwater"), true);
            cir.setReturnValue(addedStack);
            return;
        }

        // 2) set HeatingRecipe cache
        this.sdtfc$cachedHeatingRecipe = heating;

        // 3) insert
        boolean wasEmpty = inventory.getStackInSlot(0).isEmpty();
        ItemStack remainder = inventory.insertItem(0, addedStack.copy(), false);
        if (!ItemStack.matches(remainder, addedStack)) {
            this.lastRecipeID = null;
            this.cookingTime = 0;

            //play sound
            final BlockPos pos = self.getBlockPos();
            if (!ItemUtils.isInventoryEmpty(inventory) && wasEmpty) {
                lvl.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        vectorwing.farmersdelight.common.registry.ModSounds.BLOCK_SKILLET_ADD_FOOD.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.0F);
            }

            cir.setReturnValue(remainder);
            return;
        }

        cir.setReturnValue(addedStack);
    }


    /**
     If heating recipe exist -> add heat until the temperature is reached and done
     */
    @Inject(method = "cookAndOutputItems", at = @At("HEAD"), cancellable = true)
    private void sdtfc$cookWithBelowTemperature(ItemStack cookingStack, Level level, CallbackInfo ci) {
        survivorsDelight$LOG.info("cooking");
        final BlockEntity self = (BlockEntity) (Object) this;
        final BlockPos pos = self.getBlockPos();

        if (level == null || cookingStack.isEmpty()) return;
        if (sdtfc$hasCampfireRecipe(level, cookingStack)) return;
        if(skilletStack.getItem() instanceof SDSkilletItem sdSkilletItem){
            if(!sdSkilletItem.canCook(skilletStack) && skilletStack.is(SDTags.ItemTags.RETURN_COPPER_SKILLET)){
                CompoundTag tag = skilletStack.serializeNBT();
                tag.putString("id", SkilletMaterial.COPPER.location().toString());
                ItemStack newSkilletStack = ItemStack.of(tag);
                newSkilletStack.setDamageValue(0);
                setSkilletItem(newSkilletStack);

                level.destroyBlock(pos, true);
            }
        }

        // get TFC HeatingRecipe
        if (sdtfc$cachedHeatingRecipe == null) {
            sdtfc$cachedHeatingRecipe = HeatingRecipe.getRecipe(new ItemStackInventory(cookingStack));
            if (sdtfc$cachedHeatingRecipe == null) return; //fallback to original
        }

        //check and modify current heat and temperature
        final float belowTemp = sdtfc$getBelowDeviceTemperatureSafe();
        final IHeat heat = HeatCapability.get(cookingStack);
        if (heat == null || belowTemp <= 0f) {
            ci.cancel();
            return;
        }
        HeatCapability.addTemp(heat, belowTemp);

        //if the recipe temperature is reached (this part is modified from iron grill)
        if (sdtfc$cachedHeatingRecipe.isValidTemperature(heat.getTemperature())) {
            final ItemStack result = sdtfc$cachedHeatingRecipe.assemble(new ItemStackInventory(cookingStack), level.registryAccess());

            FoodCapability.applyTrait(result, SkilletUtil.skilletCooked);
            FoodCapability.updateFoodDecayOnCreate(result);

            final BlockState state = self.getBlockState();

            Direction direction = state.getValue(SkilletBlock.FACING).getClockWise();
            ItemUtils.spawnItemEntity(
                    level, result.copy(),
                    pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                    direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F
            );

            cookingTime = 0;
            inventory.extractItem(0, 1, false);
            level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    ModSounds.BLOCK_SKILLET_ADD_FOOD.get(), SoundSource.BLOCKS, 0.8F, 1.0F);

            sdtfc$cachedHeatingRecipe = null;
            if(!level.isClientSide && skilletStack.getItem() instanceof SDSkilletItem){
                FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(Objects.requireNonNull(level.getServer()).getLevel(level.dimension()));
                fakePlayer.setGameMode(GameType.SURVIVAL);
                skilletStack.hurtAndBreak(1 + SkilletUtil.extraHurtForTemperature(skilletStack, belowTemp), fakePlayer, (user) -> {});
            }
        }

        ci.cancel();
    }

    @Inject(method = "removeItem", at = @At("TAIL"))
    private void sdtfc$clearCacheOnRemove(CallbackInfoReturnable<ItemStack> cir) {
        sdtfc$cachedHeatingRecipe = null;
    }

    /**
     get temperature of the block below the skillet
     */
    @Unique
    private float sdtfc$getBelowDeviceTemperatureSafe() {
        final BlockEntity self = (BlockEntity) (Object) this;
        final Level level = self.getLevel();
        if (level == null) return 0f;
        final BlockPos pos = self.getBlockPos();
        return HeatHelper.getTargetTemperature(pos, level, requiresDirectHeat(), HeatHelper.GetterType.BLOCK);
    }

    /**
     Check campfire cooking recipe. Basically copied from FD getMatchingRecipe
     */
    @Unique
    private boolean sdtfc$hasCampfireRecipe(Level level, ItemStack stack) {
        var recipeWrapper = new SimpleContainer(new ItemStack[]{stack});
        if (this.lastRecipeID != null) {
            Recipe<Container> recipe = ((RecipeManagerAccessor)level.getRecipeManager()).getRecipeMap(RecipeType.CAMPFIRE_COOKING).get(this.lastRecipeID);
            if (recipe instanceof CampfireCookingRecipe && recipe.matches(recipeWrapper, level)) {
                return true;
            }
        }

        Optional<CampfireCookingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, recipeWrapper, level);
        return recipe.isPresent();
    }

    @Unique
    private ItemStack sdtfc$getStoredStack() {
        return inventory.getStackInSlot(0);
    }
}
