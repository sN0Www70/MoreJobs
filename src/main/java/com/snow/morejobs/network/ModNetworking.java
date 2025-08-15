package com.snow.morejobs.network;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.network.packets.InterestRatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MoreJobsMod.MODID, "bank_system"), // Different name
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(InterestRatePacket.class, id())
                .decoder(InterestRatePacket::new)
                .encoder(InterestRatePacket::encode)
                .consumer(InterestRatePacket::handle)
                .add();
    }
}