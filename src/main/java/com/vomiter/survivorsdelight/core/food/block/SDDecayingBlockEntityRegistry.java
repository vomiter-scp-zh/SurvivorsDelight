package com.vomiter.survivorsdelight.core.food.block;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class SDDecayingBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE , SurvivorsDelight.MODID);

    public static void register(IEventBus modEventBus){
        BLOCK_ENTITIES.register(modEventBus);
    }


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayingFeastBlockEntity>> FEAST_DECAYING =
            BLOCK_ENTITIES.register("feast_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingFeastBlockEntity::new,
                            ModBlocks.ROAST_CHICKEN_BLOCK.get(),
                            ModBlocks.STUFFED_PUMPKIN_BLOCK.get(),
                            ModBlocks.HONEY_GLAZED_HAM_BLOCK.get(),
                            ModBlocks.SHEPHERDS_PIE_BLOCK.get(),
                            ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayingPieBlockEntity>> PIE_DECAYING =
            BLOCK_ENTITIES.register("pie_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingPieBlockEntity::new,
                            ModBlocks.APPLE_PIE.get(),
                            ModBlocks.CHOCOLATE_PIE.get(),
                            ModBlocks.SWEET_BERRY_CHEESECAKE.get()
                    ).build(null));


    private SDDecayingBlockEntityRegistry() {}
}
