package com.snow.morejobs.gui.bartender;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class BartenderShopMenuProvider {

    private static final ITextComponent TITLE = new StringTextComponent("🍷 Barman - Boutique");

    public static void open(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        NetworkHooks.openGui(serverPlayer, new SimpleNamedContainerProvider(
                (id, inv, p) -> new BartenderShopContainer(id, p),
                TITLE
        ));
    }
}
