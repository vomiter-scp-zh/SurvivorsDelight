package com.vomiter.survivorsdelight.core.device.skillet.itemcooking;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID)
public class SkilletCookingCap {
    public static final ResourceLocation ID = ResourceLocation.tryBuild(SurvivorsDelight.MODID, "skillet_cooking");
    public static final Capability<ISkilletItemCookingData> CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final SkilletItemCookingData backend = new SkilletItemCookingData();
        private final LazyOptional<ISkilletItemCookingData> optional = LazyOptional.of(() -> backend);

        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
            return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
        }
        @Override public CompoundTag serializeNBT() { return backend.save(); }
        @Override public void deserializeNBT(CompoundTag nbt) { backend.load(nbt); }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) event.addCapability(ID, new Provider());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(CAPABILITY).ifPresent(oldCap ->
                event.getEntity().getCapability(CAPABILITY).ifPresent(newCap -> ((SkilletItemCookingData)newCap).load(((SkilletItemCookingData)oldCap).save()))
        );
    }

    public static ISkilletItemCookingData get(Player player) {
        return player.getCapability(CAPABILITY).orElseThrow(() -> new IllegalStateException("SkilletCooking capability missing"));
    }
}
