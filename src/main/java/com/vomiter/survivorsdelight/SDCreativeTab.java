package com.vomiter.survivorsdelight;

import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.vomiter.survivorsdelight.SurvivorsDelight.MODID;

public class SDCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    private static void safeAccept(Supplier<? extends ItemLike> sup, CreativeModeTab.Output output) {
        if (sup == null) return;
        ItemLike itemLike = sup.get();
        if (itemLike != null) output.accept(itemLike); // Output 有 accept(ItemLike) 多載
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID + ".main"))
                    .icon(() -> new ItemStack(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).get()))
                    .displayItems((parameters, output) -> {
                        output.accept(SDSkilletItems.FARMER.get());
                        Arrays.stream(SkilletMaterial.values()).forEach(m -> {
                            safeAccept(SDSkilletPartItems.HEADS.get(m), output);
                            safeAccept(SDSkilletPartItems.UNFINISHED.get(m), output);
                            safeAccept(SDSkilletItems.SKILLETS.get(m), output);
                        });
                        output.accept(SDSkilletPartItems.LINING_SILVER.get());
                        output.accept(SDSkilletPartItems.LINING_TIN.get());
                    })
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(FarmersDelight.MODID, FarmersDelight.MODID))
                    .build()
    );
}
