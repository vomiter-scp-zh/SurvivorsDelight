package com.vomiter.survivorsdelight.registry;

import com.vomiter.survivorsdelight.SurvivorsDelight;
import com.vomiter.survivorsdelight.registry.recipe.NutrientShapedRecipe;
import com.vomiter.survivorsdelight.registry.recipe.NutrientShapelessRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class SDRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SurvivorsDelight.MODID);

    public static final RegistryObject<RecipeSerializer<NutrientShapedRecipe>> NUTRITION_CRAFTING =
            SERIALIZERS.register("nutrition_crafting", NutrientShapedRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<NutrientShapelessRecipe>> NUTRITION_CRAFTING_SHAPELESS =
            SERIALIZERS.register("nutrition_crafting_shapeless", NutrientShapelessRecipe.Serializer::new);


    private SDRecipeSerializers() {}
}
