package com.snow.morejobs.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class CloneUtils {

    public static void spawnClone(ServerPlayerEntity target) {
        ServerWorld world = (ServerWorld) target.level;
        MinecraftServer server = target.getServer();
        if (server == null) return;

        // Créer un faux GameProfile avec même UUID (ou un random) et skin
        GameProfile originalProfile = target.getGameProfile();
        UUID cloneUUID = UUID.randomUUID();
        GameProfile cloneProfile = new GameProfile(cloneUUID, originalProfile.getName());

        // Copier les propriétés de skin du joueur (important pour l’apparence)
        cloneProfile.getProperties().putAll(originalProfile.getProperties());

        // Créer un faux joueur (OtherPlayerEntity)
        FakePlayerEntity clone = new FakePlayerEntity(world, cloneProfile);
        clone.setPos(target.getX(), target.getY(), target.getZ());
        clone.setCustomName(new StringTextComponent("Clone de " + target.getName().getString()));
        clone.setCustomNameVisible(true);
        clone.setInvulnerable(false);
        clone.setSilent(true);

        world.addFreshEntity(clone);

        // Supprimer automatiquement le clone après 30 minutes (1800000 ms)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (clone.isAlive()) {
                    clone.remove();
                }
            }
        }, 30 * 60 * 1000);
    }
}
