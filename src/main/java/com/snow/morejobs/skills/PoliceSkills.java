package com.snow.morejobs.skills;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class PoliceSkills {

    private static final Set<UUID> frozenPlayers = new HashSet<>();
    private static final Map<UUID, UUID> escortMap = new HashMap<>(); // escorted -> police

    public static boolean isFrozen(ServerPlayerEntity player) {
        return frozenPlayers.contains(player.getUUID());
    }

    public static void toggleFreeze(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        if (frozenPlayers.contains(id)) {
            frozenPlayers.remove(id);
            player.sendMessage(new StringTextComponent("❄️ Tu n'es plus gelé."), id);
        } else {
            frozenPlayers.add(id);
            player.sendMessage(new StringTextComponent("🚫 Tu as été gelé par un policier."), id);
        }
    }

    public static void toggleEscort(ServerPlayerEntity police, ServerPlayerEntity target) {
        UUID id = target.getUUID();

        if (escortMap.containsKey(id)) {
            escortMap.remove(id);
            target.sendMessage(new StringTextComponent("⛓️ Tu n'es plus escorté."), id);
            police.sendMessage(new StringTextComponent("⛓️ Escort terminé."), police.getUUID());
        } else {
            escortMap.put(id, police.getUUID());
            target.sendMessage(new StringTextComponent("👮 Tu es escorté par la police."), id);
            police.sendMessage(new StringTextComponent("👣 Escort démarré."), police.getUUID());
        }
    }

    public static void tick(ServerPlayerEntity player) {
        UUID playerId = player.getUUID();

        // Immobilise si gelé
        if (frozenPlayers.contains(playerId)) {
            player.teleportTo(player.getX(), player.getY(), player.getZ());
        }

        // Suit le policier s'il est escorté
        if (escortMap.containsKey(playerId)) {
            UUID policeId = escortMap.get(playerId);
            ServerPlayerEntity police = player.getServer().getPlayerList().getPlayer(policeId);
            if (police != null) {
                double x = police.getX();
                double y = police.getY();
                double z = police.getZ();
                player.teleportTo(x, y, z);
            }
        }
    }
}
