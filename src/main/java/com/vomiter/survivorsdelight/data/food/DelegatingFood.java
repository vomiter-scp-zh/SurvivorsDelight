package com.vomiter.survivorsdelight.data.food;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import org.jetbrains.annotations.NotNull;

public final class DelegatingFood extends FoodHandler.Dynamic{
    private final FoodHandler.Dynamic base;
    private final FoodData fallback;

    public DelegatingFood(@NotNull FoodHandler.Dynamic base, @NotNull FoodData fallback) {
        this.base = base;
        this.fallback = fallback;
    }

    @Override
    public @NotNull FoodData getData() {
        final FoodData d = base.getData();
        return (d.hunger() == 0) ? fallback : d;
    }

}
