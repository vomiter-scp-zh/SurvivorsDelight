package com.vomiter.survivorsdelight.data;

import net.dries007.tfc.common.entities.TFCEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput p_256095_, CompletableFuture<HolderLookup.Provider> p_256572_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256095_, p_256572_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.DOG_FOOD_USERS).add(TFCEntities.DOG.get());
    }

}
