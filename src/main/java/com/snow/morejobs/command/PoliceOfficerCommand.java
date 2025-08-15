package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.gui.checkup.CheckupContainer;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class PoliceOfficerCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkup")
                .requires(cs -> cs.getEntity() instanceof ServerPlayerEntity)
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity police = ctx.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(ctx, "player");

                            // Vérification métier
                            if (!JobDataStorage.get(police).hasActive(JobType.POLICE_OFFICER)) {
                                police.sendMessage(new StringTextComponent("❌ Tu n'es pas policier."), police.getUUID());
                                return 0;
                            }

                            MinecraftServer server = ctx.getSource().getServer();
                            ServerPlayerEntity target = server.getPlayerList().getPlayerByName(name);

                            if (target == null) {
                                police.sendMessage(new StringTextComponent("❌ Joueur introuvable."), police.getUUID());
                                return 0;
                            }

                            // Vérification de distance
                            if (police.distanceToSqr(target) > 9) { // 3 blocs max
                                police.sendMessage(new StringTextComponent("❌ Trop loin pour inspecter ce joueur."), police.getUUID());
                                return 0;
                            }

                            // Ouverture de l'inventaire en lecture seule
                            NetworkHooks.openGui(police, new SimpleNamedContainerProvider(
                                    (windowId, inv, playerEntity) -> new CheckupContainer(windowId, inv, target),
                                    new TranslationTextComponent("container.morejobs.checkup")
                            ), buf -> buf.writeUUID(target.getUUID()));

                            police.sendMessage(new StringTextComponent("🎒 Tu inspectes " + target.getName().getString()), police.getUUID());
                            return 1;
                        })));
    }
}
