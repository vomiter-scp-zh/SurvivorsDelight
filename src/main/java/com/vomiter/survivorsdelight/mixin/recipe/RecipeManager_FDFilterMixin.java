package com.vomiter.survivorsdelight.mixin.recipe;

import com.google.gson.JsonElement;
import com.vomiter.survivorsdelight.data.recipe.FDRecipeBlocker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManager_FDFilterMixin {

    // 原本是 private final，要標 @Mutable 才能重新指定
    @Shadow
    @Mutable
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Shadow
    @Mutable
    private Map<ResourceLocation, Recipe<?>> byName;

    @Inject(method = "apply*", at = @At("TAIL"))
    private void sd$filterFdFoodRecipes(Map<ResourceLocation, JsonElement> json,
                                        ResourceManager resourceManager,
                                        ProfilerFiller profiler,
                                        CallbackInfo ci) {

        // 1. 拿到真正的 registryAccess（你前面提到的需求）
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        RegistryAccess access = server != null ? server.registryAccess() : RegistryAccess.EMPTY;

        // 2. 先做「外層」的可變拷貝
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes = new HashMap<>();
        // 這個也要做可變拷貝
        Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>(this.byName);

        for (Map.Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : this.recipes.entrySet()) {
            RecipeType<?> type = entry.getKey();
            Map<ResourceLocation, Recipe<?>> originalById = entry.getValue();

            // 先把裡層也複製成可變的
            Map<ResourceLocation, Recipe<?>> filteredById = new HashMap<>(originalById);

            // 只處理你要的三類，其他原樣塞回去
            if (type == RecipeType.CRAFTING
                    || type == ModRecipeTypes.CUTTING.get()
                    || type == ModRecipeTypes.COOKING.get()) {

                Iterator<Map.Entry<ResourceLocation, Recipe<?>>> it = filteredById.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<ResourceLocation, Recipe<?>> e = it.next();
                    ResourceLocation id = e.getKey();
                    Recipe<?> recipe = e.getValue();

                    if (FDRecipeBlocker.shouldBlock(id, recipe, access)) {
                        // 從這一個 type 底下移掉
                        it.remove();
                        // 同時也要從全域 byName 移掉
                        newByName.remove(id);
                    }
                }
            }

            // 把處理好的這一個 type 塞回新的 map
            newRecipes.put(type, filteredById);
        }

        // 3. 最後再指回去（這裡你要不要再包成 unmodifiable 看你要不要模仿 vanilla）
        this.recipes = Map.copyOf(newRecipes);
        this.byName = Map.copyOf(newByName);
    }
}
