package com.vomiter.survivorsdelight.data;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import com.vomiter.survivorsdelight.data.tags.SDItemTags;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               ModBlockTagsProvider blockTags,
                               ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags.contentsGetter(), SurvivorsDelight.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        assert TFCItems.GLUE.getKey() != null;
        tag(SDItemTags.FOOD_MODEL_COATING).add(TFCItems.GLUE.getKey());
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getKey()));
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_SILVER).getKey()));
        tag(SDItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_TIN).getKey()));
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
                    assert head.getKey() != null;
                    copper_tag.add(head.getKey());
                }
                if(uf != null) {
                    assert uf.getKey() != null;
                    copper_tag.add(uf.getKey());
                }
            }
            else{
                var metal_tag = tag(TagKey.create(
                        ResourceKey.createRegistryKey(RLUtils.build("minecraft", "item")),
                        RLUtils.build("tfc", "metal_item/" + m.material)))
                        .add(skillet);
                if(head != null) {
                    assert head.getKey() != null;
                    metal_tag.add(head.getKey());
                    tag(SDItemTags.SKILLET_HEADS).add(head.getKey());
                }
                if(uf != null) {
                    assert uf.getKey() != null;
                    metal_tag.add(uf.getKey());
                    tag(SDItemTags.UNFINISHED_SKILLETS).add(uf.getKey());
                }
            }
        }
        tag(TFCTags.Items.DOG_FOOD).add(ModItems.DOG_FOOD.get());
    }

}
