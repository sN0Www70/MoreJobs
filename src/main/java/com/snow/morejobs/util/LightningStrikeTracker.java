package com.snow.morejobs.handler;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreJobsMod.MODID)
public class LightningStrikeTracker {

    private static final Map<UUID, Integer> lightningHits = new HashMap<>();

    @SubscribeEvent
    public static void onLightningHit(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof ServerPlayerEntity)) return;
        if (event.getSource() != DamageSource.LIGHTNING_BOLT) return;

        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        UUID uuid = player.getUUID();

        int strikes = lightningHits.getOrDefault(uuid, 0) + 1;
        lightningHits.put(uuid, strikes);

        if (strikes == 10) {
            JobDataStorage data = JobDataStorage.get(player);
            if (!data.hasJob(JobType.MAD_SCIENTIST)) {
                data.addJob(JobType.MAD_SCIENTIST);
                data.save();
                player.sendMessage(new StringTextComponent("Tu es frappé d'une idée géniale... §e" + JobType.MAD_SCIENTIST.getDisplayName()), uuid);
            }
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        UUID oldId = event.getOriginal().getUUID();
        UUID newId = event.getPlayer().getUUID();
        int count = lightningHits.getOrDefault(oldId, 0);
        lightningHits.put(newId, count);
    }
}
