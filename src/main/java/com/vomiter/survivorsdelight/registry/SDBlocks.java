package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.container.SDCabinetBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class SDBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SurvivorsDelight.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SurvivorsDelight.MODID);

    public static final Map<Wood, RegistryObject<Block>> CABINETS = new EnumMap<>(Wood.class);
    static {
        for (Wood wood : Wood.values()) {
            String name = "planks/cabinet/" + wood.getSerializedName();
            var ro = BLOCKS.register(
                    name,
                    () -> (Block) new SDCabinetBlock(
                            BlockBehaviour.Properties.copy(Blocks.BARREL)));
            CABINETS.put(wood, ro);
            BLOCK_ITEMS.register(name, () -> new BlockItem(ro.get(), new Item.Properties()));
        }
    }

}
