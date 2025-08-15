package com.snow.morejobs.handler;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = MoreJobsMod.MODID)
public class RitualKillTracker {

    private static final Map<UUID, RitualProgress> playerRituals = new HashMap<>();
    private static final long TIME_LIMIT_MS = 60_000; // 60 secondes

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
        UUID uuid = player.getUUID();
        RitualProgress progress = playerRituals.getOrDefault(uuid, new RitualProgress());

        String mobType = getMobType((LivingEntity) event.getEntity());
        if (mobType == null) return;

        long now = System.currentTimeMillis();

        // Réinitialiser si le temps est dépassé
        if (now - progress.startTime > TIME_LIMIT_MS) {
            progress = new RitualProgress();
        }

        progress.killedTypes.add(mobType);
        progress.startTime = progress.startTime == 0 ? now : progress.startTime;
        playerRituals.put(uuid, progress);

        if (progress.killedTypes.containsAll(RitualProgress.REQUIRED_TYPES)) {
            JobDataStorage data = JobDataStorage.get(player);
            if (!data.hasJob(JobType.FATETELLER)) {
                data.addJob(JobType.FATETELLER);
                data.save();
                player.sendMessage(new StringTextComponent("§5Les astres se sont alignés... Nouveau métier débloqué : §d" + JobType.FATETELLER.getDisplayName()), uuid);
                playerRituals.remove(uuid);
            }
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        UUID oldId = event.getOriginal().getUUID();
        UUID newId = event.getPlayer().getUUID();
        RitualProgress oldProgress = playerRituals.get(oldId);
        if (oldProgress != null) {
            playerRituals.put(newId, oldProgress);
        }
    }

    private static String getMobType(LivingEntity entity) {
        if (entity instanceof SquidEntity) return "squid";
        if (entity instanceof RabbitEntity) return "rabbit";
        if (entity instanceof BatEntity) return "bat";
        return null;
    }

    private static class RitualProgress {
        static final Set<String> REQUIRED_TYPES;
        static {
            Set<String> set = new HashSet<>();
            set.add("squid");
            set.add("rabbit");
            set.add("bat");
            REQUIRED_TYPES = Collections.unmodifiableSet(set);
        }

        Set<String> killedTypes = new HashSet<>();
        long startTime = 0;
    }
}
