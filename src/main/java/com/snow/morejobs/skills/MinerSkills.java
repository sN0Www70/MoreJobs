package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.EconomyUtils;
import com.snow.morejobs.util.EntityUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class MinerSkills {

    private static final long FOCUS_DURATION_TICKS = 20L * 60 * 3;
    private static final String ECHO_KEY = "miner_echolocate";

    public static void minerFocus(ServerPlayerEntity player) {
        player.addEffect(new EffectInstance(Effects.DIG_SPEED, (int) FOCUS_DURATION_TICKS, 1, false, true));
        player.addEffect(new EffectInstance(Effects.INVISIBILITY, (int) FOCUS_DURATION_TICKS, 0, false, true));
        player.sendMessage(new StringTextComponent("⛏️ Miner Focus activé : haste II + invisibilité"), player.getUUID());

        JobDataStorage data = JobDataStorage.get(player);
        data.addXp(JobType.MINER, 15);
        data.save();

        EconomyUtils.giveMoney(player, 15);
        player.sendMessage(new StringTextComponent("✅ +15 XP & Chelous"), player.getUUID());
    }

    public static void toggleEchoLocate(ServerPlayerEntity player, boolean enable) {
        EntityUtils.toggleFlag(player.getUUID(), ECHO_KEY, enable);
        String msg = enable
                ? "EchoLocate activé ! Night Vision en cave."
                : "EchoLocate désactivé.";
        player.sendMessage(new StringTextComponent(msg), player.getUUID());
        if (!enable) player.removeEffect(Effects.NIGHT_VISION);
    }

    public static boolean isEchoLocateActive(UUID playerId) {
        return EntityUtils.getFlag(playerId, ECHO_KEY);
    }

    public static void locateOre(ServerPlayerEntity player) {
        BlockPos found = EntityUtils.findNearestOre((ServerWorld) player.level, player.blockPosition(), 64);
        if (found != null) {
            Vector3d dir = Vector3d.atLowerCornerOf(found.subtract(player.blockPosition()));
            player.sendMessage(new StringTextComponent(
                    String.format("Miner : minerai détecté direction (%.2f, %.2f, %.2f)",
                            dir.x, dir.y, dir.z)), player.getUUID());

            JobDataStorage data = JobDataStorage.get(player);
            data.addXp(JobType.MINER, 10);
            data.save();

            EconomyUtils.giveMoney(player, 10);
            player.sendMessage(new StringTextComponent("✅ +10 XP & Chelous"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("❌ Aucun minerai proche."), player.getUUID());
        }
    }
}
