package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

public class MoreJobsCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("morejobs")
                .requires(cs -> {
                    // Autorise tous les joueurs (et OP en console)
                    if (cs.getEntity() instanceof ServerPlayerEntity) return true;
                    return cs.hasPermission(2);
                })

                .then(Commands.literal("assignjob")
                        .requires(cs -> {
                            if (cs.hasPermission(2)) return true;
                            if (cs.getEntity() instanceof ServerPlayerEntity) {
                                ServerPlayerEntity player = (ServerPlayerEntity) cs.getEntity();
                                return com.snow.morejobs.data.JobDataStorage.get(player).hasActive(com.snow.morejobs.jobs.JobType.MAYOR);
                            }
                            return false;
                        })
                        .then(Commands.argument("job", StringArgumentType.word())
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String jobName = StringArgumentType.getString(ctx, "job");
                                            String playerName = StringArgumentType.getString(ctx, "player");
                                            return assignJob(ctx.getSource(), jobName, playerName);
                                        }))))



                .then(Commands.literal("jobquit")
                        .requires(cs -> cs.getEntity() instanceof ServerPlayerEntity)
                        .then(Commands.argument("job", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    String jobName = StringArgumentType.getString(ctx, "job");
                                    return quitJob(player, jobName);
                                })))

                .then(Commands.literal("jobinfo")
                        .requires(cs -> cs.getEntity() instanceof ServerPlayerEntity)
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                            return showInfo(ctx.getSource(), player);
                        })
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(cs -> {
                                    if (cs.hasPermission(2)) return true;
                                    if (cs.getEntity() instanceof ServerPlayerEntity) {
                                        ServerPlayerEntity player = (ServerPlayerEntity) cs.getEntity();
                                        return com.snow.morejobs.data.JobDataStorage.get(player).hasActive(com.snow.morejobs.jobs.JobType.MAYOR);
                                    }
                                    return false;
                                })
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "player");
                                    MinecraftServer server = ctx.getSource().getServer();
                                    ServerPlayerEntity target = server.getPlayerList().getPlayerByName(name);
                                    if (target != null) {
                                        return showInfo(ctx.getSource(), target);
                                    } else {
                                        ctx.getSource().sendFailure(new StringTextComponent("Joueur introuvable."));
                                        return 0;
                                    }
                                })))


                .executes(ctx -> {
                    ctx.getSource().sendSuccess(new StringTextComponent("§6=== MoreJobs - Commandes disponibles ==="), false);
                    if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
                        ctx.getSource().sendSuccess(new StringTextComponent("§e/morejobs jobinfo §7- Voir tes informations de jobs"), false);
                        ctx.getSource().sendSuccess(new StringTextComponent("§e/morejobs jobquit <job> §7- Quitter un job"), false);
                    }
                    if (ctx.getSource().hasPermission(2)) {
                        ctx.getSource().sendSuccess(new StringTextComponent("§c/morejobs assignjob <job> <joueur> §7- Attribuer un job"), false);
                        ctx.getSource().sendSuccess(new StringTextComponent("§c/morejobs jobinfo <joueur> §7- Voir les jobs d'un joueur"), false);
                    }
                    return 1;
                })
        );
    }

    private static int assignJob(CommandSource source, String jobName, String playerName) {
        JobType job = JobType.fromName(jobName);
        if (job == JobType.NONE) {
            source.sendFailure(new StringTextComponent("Job inconnu."));
            return 0;
        }

        if (job.isSecret()) {
            source.sendFailure(new StringTextComponent("Ce job est secret et ne peut pas être attribué manuellement."));
            return 0;
        }

        ServerPlayerEntity player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(new StringTextComponent("Joueur introuvable."));
            return 0;
        }

        JobDataStorage data = JobDataStorage.get(player);
        data.setActive(job);
        data.unlockJob(job);
        data.save();

        if (job.hasLuckPermsGroup()) {
            String cmd = "lp user " + player.getName().getString() + " parent add " + job.getLuckPermsGroup();
            System.out.println("[MoreJobs] LP → " + cmd);

            try {
                Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                Object server = bukkitClass.getMethod("getServer").invoke(null);
                Object console = server.getClass().getMethod("getConsoleSender").invoke(server);
                server.getClass().getMethod("dispatchCommand", Class.forName("org.bukkit.command.CommandSender"), String.class)
                        .invoke(server, console, cmd);
            } catch (Throwable e) {
                System.err.println("[MoreJobs] Erreur LP Bukkit (assign): " + e.getMessage());
            }
        }

        source.sendSuccess(new StringTextComponent("Job " + job.getDisplayName() + " attribué à " + playerName + "."), true);
        return 1;
    }

    private static int quitJob(ServerPlayerEntity player, String jobName) {
        JobType job = JobType.fromName(jobName);
        if (job == JobType.NONE) return 0;

        if (job.isSecret()) {
            player.sendMessage(new StringTextComponent("Ce job ne peut pas être quitté."), player.getUUID());
            return 0;
        }

        JobDataStorage data = JobDataStorage.get(player);
        data.removeActive(job);
        data.save();

        if (job.hasLuckPermsGroup()) {
            String cmd = "lp user " + player.getName().getString() + " parent remove " + job.getLuckPermsGroup();
            System.out.println("[MoreJobs] LP → " + cmd);

            try {
                Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                Object server = bukkitClass.getMethod("getServer").invoke(null);
                Object console = server.getClass().getMethod("getConsoleSender").invoke(server);
                server.getClass().getMethod("dispatchCommand", Class.forName("org.bukkit.command.CommandSender"), String.class)
                        .invoke(server, console, cmd);
            } catch (Throwable e) {
                System.err.println("[MoreJobs] Erreur LP Bukkit (quit): " + e.getMessage());
            }

        }

        player.sendMessage(new StringTextComponent("Tu as quitté le job : " + job.getDisplayName()), player.getUUID());
        return 1;
    }


    private static int showInfo(CommandSource source, ServerPlayerEntity player) {
        JobDataStorage data = JobDataStorage.get(player);
        source.sendSuccess(new StringTextComponent("§6=== Infos de " + player.getName().getString() + " ==="), false);

        for (JobType job : JobType.values()) {
            if (job == JobType.NONE) continue;
            int xp = data.getXp(job);
            boolean active = data.hasActive(job);
            boolean unlocked = data.isUnlocked(job);

            String status = (active ? "§a[ACTIF]" : unlocked ? "§7[Débloqué]" : "§c[Verrouillé]");
            source.sendSuccess(new StringTextComponent(status + " §e" + job.getDisplayName() + " §7(" + xp + " xp)"), false);
        }

        return 1;
    }
}
