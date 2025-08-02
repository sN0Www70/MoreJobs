package com.snow.morejobs.util;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class SalaryTicker {

    private static final Map<UUID, Integer> tickMap = new HashMap<>();
    private static final int PAY_INTERVAL_TICKS = 20 * 60 * 20;

    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID uuid = player.getUUID();
        tickMap.put(uuid, tickMap.getOrDefault(uuid, 0) + 1);

        if (tickMap.get(uuid) >= PAY_INTERVAL_TICKS) {
            tickMap.put(uuid, 0);

            JobDataStorage data = JobDataStorage.get(player);
            for (String jobName : data.getActiveJobs()) {
                JobType job = JobType.fromName(jobName);
                if (job != JobType.NONE) {
                    EconomyUtils.giveMoney(player, 1);
                }
            }
        }
    }
}
