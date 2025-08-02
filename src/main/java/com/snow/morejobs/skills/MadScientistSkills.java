package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class MadScientistSkills {

    private static final Set<String> ALLOWED_EFFECTS = new HashSet<>(Arrays.asList(
            "speed", "slowness", "levitation", "night_vision", "blindness", "jump_boost", "nausea"
    ));

    public static boolean runExperiment(ServerPlayerEntity player, String effectName) {
        if (!(player.level instanceof ServerWorld)) return false;
        ServerWorld world = (ServerWorld) player.level;

        String effectKey = effectName.toLowerCase();
        EffectInstance effect;

        switch (effectKey) {
            case "speed":
                effect = new EffectInstance(Effects.MOVEMENT_SPEED, 20 * 20, 1);
                break;
            case "slowness":
                effect = new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 20, 1);
                break;
            case "levitation":
                effect = new EffectInstance(Effects.LEVITATION, 20 * 20, 0);
                break;
            case "night_vision":
                effect = new EffectInstance(Effects.NIGHT_VISION, 20 * 20, 0);
                break;
            case "blindness":
                effect = new EffectInstance(Effects.BLINDNESS, 20 * 20, 0);
                break;
            case "jump_boost":
                effect = new EffectInstance(Effects.JUMP, 20 * 20, 2);
                break;
            case "nausea":
                effect = new EffectInstance(Effects.CONFUSION, 20 * 20, 0);
                break;
            default:
                player.sendMessage(new StringTextComponent("❌ Effet invalide."), player.getUUID());
                return false;
        }

        AxisAlignedBB box = player.getBoundingBox().inflate(5);
        List<ServerPlayerEntity> targets = world.getEntitiesOfClass(ServerPlayerEntity.class, box,
                p -> !p.getUUID().equals(player.getUUID()));

        for (ServerPlayerEntity target : targets) {
            target.addEffect(effect);
            target.sendMessage(new StringTextComponent("🧪 Un effet étrange t’a été appliqué…"), target.getUUID());
        }

        player.sendMessage(new StringTextComponent("🧪 Expérience déclenchée : " + effectKey), player.getUUID());

        JobDataStorage data = JobDataStorage.get(player);
        data.addXp(JobType.MAD_SCIENTIST, 5);
        data.save();

        return true;
    }

    public static boolean mutateNearbyPlayer(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;

        List<ServerPlayerEntity> targets = world.getEntitiesOfClass(ServerPlayerEntity.class,
                player.getBoundingBox().inflate(6), p -> !p.getUUID().equals(player.getUUID()));

        if (targets.isEmpty()) {
            player.sendMessage(new StringTextComponent("❌ Aucun joueur à transformer."), player.getUUID());
            return false;
        }

        ServerPlayerEntity target = targets.get(world.random.nextInt(targets.size()));
        return mutateTarget(player, target);
    }

    public static boolean mutateTarget(ServerPlayerEntity player, ServerPlayerEntity target) {
        ServerWorld world = (ServerWorld) player.level;

        target.addEffect(new EffectInstance(Effects.INVISIBILITY, 20 * 10, 0, false, false));

        PigEntity pig = EntityType.PIG.create(world);
        if (pig == null) return false;

        pig.setPos(target.getX(), target.getY(), target.getZ());
        pig.setInvulnerable(true);
        pig.setCustomName(new StringTextComponent("Mutant"));
        pig.setCustomNameVisible(true);

        world.addFreshEntity(pig);

        world.getServer().execute(() -> {
            pig.remove();
            target.removeEffect(Effects.INVISIBILITY);
            target.sendMessage(new StringTextComponent("🐷 Tu reviens à la normale."), target.getUUID());
        });

        player.sendMessage(new StringTextComponent("🐷 " + target.getName().getString() + " a été transformé !"), player.getUUID());

        JobDataStorage data = JobDataStorage.get(player);
        data.addXp(JobType.MAD_SCIENTIST, 5);
        data.save();

        return true;
    }
}
