package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.EconomyUtils;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class FarmerSkills {

    private static final Set<UUID> activeLure = new HashSet<>();

    public static void feedNearbyAnimals(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;

        List<AnimalEntity> animals = world.getEntitiesOfClass(
                AnimalEntity.class,
                player.getBoundingBox().inflate(6),
                a -> !a.isBaby() && a.canFallInLove()
        );

        int fed = 0;
        for (AnimalEntity animal : animals) {
            animal.setInLove(player);
            fed++;
        }

        if (fed > 0) {
            JobDataStorage data = JobDataStorage.get(player);
            data.addXp(JobType.FARMER, fed);
            data.save();

            EconomyUtils.giveMoney(player, fed);
            player.sendMessage(new StringTextComponent("✅ " + fed + " animaux nourris | +" + fed + " XP & Chelous"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("❌ Aucun animal à nourrir trouvé."), player.getUUID());
        }
    }

    public static void toggleLure(ServerPlayerEntity player, boolean enable) {
        UUID uuid = player.getUUID();

        if (enable) {
            activeLure.add(uuid);
            player.sendMessage(new StringTextComponent("✨ Les animaux sont attirés par toi !"), uuid);
        } else {
            activeLure.remove(uuid);
            player.sendMessage(new StringTextComponent("❌ Attraction désactivée."), uuid);
        }
    }

    public static boolean isLureActive(ServerPlayerEntity player) {
        return activeLure.contains(player.getUUID());
    }
}
