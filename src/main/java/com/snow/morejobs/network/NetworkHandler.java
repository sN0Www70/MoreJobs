package com.snow.morejobs.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    public static final String PROTOCOL = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("morejobs", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, CooldownSyncPacket.class,
                CooldownSyncPacket::encode,
                CooldownSyncPacket::decode,
                CooldownSyncPacket::handle);
    }
}
