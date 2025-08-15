package com.snow.morejobs.gui.checkup;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, "morejobs");

    public static final RegistryObject<ContainerType<CheckupContainer>> CHECKUP =
            CONTAINERS.register("checkup",
                    () -> new ContainerType<>(
                            (IContainerFactory<CheckupContainer>) CheckupContainer::new // Utilise le constructeur (windowId, inv, buf)
                    )
            );

    /** Appel côté mod main */
    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }

    /** Variante simplifiée si tu veux juste l’appeler sans argument */
    public static void register() {
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
