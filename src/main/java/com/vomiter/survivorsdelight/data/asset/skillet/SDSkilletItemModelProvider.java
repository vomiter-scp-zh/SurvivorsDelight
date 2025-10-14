package com.vomiter.survivorsdelight.data.asset.skillet;

import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class SDSkilletItemModelProvider extends ItemModelProvider {
    public SDSkilletItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, "survivorsdelight", efh);
    }

    @Override
    protected void registerModels() {
        for (SkilletMaterial m : SkilletMaterial.values()){
            genSkilletModel(m);
        }
    }

    private void genSkilletModel(SkilletMaterial m) {
        String name = m.material;
        var skillet = withExistingParent(
                "item/skillet/" + name,
                modLoc("item/skillet/skillet")// parent: survivorsdelight:item/skillet/skillet
        );

        var cooking = withExistingParent(
                "item/skillet/" + name + "_cooking",
                modLoc("item/skillet/cooking")// parent: survivorsdelight:item/skillet/cooking
        );

        var head = withExistingParent(
                "item/skillet_head/" + name,
                modLoc("item/skillet/skillet_head")
        );

        addTextures(m, skillet);
        addTextures(m, cooking);
        addTextures(m, head);

        var uf = withExistingParent(
                "item/unfinished_skillet/" + name,
                modLoc("item/skillet/" + name)
        );
        addTextures(m, uf);
        existingFileHelper.trackGenerated(RLUtils.build("tfc", "block/empty"), PackType.CLIENT_RESOURCES, ".png", "textures");
        uf.texture("2", m.textures.get("0"));
    }

    private void addTextures(SkilletMaterial m, ModelBuilder<ItemModelBuilder> builder){
        m.textures.forEach((key, rl) -> {
            existingFileHelper.trackGenerated(rl, PackType.CLIENT_RESOURCES, ".png", "textures");
            builder.texture(key, rl);
            if(Objects.equals(key, "0")){
                builder.texture("particle", rl);
            }
        });
    }
}
