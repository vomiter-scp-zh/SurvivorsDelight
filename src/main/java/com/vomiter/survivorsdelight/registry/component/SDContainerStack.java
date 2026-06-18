package com.vomiter.survivorsdelight.registry.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SDContainerStack(ItemStack stack) {
    public SDContainerStack {
        stack = stack.copy();
    }
    public static final Codec<SDContainerStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(SDContainerStack::stack)
    ).apply(instance, SDContainerStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SDContainerStack> STREAM_CODEC =
            ItemStack.OPTIONAL_STREAM_CODEC.map(
                    SDContainerStack::new,
                    SDContainerStack::stack
            );

    @Override
    public ItemStack stack() {
        return stack.copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SDContainerStack other)) return false;
        return ItemStack.matches(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(this.stack);
    }
}