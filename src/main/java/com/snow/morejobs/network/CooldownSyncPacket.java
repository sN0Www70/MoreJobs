package com.snow.morejobs.network;

import com.snow.morejobs.client.ClientCooldownManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CooldownSyncPacket {
    private final Map<String, Long> cooldowns;
    private final Map<String, Long> durations;

    public CooldownSyncPacket(Map<String, Long> cooldowns, Map<String, Long> durations) {
        this.cooldowns = cooldowns;
        this.durations = durations;
    }

    public static void encode(CooldownSyncPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.cooldowns.size());
        msg.cooldowns.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeLong(value);
        });

        buf.writeVarInt(msg.durations.size());
        msg.durations.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeLong(value);
        });
    }

    public static CooldownSyncPacket decode(PacketBuffer buf) {
        Map<String, Long> cds = new HashMap<>();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            cds.put(buf.readUtf(32767), buf.readLong());
        }

        Map<String, Long> durs = new HashMap<>();
        int dcount = buf.readVarInt();
        for (int i = 0; i < dcount; i++) {
            durs.put(buf.readUtf(32767), buf.readLong());
        }

        return new CooldownSyncPacket(cds, durs);
    }

    public static void handle(CooldownSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientCooldownManager.updateCooldowns(msg.cooldowns, msg.durations);
        });
        ctx.get().setPacketHandled(true);
    }
}
