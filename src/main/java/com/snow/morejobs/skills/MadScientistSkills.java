package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class MadScientistSkills {

    private static final Set<String> ALLOWED_EFFECTS = new HashSet<>(Arrays.asList(
            "speed", "slowness", "levitation", "night_vision", "blindness", "jump_boost", "nausea"
    ));

    private static final Map<UUID, Long> experimentZones = new HashMap<>();
    private static final Map<UUID, String> activeEffects = new HashMap<>();

    public static boolean runExperiment(ServerPlayerEntity player, String effectName) {
        if (!(player.level instanceof ServerWorld)) return false;

        String effectKey = effectName.toLowerCase();
        if (!ALLOWED_EFFECTS.contains(effectKey)) {
            player.sendMessage(new StringTextComponent("❌ Effet invalide."), player.getUUID());
            return false;
        }

        experimentZones.put(player.getUUID(), System.currentTimeMillis() + 20_000);
        activeEffects.put(player.getUUID(), effectKey);

        player.sendMessage(new StringTextComponent("🧪 Zone d’expérimentation activée (20s) - effet : " + effectKey), player.getUUID());

        JobDataStorage data = JobDataStorage.get(player);
        data.addXp(JobType.MAD_SCIENTIST, 5);
        data.save();

        return true;
    }

    public static void tick(ServerWorld world) {
        long now = System.currentTimeMillis();

        for (ServerPlayerEntity player : world.getPlayers(p -> true)) {
            UUID uuid = player.getUUID();
            if (!experimentZones.containsKey(uuid)) continue;

            long endTime = experimentZones.get(uuid);
            if (now > endTime) {
                experimentZones.remove(uuid);
                activeEffects.remove(uuid);
                continue;
            }

            Vector3d center = player.position();
            double radius = 5.0;
            for (int i = 0; i < 360; i += 15) {
                double angle = Math.toRadians(i);
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                world.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, center.y, z, 1, 0, 0.1, 0, 0.01);
            }

            String effectKey = activeEffects.get(uuid);
            EffectInstance effect = getEffect(effectKey);
            if (effect == null) continue;

            AxisAlignedBB box = player.getBoundingBox().inflate(radius);
            List<ServerPlayerEntity> targets = world.getEntitiesOfClass(ServerPlayerEntity.class, box,
                    p -> !p.getUUID().equals(uuid));

            for (ServerPlayerEntity target : targets) {
                if (player.distanceToSqr(target) <= radius * radius && !target.hasEffect(effect.getEffect())) {
                    target.addEffect(new EffectInstance(effect.getEffect(), 40, effect.getAmplifier()));
                    target.sendMessage(new StringTextComponent("🧪 Un effet étrange t’affecte…"), target.getUUID());
                }
            }
        }
    }

    private static EffectInstance getEffect(String effectKey) {
        switch (effectKey) {
            case "speed": return new EffectInstance(Effects.MOVEMENT_SPEED, 40, 1);
            case "slowness": return new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 1);
            case "levitation": return new EffectInstance(Effects.LEVITATION, 40, 0);
            case "night_vision": return new EffectInstance(Effects.NIGHT_VISION, 40, 0);
            case "blindness": return new EffectInstance(Effects.BLINDNESS, 40, 0);
            case "jump_boost": return new EffectInstance(Effects.JUMP, 40, 2);
            case "nausea": return new EffectInstance(Effects.CONFUSION, 40, 0);
            default: return null;
        }
    }
}
