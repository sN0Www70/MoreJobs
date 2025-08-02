package com.snow.morejobs;

import com.snow.morejobs.gui.architect.ArchitectShopContainer;
import com.snow.morejobs.gui.architect.client.ArchitectShopScreen;
import com.snow.morejobs.gui.bartender.BartenderShopContainer;
import com.snow.morejobs.gui.bartender.client.BartenderShopScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "morejobs", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ScreenManager.register(ArchitectShopContainer.TYPE, ArchitectShopScreen::new);
        ScreenManager.register(BartenderShopContainer.TYPE, BartenderShopScreen::new);
    }
}
