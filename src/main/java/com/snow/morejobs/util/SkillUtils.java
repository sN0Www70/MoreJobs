package com.snow.morejobs.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

public class SkillUtils {

    public static boolean tryUseSkill(PlayerEntity player, String skillId, int cooldownMs) {
        if (CooldownManager.isOnCooldown(player, skillId)) {
            long remaining = CooldownManager.getRemaining(player, skillId) / 1000;
            player.sendMessage(new StringTextComponent("⏳ " + skillId + " — encore " + formatTime(remaining)), player.getUUID());
            return false;
        }

        CooldownManager.setCooldown(player, skillId, cooldownMs);
        player.sendMessage(new StringTextComponent("🟢 " + skillId + " utilisé — recharge en " + formatTime(cooldownMs / 1000)), player.getUUID());
        return true;
    }

    public static void scheduleNotify(PlayerEntity player, String skillId, int delayMs) {
        int ticks = delayMs / 50;
        CooldownNotifier.schedule(player, skillId, ticks);
    }


    private static String formatTime(long seconds) {
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%d:%02ds", m, s);
    }
}
