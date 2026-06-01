package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.common.container.SDCabinetBlockEntity;
import com.vomiter.survivorsdelight.common.food.block.DecayingFeastBlockEntity;
import com.vomiter.survivorsdelight.common.food.block.DecayingPieBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class SDBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SurvivorsDelight.MODID);

    private static Block[] cabinetBlocks() {
        return SDBlocks.CABINETS.values()
                .stream()
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    private static Block[] feastBlock(){
        return BuiltInRegistries.BLOCK
                .stream()
                .filter(b -> b instanceof FeastBlock)
                .toArray(Block[]::new);
    }

    private static Block[] pieBlock(){
        return BuiltInRegistries.BLOCK
                .stream()
                .filter(b -> b instanceof PieBlock)
                .toArray(Block[]::new);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SDCabinetBlockEntity>> SD_CABINET =
            BLOCK_ENTITIES.register("cabinet",
                    () -> BlockEntityType.Builder.of(SDCabinetBlockEntity::new, cabinetBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayingFeastBlockEntity>> FEAST_DECAYING =
            BLOCK_ENTITIES.register("feast_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingFeastBlockEntity::new,
                            feastBlock()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayingPieBlockEntity>> PIE_DECAYING =
            BLOCK_ENTITIES.register("pie_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingPieBlockEntity::new,
                            pieBlock()
                    ).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
