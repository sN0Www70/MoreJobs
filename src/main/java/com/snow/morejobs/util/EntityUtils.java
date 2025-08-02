package com.snow.morejobs.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class EntityUtils {

    private static final Map<UUID, Map<String, Boolean>> flags = new HashMap<>();

    public static void toggleFlag(UUID playerId, String key, boolean value) {
        flags.computeIfAbsent(playerId, k -> new HashMap<>()).put(key, value);
    }

    public static boolean getFlag(UUID playerId, String key) {
        return flags.getOrDefault(playerId, new HashMap<>()).getOrDefault(key, false);
    }

    public static LivingEntity getNearestEntity(PlayerEntity player, boolean hostileOnly) {
        ServerWorld world = (ServerWorld) player.level;
        double radius = 16.0D;
        AxisAlignedBB box = player.getBoundingBox().inflate(radius);

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, box, e -> {
            if (e == player || !e.isAlive()) return false;
            return hostileOnly ? e instanceof MonsterEntity : e instanceof AnimalEntity;
        });

        return entities.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                .orElse(null);
    }

    public static void applyGlow(LivingEntity entity, int durationTicks) {
        entity.addEffect(new EffectInstance(Effects.GLOWING, durationTicks));
    }

    public static BlockPos findNearestOre(ServerWorld world, BlockPos origin, int radius) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    Block block = world.getBlockState(pos).getBlock();

                    if (isOre(block)) {
                        double dist = pos.distSqr(origin.getX(), origin.getY(), origin.getZ(), true);
                        if (dist < bestDistance) {
                            bestDistance = dist;
                            bestPos = pos.immutable();
                        }
                    }
                }
            }
        }

        return bestPos;
    }

    private static boolean isOre(Block block) {
        return block == Blocks.COAL_ORE ||
                block == Blocks.IRON_ORE ||
                block == Blocks.GOLD_ORE ||
                block == Blocks.REDSTONE_ORE ||
                block == Blocks.LAPIS_ORE ||
                block == Blocks.DIAMOND_ORE ||
                block == Blocks.EMERALD_ORE;
    }
}
