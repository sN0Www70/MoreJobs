package com.snow.morejobs.util;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobUnlockFunctionHandler {

    private static final Map<UUID, Integer> chorusEatenMap = new HashMap<>();

    public static void handle(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int chorusEaten = chorusEatenMap.getOrDefault(uuid, 0);

        // Check if player just ate a chorus fruit
        if (player.getFoodData().getFoodLevel() > 0 && player.getMainHandItem().getItem().getRegistryName().getPath().contains("chorus_fruit")) {
            chorusEaten++;
            chorusEatenMap.put(uuid, chorusEaten);

            if (chorusEaten >= 100) {
                JobDataStorage data = JobDataStorage.get(player);
                JobType alien = JobType.ALIEN;

                if (!data.getActiveJobs().contains(alien.getName())) {
                    data.addJob(alien);
                    player.sendMessage(new StringTextComponent("👽 Nouveau métier secret débloqué : §a" + alien.getDisplayName()), uuid);
                }
            }
        }
    }
}
