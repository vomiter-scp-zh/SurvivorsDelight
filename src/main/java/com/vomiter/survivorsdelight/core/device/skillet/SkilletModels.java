package com.vomiter.survivorsdelight.core.device.skillet;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.util.RLUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.client.model.SkilletModel;

import java.util.Map;

@Mod.EventBusSubscriber(modid = SurvivorsDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SkilletModels {
    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        for (SkilletMaterial m : SkilletMaterial.values()){
            event.register(new ModelResourceLocation(RLUtils.build(SurvivorsDelight.MODID, "skillet/" + m.material +"_cooking"), "inventory"));
        }
        event.register(new ModelResourceLocation(RLUtils.build(SurvivorsDelight.MODID, "skillet/" + "farmer" +"_cooking"), "inventory"));
    }

    public static void makeModel(String name, ModelEvent.ModifyBakingResult event){
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelResourceLocation skilletLocation = new ModelResourceLocation(RLUtils.build(SurvivorsDelight.MODID, "skillet/" + name), "inventory");
        BakedModel skilletModel = modelRegistry.get(skilletLocation);
        ModelResourceLocation skilletCookingLocation = new ModelResourceLocation(RLUtils.build(SurvivorsDelight.MODID, "skillet/" + name +"_cooking"), "inventory");
        BakedModel skilletCookingModel = modelRegistry.get(skilletCookingLocation);
        modelRegistry.put(skilletLocation, new SkilletModel(event.getModelBakery(), skilletModel, skilletCookingModel));
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        for (SkilletMaterial m : SkilletMaterial.values()){
            makeModel(m.material, event);
        }
        makeModel("farmer", event);
    }
}
