package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.GardenerSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class GardenerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("growplants")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getEntity() instanceof ServerPlayerEntity ? (ServerPlayerEntity) ctx.getSource().getEntity() : null;
                    if (player == null) {
                        ctx.getSource().sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                        return 0;
                    }

                    if (!JobDataStorage.get(player).hasActive(JobType.GARDENER)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Gardener."), player.getUUID());
                        return 0;
                    }

                    String key = "grow_plants";
                    if (CooldownManager.isOnCooldown(player, key)) {
                        long sec = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                        return 0;
                    }

                    GardenerSkills.growPlants(player);
                    CooldownManager.setCooldown(player, key, 300_000); // 5 min
                    return 1;
                })
        );

        dispatcher.register(Commands.literal("harvestplants")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getEntity() instanceof ServerPlayerEntity ? (ServerPlayerEntity) ctx.getSource().getEntity() : null;
                    if (player == null) {
                        ctx.getSource().sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                        return 0;
                    }

                    if (!JobDataStorage.get(player).hasActive(JobType.GARDENER)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Gardener."), player.getUUID());
                        return 0;
                    }

                    String key = "harvest_plants";
                    if (CooldownManager.isOnCooldown(player, key)) {
                        long sec = CooldownManager.getRemaining(player, key) / 1000;
                        player.sendMessage(new StringTextComponent("Cooldown : " + sec + "s"), player.getUUID());
                        return 0;
                    }

                    GardenerSkills.harvestPlants(player);
                    CooldownManager.setCooldown(player, key, 60_000); // 1 min
                    return 1;
                })
        );
    }
}
