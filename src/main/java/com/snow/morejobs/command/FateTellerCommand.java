package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.FateTellerSkills;
import com.snow.morejobs.util.CooldownManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class FateTellerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("fate")
                .then(Commands.argument("effect", StringArgumentType.word())
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> {
                                    CommandSource source = ctx.getSource();

                                    if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                        source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                        return 0;
                                    }

                                    ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
                                    JobDataStorage data = JobDataStorage.get(player);

                                    if (!data.hasActive(JobType.FATETELLER)) {
                                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas devin."), player.getUUID());
                                        return 0;
                                    }

                                    String key = "fate_effect";
                                    if (CooldownManager.isOnCooldown(player, key)) {
                                        long sec = CooldownManager.getRemaining(player, key) / 1000;
                                        player.sendMessage(new StringTextComponent("⏳ Cooldown : " + sec + "s"), player.getUUID());
                                        return 0;
                                    }

                                    String effect = StringArgumentType.getString(ctx, "effect");
                                    ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "target");

                                    boolean success = FateTellerSkills.applyEffect(player, effect, target);
                                    if (success) {
                                        CooldownManager.setCooldown(player, key, 2 * 60_000);
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }))));
    }
}
