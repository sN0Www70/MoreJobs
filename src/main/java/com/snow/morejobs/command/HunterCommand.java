package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.HunterSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class HunterCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("track")
                .requires(cs -> {
                    try {
                        ServerPlayerEntity p = cs.getPlayerOrException();
                        return JobDataStorage.get(p).hasActive(JobType.HUNTER);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(ctx -> {
                            String type = StringArgumentType.getString(ctx, "type");
                            CommandSource source = ctx.getSource();

                            if (!(source.getEntity() instanceof PlayerEntity)) {
                                source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                return 0;
                            }

                            PlayerEntity player = (PlayerEntity) source.getEntity();
                            String key = "track_" + type.toLowerCase();

                            if (CooldownManager.isOnCooldown(player, key)) {
                                long sec = CooldownManager.getRemaining(player, key) / 1000;
                                player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                                return 0;
                            }

                            boolean hostile = type.equalsIgnoreCase("hostile");
                            boolean passive = type.equalsIgnoreCase("passive");

                            if (!hostile && !passive) {
                                player.sendMessage(new StringTextComponent("Usage : /track passive | hostile"), player.getUUID());
                                return 0;
                            }

                            HunterSkills.track(player, hostile);
                            CooldownManager.setCooldown(player, key, 60_000); // 60 sec

                            return 1;
                        })));

        dispatcher.register(Commands.literal("bloodrush")
                .requires(cs -> {
                    try {
                        ServerPlayerEntity p = cs.getPlayerOrException();
                        return JobDataStorage.get(p).hasActive(JobType.HUNTER);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .executes(ctx -> {
                    CommandSource source = ctx.getSource();

                    if (!(source.getEntity() instanceof PlayerEntity)) {
                        source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                        return 0;
                    }

                    PlayerEntity player = (PlayerEntity) source.getEntity();
                    String key = "bloodrush";

                    if (CooldownManager.isOnCooldown(player, key)) {
                        long sec = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                        return 0;
                    }

                    HunterSkills.bloodrush(player);
                    CooldownManager.setCooldown(player, key, 300_000); // 5 min

                    return 1;
                }));
    }
}
