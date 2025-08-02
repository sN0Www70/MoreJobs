package com.snow.morejobs.util;

import com.snow.morejobs.skills.MinerSkills;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MinerTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END) return;
        PlayerEntity player = ev.player;
        if (player.level.isClientSide) return;

        if (MinerSkills.isEchoLocateActive(player.getUUID()) && player.getY() < 60) {
            EffectInstance current = player.getEffect(Effects.NIGHT_VISION);
            if (current == null || current.getDuration() < 100) {
                player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, false, false));
            }
        }
    }
}
