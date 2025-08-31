package com.vomiter.survivorsdelight.core.foodBlock;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;


@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FDDecayingBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SurvivorsDelight.MODID);

    static {
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<BlockEntityType<FeastDecayingBlockEntity>> FEAST_DECAYING =
            BLOCK_ENTITIES.register("feast_decaying",
                    () -> BlockEntityType.Builder.of(
                            FeastDecayingBlockEntity::new,
                            ModBlocks.ROAST_CHICKEN_BLOCK.get(),
                            ModBlocks.STUFFED_PUMPKIN_BLOCK.get(),
                            ModBlocks.HONEY_GLAZED_HAM_BLOCK.get(),
                            ModBlocks.SHEPHERDS_PIE_BLOCK.get(),
                            ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<PieDecayingBlockEntity>> PIE_DECAYING =
            BLOCK_ENTITIES.register("pie_decaying",
                    () -> BlockEntityType.Builder.of(
                            PieDecayingBlockEntity::new,
                            ModBlocks.APPLE_PIE.get(),
                            ModBlocks.CHOCOLATE_PIE.get(),
                            ModBlocks.SWEET_BERRY_CHEESECAKE.get()
                    ).build(null));


    private FDDecayingBlockEntityRegistry() {}
}
