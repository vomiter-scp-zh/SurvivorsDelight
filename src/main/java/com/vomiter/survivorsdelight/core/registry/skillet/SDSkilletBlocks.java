package com.vomiter.survivorsdelight.core.registry.skillet;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.mixin.BlockEntityTypeAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SDSkilletBlocks {
    private SDSkilletBlocks() {}
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SurvivorsDelight.MODID);

    public static final Map<SkilletMaterial, RegistryObject<Block>> SKILLETS = new EnumMap<>(SkilletMaterial.class);
    public static RegistryObject<Block> get(SkilletMaterial m){
        return SKILLETS.get(m);
    }
    public static ResourceKey<Block> getKey(SkilletMaterial m){
        return SKILLETS.get(m).getKey();
    }

    public static final RegistryObject<Block> FARMER =BLOCKS.register("skillet/farmer", () ->
            new SkilletBlock(
            BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(0.5f, 6.0f)
                    .sound(SoundType.LANTERN)
    ));

    static {
        for (SkilletMaterial m : SkilletMaterial.values()) {
            RegistryObject<Block> ro = BLOCKS.register(m.path(), () ->
                    new SkilletBlock(BlockBehaviour.Properties
                            .of()
                            .mapColor(MapColor.METAL)
                            .noOcclusion()
                            .strength(0.5f, 6.0f)
                            .sound(SoundType.LANTERN)
                    )
            );
            SKILLETS.put(m, ro);
        }
    }

    @Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Compat {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                BlockEntityType<SkilletBlockEntity> type = ModBlockEntityTypes.SKILLET.get();
                BlockEntityTypeAccessor acc = (BlockEntityTypeAccessor)type;
                HashSet<Block> validBlocks = new HashSet<>(acc.getValidBlocks());
                acc.setValidBlocks(validBlocks);
                SDSkilletBlocks.SKILLETS.values().forEach(ro -> validBlocks.add(ro.get()));
                validBlocks.add(FARMER.get());
            });
        }
    }
}
