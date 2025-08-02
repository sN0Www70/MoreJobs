package com.snow.morejobs.command;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.MinerSkills;
import com.snow.morejobs.util.CooldownManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class MinerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal("minerfocus")
                .executes(ctx -> {
                    if (!(ctx.getSource().getEntity() instanceof ServerPlayerEntity)) {
                        ctx.getSource().sendSuccess(new StringTextComponent("Joueurs uniquement"), false);
                        return 0;
                    }

                    ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();

                    if (!JobDataStorage.get(player).hasActive(JobType.MINER)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Mineur."), player.getUUID());
                        return 0;
                    }

                    String key = "miner_focus";
                    if (CooldownManager.isOnCooldown(player, key)) {
                        long s = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + s + "s"), player.getUUID());
                        return 0;
                    }

                    MinerSkills.minerFocus(player);
                    CooldownManager.setCooldown(player, key, 5 * 60_000); // 5 minutes
                    return 1;
                })
        );

        dispatcher.register(Commands.literal("echolocate")
                .then(Commands.argument("state", StringArgumentType.word())
                        .executes(ctx -> {
                            if (!(ctx.getSource().getEntity() instanceof ServerPlayerEntity)) {
                                ctx.getSource().sendSuccess(new StringTextComponent("Joueurs uniquement"), false);
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();

                            if (!JobDataStorage.get(player).hasActive(JobType.MINER)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas Mineur."), player.getUUID());
                                return 0;
                            }

                            boolean enable = StringArgumentType.getString(ctx, "state").equalsIgnoreCase("on");
                            MinerSkills.toggleEchoLocate(player, enable);
                            return 1;
                        }))
        );

        dispatcher.register(Commands.literal("locateore")
                .executes(ctx -> {
                    if (!(ctx.getSource().getEntity() instanceof ServerPlayerEntity)) {
                        ctx.getSource().sendSuccess(new StringTextComponent("Joueurs uniquement"), false);
                        return 0;
                    }

                    ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();

                    if (!JobDataStorage.get(player).hasActive(JobType.MINER)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Mineur."), player.getUUID());
                        return 0;
                    }

                    String key = "locate_ore";
                    if (CooldownManager.isOnCooldown(player, key)) {
                        long s = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + s + "s"), player.getUUID());
                        return 0;
                    }

                    MinerSkills.locateOre(player);
                    CooldownManager.setCooldown(player, key, 2 * 60_000); // 2 minutes
                    return 1;
                })
        );
    }
}
