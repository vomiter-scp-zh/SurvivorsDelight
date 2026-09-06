package com.vomiter.survivorsdelight.adapter.stove;

import net.dries007.tfc.common.recipes.HeatingRecipe;

public interface IStoveBlockEntity {
    int sdtfc$getLeftBurnTick();
    void sdtfc$setLeftBurnTick(int v);
    default void sdtfc$addLeftBurnTick(int v){
        sdtfc$setLeftBurnTick(sdtfc$getLeftBurnTick() + v);
    }
    default void sdtfc$reduceLeftBurnTick(int v){
        sdtfc$setLeftBurnTick(sdtfc$getLeftBurnTick() - v);
        if(sdtfc$getLeftBurnTick() < 0) sdtfc$setLeftBurnTick(0);
    }
    HeatingRecipe[] sdtfc$getCachedRecipes();

    static float sdtfc$getStaticTemperature(){
        return 550;
    }
    static int sdtfc$getMaxDuration(){ return 7 * 20 * 60 * 20;}


}
