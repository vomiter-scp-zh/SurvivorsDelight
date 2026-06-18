package com.vomiter.survivorsdelight.registry.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MedleyContent(List<ItemStack> stacks) {
    public MedleyContent {
        stacks = List.copyOf(stacks.stream()
                .map(ItemStack::copy)
                .toList());
    }

    public static final Codec<MedleyContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(MedleyContent::stacks)
    ).apply(instance, MedleyContent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MedleyContent> STREAM_CODEC =
            ItemStack.OPTIONAL_STREAM_CODEC
                    .apply(ByteBufCodecs.list())
                    .map(
                            MedleyContent::new,
                            MedleyContent::stacks
                    );

    @Override
    public List<ItemStack> stacks() {
        return stacks.stream()
                .map(ItemStack::copy)
                .toList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MedleyContent other)) return false;
        if (this.stacks.size() != other.stacks.size()) return false;

        for (int i = 0; i < this.stacks.size(); i++) {
            if (!ItemStack.matches(this.stacks.get(i), other.stacks.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (ItemStack stack : stacks) {
            result = 31 * result + ItemStack.hashItemAndComponents(stack);
        }
        return result;
    }
}