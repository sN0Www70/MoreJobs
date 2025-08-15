package com.snow.morejobs.event;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.items.ModItems;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.PoliceHandlers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreJobsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isClientSide()) return;
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        if (!(event.getTarget() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity police = (ServerPlayerEntity) event.getPlayer();
        ServerPlayerEntity target = (ServerPlayerEntity) event.getTarget();

        if (!JobDataStorage.get(police).hasActive(JobType.POLICE_OFFICER)) return;

        ItemStack stack = police.getItemInHand(event.getHand());
        if (stack.isEmpty() || stack.getItem() != ModItems.HANDCUFF.get()) return;
        if (police.distanceToSqr(target) > 9.0D) return;
        if (police.getCooldowns().isOnCooldown(ModItems.HANDCUFF.get())) return;

        if (event.getHand() == Hand.MAIN_HAND) {
            PoliceHandlers.setFrozen(target, !PoliceHandlers.isFrozen(target));
        } else if (event.getHand() == Hand.OFF_HAND) {
            if (PoliceHandlers.isEscorted(target)) {
                PoliceHandlers.stopEscort(target);
            } else {
                PoliceHandlers.startEscort(police, target);
            }
        }

        police.getCooldowns().addCooldown(ModItems.HANDCUFF.get(), 20); // 1s cooldown
        event.setCanceled(true);
        police.swing(event.getHand(), true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity sp = (ServerPlayerEntity) event.player;
        if (sp.level.isClientSide) return;

        PoliceHandlers.tick(sp);
    }
}
