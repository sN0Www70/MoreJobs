package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.InteriorDesignerSkills;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class InteriorDesignerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("colorpalette")
                .then(Commands.argument("state", StringArgumentType.word())
                        .executes(ctx -> {
                            CommandSource source = ctx.getSource();
                            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                source.sendSuccess(new StringTextComponent("Commande réservée aux joueurs."), false);
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                            if (!JobDataStorage.get(player).hasActive(JobType.INTERIOR_DESIGNER)) {
                                player.sendMessage(new StringTextComponent("❌ Tu n'es pas Interior Designer."), player.getUUID());
                                return 0;
                            }

                            String state = StringArgumentType.getString(ctx, "state");

                            if (state.equalsIgnoreCase("on")) {
                                InteriorDesignerSkills.setPalette(player, true);
                            } else if (state.equalsIgnoreCase("off")) {
                                InteriorDesignerSkills.setPalette(player, false);
                            } else {
                                player.sendMessage(new StringTextComponent("Usage : /colorpalette on | off"), player.getUUID());
                                return 0;
                            }

                            return 1;
                        })));
    }
}
