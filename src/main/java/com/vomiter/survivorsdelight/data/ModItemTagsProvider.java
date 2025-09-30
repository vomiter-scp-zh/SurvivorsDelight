package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.SDSkilletPartItems;
import com.vomiter.survivorsdelight.data.tags.SDItemTags;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags, SurvivorsDelight.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(SDItemTags.FOOD_MODEL_COATING).add(TFCItems.GLUE.key());
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).get()));
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_SILVER).get()));
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_TIN).get()));
        for (SkilletMaterial m : SkilletMaterial.values()){
            var skillet = SDSkilletItems.getKey(m);
            var head = SDSkilletPartItems.HEADS.get(m);
            var uf = SDSkilletPartItems.UNFINISHED.get(m);

            tag(SDItemTags.SKILLETS).add(skillet);
            if(m.isWeapon){
                tag(TFCTags.Items.DEALS_CRUSHING_DAMAGE).add(skillet);
            }
            if(m.material.contains("copper")){
                var copper_tag = tag(TagKey.create(
                        ResourceKey.createRegistryKey(RLUtils.build("minecraft", "item")),
                        RLUtils.build("tfc", "metal_item/copper"))
                ).add(skillet);
                if(head != null) {
                    copper_tag.add(head.getKey());
                }
                if(uf != null) {
                    copper_tag.add(uf.getKey());
                }
            }
            else{
                var metal_tag = tag(TagKey.create(
                        ResourceKey.createRegistryKey(RLUtils.build("minecraft", "item")),
                        RLUtils.build("tfc", "metal_item/" + m.material)))
                        .add(skillet);
                if(head != null) {
                    metal_tag.add(head.getKey());
                }
                if(uf != null) {
                    metal_tag.add(uf.getKey());
                }
            }
        }
        tag(TFCTags.Items.DOG_FOOD).add(ModItems.DOG_FOOD.get());
    }

}
