package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class AlienSkills {

    public static void levitateNearby(PlayerEntity player) {
        if (!(player.level instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) player.level;
        AxisAlignedBB box = player.getBoundingBox().inflate(5);
        List<ServerPlayerEntity> nearby = world.getEntitiesOfClass(ServerPlayerEntity.class, box,
                p -> !p.getUUID().equals(player.getUUID()));

        if (nearby.isEmpty()) {
            player.sendMessage(new StringTextComponent("Aucun joueur autour."), player.getUUID());
            return;
        }

        for (ServerPlayerEntity target : nearby) {
            target.addEffect(new EffectInstance(Effects.LEVITATION, 100, 1)); // 5 sec
        }

        player.sendMessage(new StringTextComponent("Tous les joueurs proches lévitent !"), player.getUUID());

        if (player instanceof ServerPlayerEntity) {
            JobDataStorage.get((ServerPlayerEntity) player).addXp(JobType.ALIEN, 2);
        }
    }

    public static boolean swapWithNearby(PlayerEntity player) {
        if (!(player.level instanceof ServerWorld)) return false;

        ServerWorld world = (ServerWorld) player.level;
        AxisAlignedBB box = player.getBoundingBox().inflate(10);
        List<ServerPlayerEntity> targets = world.getEntitiesOfClass(ServerPlayerEntity.class, box,
                p -> !p.getUUID().equals(player.getUUID()));

        if (targets.isEmpty()) {
            return false;
        }

        ServerPlayerEntity target = targets.get(world.random.nextInt(targets.size()));

        double x1 = player.getX(), y1 = player.getY(), z1 = player.getZ();
        player.teleportTo(target.getX(), target.getY(), target.getZ());
        target.teleportTo(x1, y1, z1);

        player.sendMessage(new StringTextComponent("Échange de position avec " + target.getName().getString()), player.getUUID());
        target.sendMessage(new StringTextComponent("Un alien vous a échangé de place avec lui !"), target.getUUID());

        if (player instanceof ServerPlayerEntity) {
            JobDataStorage.get((ServerPlayerEntity) player).addXp(JobType.ALIEN, 5);
        }

        return true;
    }
}
