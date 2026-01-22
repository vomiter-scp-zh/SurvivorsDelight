package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.content.container.SDCabinetBlockEntity;
import com.vomiter.survivorsdelight.content.food.block.DecayingFeastBlockEntity;
import com.vomiter.survivorsdelight.content.food.block.DecayingPieBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class SDBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SurvivorsDelight.MODID);
    private static Block[] cabinetBlocks() {
        return SDBlocks.CABINETS.values()
                .stream()
                .map(RegistryObject::get)
                .toArray(Block[]::new);
    }
    public static final RegistryObject<BlockEntityType<SDCabinetBlockEntity>> SD_CABINET =
            BLOCK_ENTITIES.register(
                    "cabinet",
                    () -> BlockEntityType.Builder.of(
                            SDCabinetBlockEntity::new,
                            cabinetBlocks()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DecayingFeastBlockEntity>> FEAST_DECAYING =
            BLOCK_ENTITIES.register("feast_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingFeastBlockEntity::new,
                            ModBlocks.ROAST_CHICKEN_BLOCK.get(),
                            ModBlocks.STUFFED_PUMPKIN_BLOCK.get(),
                            ModBlocks.HONEY_GLAZED_HAM_BLOCK.get(),
                            ModBlocks.SHEPHERDS_PIE_BLOCK.get(),
                            ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DecayingPieBlockEntity>> PIE_DECAYING =
            BLOCK_ENTITIES.register("pie_decaying",
                    () -> BlockEntityType.Builder.of(
                            DecayingPieBlockEntity::new,
                            ModBlocks.APPLE_PIE.get(),
                            ModBlocks.CHOCOLATE_PIE.get(),
                            ModBlocks.SWEET_BERRY_CHEESECAKE.get()
                    ).build(null));


}
