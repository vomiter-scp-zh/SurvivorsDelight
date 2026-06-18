package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.registry.component.MedleyContent;
import com.vomiter.survivorsdelight.registry.component.SDContainer;
import com.vomiter.survivorsdelight.registry.component.SDContainerStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SDDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SurvivorsDelight.MODID);

    public static final DeferredHolder<DataComponentType<?> ,DataComponentType<SDContainer>> FOOD_CONTAINER =
            DATA_COMPONENT_TYPES.register("food_container", () ->
                    DataComponentType.<SDContainer>builder()
                            .persistent(SDContainer.CODEC)              // JSON/存檔
                            .networkSynchronized(SDContainer.STREAM_CODEC) // 封包同步
                            .cacheEncoding()
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SDContainerStack>> FOOD_CONTAINER_STACK =
            DATA_COMPONENT_TYPES.register("food_container_stack", () ->
                    DataComponentType.<SDContainerStack>builder()
                            .persistent(SDContainerStack.CODEC)
                            .networkSynchronized(SDContainerStack.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MedleyContent>> MEDLEY_CONTENT =
            DATA_COMPONENT_TYPES.register("medley_content", () ->
                    DataComponentType.<MedleyContent>builder()
                            .persistent(MedleyContent.CODEC)
                            .networkSynchronized(MedleyContent.STREAM_CODEC)
                            .build()
            );
}
