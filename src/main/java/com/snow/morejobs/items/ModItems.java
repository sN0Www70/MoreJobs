package com.snow.morejobs.items;

import com.snow.morejobs.MoreJobsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModItems {

    // Register sur le bon namespace
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MoreJobsMod.MODID);

    // Menottes : 1 par pile, onglet OUTILS
    public static final RegistryObject<Item> HANDCUFF =
            ITEMS.register("handcuff",
                    () -> new HandcuffItem(new Item.Properties()
                            .stacksTo(1)
                            .tab(ItemGroup.TAB_TOOLS)));

    // à appeler depuis le constructeur de ton mod (tu le fais déjà)
    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
