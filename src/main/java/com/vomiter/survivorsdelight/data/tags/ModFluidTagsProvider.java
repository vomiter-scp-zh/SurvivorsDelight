package com.vomiter.survivorsdelight.data.tags;

import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.vomiter.survivorsdelight.SurvivorsDelight;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput out,
                                CompletableFuture<HolderLookup.Provider> lookup,
                                @Nullable ExistingFileHelper efh) {
        super(out, lookup, SurvivorsDelight.MODID, efh);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(SDTags.FluidTags.COOKING_OILS)
                .add(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.OLIVE_OIL).getSource())
                .addOptional(FLFluids.EXTRA_FLUIDS.get(ExtraFluid.SOYBEAN_OIL).source().getId());
    }
}
