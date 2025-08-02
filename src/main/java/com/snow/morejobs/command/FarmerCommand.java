package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.FarmerSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class FarmerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("feedanimals")
                .executes(ctx -> {
                    CommandSource source = ctx.getSource();
                    if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                        source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                        return 0;
                    }

                    ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                    JobDataStorage data = JobDataStorage.get(player);
                    if (!data.hasActive(JobType.FARMER)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Farmer."), player.getUUID());
                        return 0;
                    }

                    String key = "feed_animals";

                    if (CooldownManager.isOnCooldown(player, key)) {
                        long sec = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                        return 0;
                    }

                    FarmerSkills.feedNearbyAnimals(player);
                    CooldownManager.setCooldown(player, key, 60_000);
                    return 1;
                }));

        dispatcher.register(Commands.literal("lureanimals")
                .then(Commands.argument("state", StringArgumentType.word())
                        .executes(ctx -> {
                            CommandSource source = ctx.getSource();
                            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                            JobDataStorage data = JobDataStorage.get(player);
                            if (!data.hasActive(JobType.FARMER)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas Farmer."), player.getUUID());
                                return 0;
                            }

                            String state = StringArgumentType.getString(ctx, "state");
                            boolean enable = state.equalsIgnoreCase("on");

                            FarmerSkills.toggleLure(player, enable);
                            return 1;
                        })));
    }
}
