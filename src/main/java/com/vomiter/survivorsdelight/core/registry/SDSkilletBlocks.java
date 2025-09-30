package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.mixin.BlockEntityTypeAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = SurvivorsDelight.MODID)
public class SDSkilletBlocks {
    private SDSkilletBlocks() {}
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(SurvivorsDelight.MODID);

    public static final Map<SkilletMaterial, Supplier<Block>> SKILLETS = new EnumMap<>(SkilletMaterial.class);
    public static Supplier<Block> get(SkilletMaterial m){
        return SKILLETS.get(m);
    }
    public static ResourceKey<Block> getKey(SkilletMaterial m){
        return SKILLETS.get(m).get().builtInRegistryHolder().getKey();
    }

    public static final Supplier<Block> FARMER = BLOCKS.register("skillet/farmer", () ->
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
            Supplier<Block> ro = BLOCKS.register(m.path(), () ->
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

    @EventBusSubscriber(modid = SurvivorsDelight.MODID)
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
