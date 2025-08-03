package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.EntityArgument;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.MadScientistSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class MadScientistCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("experiment")
                .then(Commands.argument("effect", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                            if (!JobDataStorage.get(player).hasActive(JobType.MAD_SCIENTIST)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas scientifique."), player.getUUID());
                                return 0;
                            }

                            if (CooldownManager.isOnCooldown(player, "experiment")) {
                                long sec = CooldownManager.getRemaining(player, "experiment") / 1000;
                                player.sendMessage(new StringTextComponent("⏳ Cooldown : " + sec + "s"), player.getUUID());
                                return 0;
                            }

                            String effect = StringArgumentType.getString(ctx, "effect");
                            boolean success = MadScientistSkills.runExperiment(player, effect);
                            if (success) {
                                CooldownManager.setCooldown(player, "experiment", 2 * 60_000);
                                return 1;
                            }
                            return 0;
                        })
                )
        );

        dispatcher.register(Commands.literal("mutate")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                    if (!JobDataStorage.get(player).hasActive(JobType.MAD_SCIENTIST)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas scientifique."), player.getUUID());
                        return 0;
                    }

                    if (CooldownManager.isOnCooldown(player, "mutate")) {
                        long sec = CooldownManager.getRemaining(player, "mutate") / 1000;
                        player.sendMessage(new StringTextComponent("⏳ Cooldown : " + sec + "s"), player.getUUID());
                        return 0;
                    }

                    boolean success = MadScientistSkills.spawnCloneNearby(player);
                    if (success) {
                        CooldownManager.setCooldown(player, "mutate", 3 * 60_000);
                        return 1;
                    }
                    return 0;
                })
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                            ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "target");

                            if (!JobDataStorage.get(player).hasActive(JobType.MAD_SCIENTIST)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas scientifique."), player.getUUID());
                                return 0;
                            }

                            if (CooldownManager.isOnCooldown(player, "mutate")) {
                                long sec = CooldownManager.getRemaining(player, "mutate") / 1000;
                                player.sendMessage(new StringTextComponent("⏳ Cooldown : " + sec + "s"), player.getUUID());
                                return 0;
                            }

                            boolean success = MadScientistSkills.spawnClone(player, target);
                            if (success) {
                                CooldownManager.setCooldown(player, "mutate", 3 * 60_000);
                                return 1;
                            }
                            return 0;
                        })
                )
        );
    }
}
