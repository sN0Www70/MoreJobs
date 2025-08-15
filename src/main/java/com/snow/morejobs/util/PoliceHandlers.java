package com.snow.morejobs.util;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.*;

public class PoliceHandlers {

    private static final Set<UUID> frozen = new HashSet<>();
    private static final Map<UUID, UUID> escort = new HashMap<>(); // target -> police

    // --- Freeze ---
    public static boolean isFrozen(ServerPlayerEntity player) {
        return frozen.contains(player.getUUID());
    }

    public static void setFrozen(ServerPlayerEntity player, boolean state) {
        if (state) frozen.add(player.getUUID());
        else frozen.remove(player.getUUID());
    }

    // --- Escort ---
    public static void startEscort(ServerPlayerEntity cop, ServerPlayerEntity target) {
        escort.put(target.getUUID(), cop.getUUID());
    }

    public static void stopEscort(ServerPlayerEntity target) {
        escort.remove(target.getUUID());
    }

    public static boolean isEscorted(ServerPlayerEntity target) {
        return escort.containsKey(target.getUUID());
    }

    public static UUID getEscorter(ServerPlayerEntity target) {
        return escort.get(target.getUUID());
    }

    // --- Tick logique ---
    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        // Empêcher les déplacements (le "ramener" à la même position)
        if (frozen.contains(id)) {
            player.teleportTo(player.getX(), player.getY(), player.getZ());
        }

        // Suivre le policier
        if (escort.containsKey(id)) {
            UUID copId = escort.get(id);
            ServerPlayerEntity cop = player.getServer().getPlayerList().getPlayer(copId);
            if (cop != null) {
                player.teleportTo(cop.getX(), cop.getY(), cop.getZ());
            }
        }
    }
}
