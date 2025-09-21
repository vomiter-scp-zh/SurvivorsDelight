package com.vomiter.survivorsdelight.core.device.skillet.data;

import com.vomiter.survivorsdelight.core.device.skillet.SkilletMaterial;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class SDSkilletBlockModelProvider extends BlockModelProvider {
    public SDSkilletBlockModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, "survivorsdelight", efh);
    }

    @Override
    protected void registerModels() {
        for (SkilletMaterial m : SkilletMaterial.values()){
            ModelBuilder<BlockModelBuilder> builder = withExistingParent(
                    "block/skillet/" + m.material,
                    modLoc("block/skillet/skillet") // parent: survivorsdelight:block/skillet/skillet
            );
            m.textures.forEach((key, rl) -> {
                existingFileHelper.trackGenerated(rl, PackType.CLIENT_RESOURCES, ".png", "textures");
                builder.texture(key, rl);
                if(Objects.equals(key, "0")){
                    builder.texture("particle", rl);
                }
            });
        }
    }
    private void genSkilletModel(String name) {
        withExistingParent(
                "block/skillet/" + name,
                modLoc("block/skillet/skillet") // parent: survivorsdelight:block/skillet/skillet
        );
    }
}
