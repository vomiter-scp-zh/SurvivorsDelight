package com.vomiter.survivorsdelight.core.registry;

import com.vomiter.survivorsdelight.core.container.SDCabinetMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.vomiter.survivorsdelight.SurvivorsDelight.MODID;

public class SDContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);

    public static final RegistryObject<MenuType<SDCabinetMenu>> CABINET =
            CONTAINERS.register("cabinet", () -> IForgeMenuType.create(SDCabinetMenu::new));
}
