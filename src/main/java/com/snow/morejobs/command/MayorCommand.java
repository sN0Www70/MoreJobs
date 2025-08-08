package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.skills.MayorSkills;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class MayorCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("annonce")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrException();

                            if (!JobDataStorage.get(player).hasActive(JobType.MAYOR)) {
                                player.sendMessage(new StringTextComponent("❌ Tu dois être Maire pour utiliser cette commande."), player.getUUID());
                                return 0;
                            }

                            String message = StringArgumentType.getString(context, "message");
                            MayorSkills.sendAnnouncement(player, message);
                            return 1;
                        })));
    }
}
