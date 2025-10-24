package com.vomiter.survivorsdelight.data.tags;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletItems;
import com.vomiter.survivorsdelight.core.registry.skillet.SDSkilletPartItems;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ModTags;

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
        addSkilletTags();
        assert TFCItems.GLUE.getKey() != null;
        tag(SDTags.ItemTags.FOOD_MODEL_COATING).add(TFCItems.GLUE.getKey());
        tag(SDTags.ItemTags.WOOD_PRESERVATIVES).addOptional(RLUtils.build("firmalife", "beeswax"));
        tag(SDTags.ItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER).getKey()));
        tag(SDTags.ItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_SILVER).getKey()));
        tag(SDTags.ItemTags.RETURN_COPPER_SKILLET).add(Objects.requireNonNull(SDSkilletItems.SKILLETS.get(SkilletMaterial.COPPER_TIN).getKey()));
        tag(TFCTags.Items.DOG_FOOD).add(ModItems.DOG_FOOD.get());
        tag(TFCTags.Items.HORSE_FOOD).add(ModItems.HORSE_FEED.get());
        tag(TFCTags.Items.USABLE_ON_TOOL_RACK).addTag(SDTags.ItemTags.SKILLETS);

        tag(ModTags.SERVING_CONTAINERS).addOptionalTag(RLUtils.build("tfc", "glass_bottles"));
        tag(ModTags.SERVING_CONTAINERS).add(TFCBlocks.CERAMIC_BOWL.get().asItem());
    }

    private void addSkilletTags(){
        for (SkilletMaterial m : SkilletMaterial.values()){
            var skillet = SDSkilletItems.getKey(m);
            var head = SDSkilletPartItems.HEADS.get(m);
            var uf = SDSkilletPartItems.UNFINISHED.get(m);

            tag(SDTags.ItemTags.SKILLETS).add(skillet);
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
                    tag(SDTags.ItemTags.SKILLET_HEADS).add(head.getKey());
                }
                if(uf != null) {
                    assert uf.getKey() != null;
                    metal_tag.add(uf.getKey());
                    tag(SDTags.ItemTags.UNFINISHED_SKILLETS).add(uf.getKey());
                }
            }
        }

    }

}
