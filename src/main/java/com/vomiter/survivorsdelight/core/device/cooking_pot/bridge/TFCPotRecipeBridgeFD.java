package com.vomiter.survivorsdelight.core.device.cooking_pot.bridge;

import com.mojang.authlib.GameProfile;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.IFluidRequiringRecipe;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blocks.devices.PotBlock;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TFCPotRecipeBridgeFD extends CookingPotRecipe implements IFluidRequiringRecipe {

    private SizedFluidIngredient fluidStackIngredient = SizedFluidIngredient.of(FluidStack.EMPTY);

    public TFCPotRecipeBridgeFD(ResourceLocation id,
                                NonNullList<Ingredient> inputItems,
                                ItemStack output, ItemStack container,
                                int cookTime) {
        super("tfc_pot_bridge", null, inputItems, output, container, 0, cookTime);
    }

    // ---- IFluidRequiringRecipe ----
    @Override public @Nullable SizedFluidIngredient sdtfc$getFluidIngredient() { return fluidStackIngredient; }
    @Override public int sdtfc$getRequiredFluidAmount() { return fluidStackIngredient.amount(); }
    @Override public void sdtfc$setFluidRequirement(@Nullable SizedFluidIngredient ing, int amount) {
        if (ing == null) {
            fluidStackIngredient = SizedFluidIngredient.of(FluidStack.EMPTY);
        } else {
            fluidStackIngredient = new SizedFluidIngredient(ing.ingredient(), amount);
        }
    }
    public void setSizedFluidIngredient(SizedFluidIngredient ing){ this.fluidStackIngredient = ing; }

    // ---- 建立橋接配方（從 TFC 鍋配方動態轉 FD 配方）----
    public static @Nullable TFCPotRecipeBridgeFD bridge(Level level, IItemHandler items, IFluidHandler fluids) {
        // 你原本的配對器；若你已有 1.21 版 TFCPotRecipeMatcher，直接沿用
        PotRecipe tfc = TFCPotRecipeMatcher.findFirstMatch(level, items, fluids, new int[]{0,1,2,3,4,5}).orElse(null);
        if (tfc == null) return null;

        // ID 與輸入
        ResourceLocation id = SDUtils.RLUtils.build(SurvivorsDelight.MODID, "cooking_pot/" + tfc.hashCode());
        NonNullList<Ingredient> inputItems = NonNullList.create();
        for (int i = 0; i < Math.min(items.getSlots(), 6); i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty()) inputItems.add(Ingredient.of(stack.getItem()));
        }

        // 快照（你自己的工具類）
        PotBlockEntity.PotInventory inv = TFCPotInventorySnapshots.snapshot(level, items, fluids);

        // 關鍵：把 PotOutput 轉 ItemStack
        ItemStack output = getOutputAsItemStack(tfc, inv, level);
        // 若 output 為空，但仍需顯示容器，容器交給下方邏輯決定
        ItemStack container = output.getItem() == Items.MUSHROOM_STEW ? Items.BOWL.getDefaultInstance() : ItemStack.EMPTY;

        int cookTime = Math.max(1, (int) (tfc.getDuration() / 5f)); // 你原本的縮短時間
        TFCPotRecipeBridgeFD bridge = new TFCPotRecipeBridgeFD(id, inputItems, output, container, cookTime);
        bridge.setSizedFluidIngredient(tfc.getFluidIngredient()); // 1.21：用 SizedFluidIngredient
        return bridge;
    }

    // ---- PotOutput → ItemStack 的核心轉換 ----
    private static ItemStack getOutputAsItemStack(PotRecipe tfc, PotBlockEntity.PotInventory inv, Level level) {
        // 1) 先產生 PotOutput（不會改動鍋內容）
        PotOutput out = tfc.getOutput(inv);

        // 客戶端跳過（行為與你舊版一致）
        if (level.isClientSide) return ItemStack.EMPTY;

        // 建臨時鍋＋假玩家
        FakePlayer fake = FakePlayerFactory.get((ServerLevel) level, new GameProfile(UUID.randomUUID(), "sd-temp-fake"));
        fake.setGameMode(GameType.SURVIVAL);
        PotBlockEntity tempPot = new PotBlockEntity(BlockPos.ZERO, PotBlock.stateById(0));
        // 準備一個淨空的 inventory 給後續 onFinish 使用
        PotBlockEntity.PotInventory tempInv = new PotBlockEntity.PotInventory(tempPot);

        // 2) 嘗試用互動容器抽出（對湯/飲品/果醬等「需要容器」的輸出）
        // 你可視情況增減嘗試順序（碗→玻璃瓶→桶）
        List<ItemStack> tryContainers = new ArrayList<>();
        tryContainers.add(new ItemStack(Items.BOWL));
        tryContainers.add(new ItemStack(Items.GLASS_BOTTLE));

        ItemStack collected = ItemStack.EMPTY;
        // 最多拉 9 份，避免無限迴圈
        outer:
        for (ItemStack container : tryContainers) {
            for (int i = 0; i < 9; i++) {
                ItemStack single = container.copy();
                single.setCount(1);
                // 這類 onInteract 通常會把成品放進玩家背包或回傳 PASS
                var beforeCount = countNonEmpty(fake);
                out.onInteract(tempPot, fake, single);
                var afterCount = countNonEmpty(fake);

                // 如果有新物品進背包，取第一格（或掃描差異）當作產物
                if (afterCount > beforeCount) {
                    // 這裡簡化：直接拿背包第一個非空作為樣本
                    for (ItemStack s : fake.getInventory().items) {
                        if (!s.isEmpty()) {
                            collected = s.copy();
                            break;
                        }
                    }
                    if (!collected.isEmpty()) break outer;
                }

                // 若 output 自身會變空（有些實作在抽完會 isEmpty）
                if (out.isEmpty()) break outer;
            }
        }

        if (!collected.isEmpty()) {
            cleanup(fake, tempPot);
            return collected;
        }

        // 3) 若互動抽不出，走 onFinish 路徑（非湯品：把成品直接寫回鍋內槽位）
        out.onFinish(tempInv);
        // 依 TFC 鍋槽佈局：4..8 是成品槽（與你舊版一致），合併成一疊作為「代表輸出」
        ItemStack result = ItemStack.EMPTY;
        for (int slot = 4; slot <= 8; slot++) {
            ItemStack s = tempInv.getStackInSlot(slot);
            if (s.isEmpty()) continue;
            if (result.isEmpty()) {
                result = s.copy();
            } else if (ItemStack.isSameItemSameComponents(result, s)) {
                result.grow(s.getCount());
            }
        }

        cleanup(fake, tempPot);
        return result;
    }

    private static int countNonEmpty(FakePlayer p) {
        int c = 0;
        for (ItemStack s : p.getInventory().items) if (!s.isEmpty()) c++;
        return c;
    }

    private static void cleanup(FakePlayer p, PotBlockEntity pot) {
        p.remove(Entity.RemovalReason.DISCARDED);
        pot.invalidateCapabilities();
    }
}
