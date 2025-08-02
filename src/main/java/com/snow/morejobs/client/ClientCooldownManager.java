package com.snow.morejobs.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientCooldownManager {

    private static final Map<String, Long> cooldowns = new HashMap<>();
    private static final Map<String, Long> durations = new HashMap<>();

    public static void updateCooldowns(Map<String, Long> newCooldowns, Map<String, Long> newDurations) {
        cooldowns.clear();
        durations.clear();
        cooldowns.putAll(newCooldowns);
        durations.putAll(newDurations);
    }

    public static Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    public static long getDuration(String skill) {
        return durations.getOrDefault(skill, 1000L);
    }
}
