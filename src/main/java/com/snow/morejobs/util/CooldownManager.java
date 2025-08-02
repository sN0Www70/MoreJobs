package com.snow.morejobs.util;

import com.snow.morejobs.network.CooldownSyncPacket;
import com.snow.morejobs.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;

public class CooldownManager {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> durations = new HashMap<>();

    public static boolean isOnCooldown(PlayerEntity player, String skill) {
        long currentTime = System.currentTimeMillis();
        return cooldowns
                .getOrDefault(player.getUUID(), Collections.emptyMap())
                .getOrDefault(skill, 0L) > currentTime;
    }

    public static void setCooldown(PlayerEntity player, String skill, long durationMillis) {
        UUID id = player.getUUID();
        long endTime = System.currentTimeMillis() + durationMillis;

        cooldowns.computeIfAbsent(id, k -> new HashMap<>()).put(skill, endTime);
        durations.computeIfAbsent(id, k -> new HashMap<>()).put(skill, durationMillis);

        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Map<String, Long> cd = cooldowns.get(id);
            Map<String, Long> dur = durations.get(id);

            NetworkHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new CooldownSyncPacket(cd, dur)
            );
        }
    }

    public static long getRemaining(PlayerEntity player, String skill) {
        long remaining = cooldowns
                .getOrDefault(player.getUUID(), Collections.emptyMap())
                .getOrDefault(skill, 0L) - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }

    public static Map<String, Long> getCooldowns(UUID playerId) {
        return cooldowns.getOrDefault(playerId, Collections.emptyMap());
    }

    public static long getDuration(UUID playerId, String skill) {
        return durations.getOrDefault(playerId, Collections.emptyMap())
                .getOrDefault(skill, 1000L); // fallback 1s
    }
}
