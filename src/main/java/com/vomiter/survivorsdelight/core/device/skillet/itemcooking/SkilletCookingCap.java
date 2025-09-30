package com.vomiter.survivorsdelight.core.device.skillet.itemcooking;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = SurvivorsDelight.MODID) // 預設：GAME bus（給 PlayerEvent.Clone）
public final class SkilletCookingCap {

    public static final ResourceLocation ID =
            RLUtils.build(SurvivorsDelight.MODID, "skillet_cooking");

    public static final EntityCapability<ISkilletItemCookingData, Void> CAPABILITY =
            EntityCapability.createVoid(ID, ISkilletItemCookingData.class);

    @EventBusSubscriber(modid = SurvivorsDelight.MODID)
    public static final class Registration {
        @SubscribeEvent
        public static void registerCaps(RegisterCapabilitiesEvent event) {
            event.registerEntity(CAPABILITY, EntityType.PLAYER,
                    (player, ctx) -> new SkilletItemCookingData());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldP = event.getOriginal();
        Player newP = event.getEntity();
        var lookup = event.getEntity().level().registryAccess(); // RegistryAccess implements HolderLookup.Provider

        ISkilletItemCookingData oldCap = oldP.getCapability(CAPABILITY);
        ISkilletItemCookingData newCap = newP.getCapability(CAPABILITY);

        if (oldCap != null && newCap != null) {
            ((SkilletItemCookingData) newCap).load(lookup, ((SkilletItemCookingData) oldCap).save(lookup));
        }
    }

    public static ISkilletItemCookingData get(Player player) {
        ISkilletItemCookingData cap = player.getCapability(CAPABILITY);
        if (cap == null) {
            throw new IllegalStateException("SkilletCooking capability missing");
        }
        return cap;
    }
}
