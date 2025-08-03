package com.snow.morejobs.handler;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ChorusEatTracker {

    private static final Map<UUID, Integer> chorusEaten = new HashMap<>();

    @SubscribeEvent
    public static void onEatChorus(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        if (event.getItem().getItem() != Items.CHORUS_FRUIT) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        UUID uuid = player.getUUID();

        int eaten = chorusEaten.getOrDefault(uuid, 0) + 1;
        chorusEaten.put(uuid, eaten);

        if (eaten == 100) {
            JobDataStorage data = JobDataStorage.get(player);
            if (!data.hasJob(JobType.ALIEN)) {
                data.addJob(JobType.ALIEN);
                data.save();
                player.sendMessage(new StringTextComponent("👽 Nouveau métier secret débloqué : §e" + JobType.ALIEN.getDisplayName()), uuid);
            }
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        UUID oldId = event.getOriginal().getUUID();
        UUID newId = event.getPlayer().getUUID();
        int count = chorusEaten.getOrDefault(oldId, 0);
        chorusEaten.put(newId, count);
    }
}
