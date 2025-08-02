package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.AlienSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class AlienCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("alien")
                .then(Commands.literal("levitateall")
                        .executes(ctx -> {
                            CommandSource source = ctx.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                            if (!JobDataStorage.get(player).hasActive(JobType.ALIEN)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas Alien."), player.getUUID());
                                return 0;
                            }

                            String key = "alien_levitate";

                            if (CooldownManager.isOnCooldown(player, key)) {
                                long sec = CooldownManager.getRemaining(player, key) / 1000;
                                player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                                return 0;
                            }

                            AlienSkills.levitateNearby(player);
                            CooldownManager.setCooldown(player, key, 60_000); // 1 min
                            return 1;
                        }))
                .then(Commands.literal("swapplayer")
                        .executes(ctx -> {
                            CommandSource source = ctx.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                            if (!JobDataStorage.get(player).hasActive(JobType.ALIEN)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas Alien."), player.getUUID());
                                return 0;
                            }

                            String key = "alien_swap";

                            if (CooldownManager.isOnCooldown(player, key)) {
                                long sec = CooldownManager.getRemaining(player, key) / 1000;
                                player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                                return 0;
                            }

                            boolean success = AlienSkills.swapWithNearby(player);
                            if (success) {
                                CooldownManager.setCooldown(player, key, 300_000); // 5 min
                            } else {
                                player.sendMessage(new StringTextComponent("Aucun joueur à proximité pour échanger de place."), player.getUUID());
                            }

                            return 1;
                        }))
        );
    }
}
