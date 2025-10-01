package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.SkilletBlock;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

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
}
