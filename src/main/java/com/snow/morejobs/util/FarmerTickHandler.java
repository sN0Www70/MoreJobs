package com.snow.morejobs.util;

import com.snow.morejobs.skills.FarmerSkills;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FarmerTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        if (player.level.isClientSide) return;

        if (!FarmerSkills.isLureActive(player)) return;

        player.level.getEntitiesOfClass(
                AnimalEntity.class,
                player.getBoundingBox().inflate(8),
                a -> !a.isBaby()
        ).forEach(animal -> animal.getNavigation().moveTo(player, 1.2));
    }
}
