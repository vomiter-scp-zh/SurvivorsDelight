package com.vomiter.survivorsdelight.core.device.skillet.itemcooking;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.SDUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class SkilletCookingCap {

    public static final ResourceLocation ID =
            SDUtils.RLUtils.build(SurvivorsDelight.MODID, "skillet_cooking");

    // 1) 宣告：void context 能力
    public static final EntityCapability<ISkilletItemCookingData, Void> CAPABILITY =
            EntityCapability.createVoid(ID, ISkilletItemCookingData.class);

    private SkilletCookingCap() {}

    // 2) 註冊：掛在 Player 上；不需要任何快取
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerEntity(CAPABILITY, EntityType.PLAYER,
                (player, ctx) -> new SkilletItemCookingData());
    }

    // 3) Clone：把舊值存起來再讀到新玩家
    public static void onClone(PlayerEvent.Clone event) {
        final Player oldP = event.getOriginal();
        final Player newP = event.getEntity();

        final var lookup = newP.level().registryAccess();

        ISkilletItemCookingData oldCap = oldP.getCapability(CAPABILITY);
        ISkilletItemCookingData newCap = newP.getCapability(CAPABILITY);

        if (oldCap != null && newCap != null) {
            // 兩種常見拷貝作法擇一（看你的實作）：

            // (A) NBT save/load（你現成就有）
            ((SkilletItemCookingData) newCap).load(
                    lookup,
                    ((SkilletItemCookingData) oldCap).save(lookup)
            );

            // (B) 直接欄位拷貝（如果你有定義）
            // newCap.copyFrom(oldCap);
        }
    }

    // 4) 取用：直接從玩家拉出能力
    public static ISkilletItemCookingData get(Player player) {
        ISkilletItemCookingData cap = player.getCapability(CAPABILITY);
        if (cap == null) throw new IllegalStateException("SkilletCooking capability missing");
        return cap;
    }
}
