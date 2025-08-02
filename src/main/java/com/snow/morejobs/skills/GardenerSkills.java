package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.CropUtils;
import com.snow.morejobs.util.EconomyUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class GardenerSkills {

    public static void growPlants(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;
        int count = CropUtils.growCropsAround(world, player.blockPosition(), 10);

        if (count > 0) {
            JobDataStorage.get(player).addXp(JobType.GARDENER, 2);
            EconomyUtils.giveMoney(player, 2);
            player.sendMessage(new StringTextComponent("✅ " + count + " plantations poussées | +2 XP & Chelous"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("❌ Aucune plante à faire pousser."), player.getUUID());
        }
    }

    public static void harvestPlants(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;
        List<ItemStack> drops = CropUtils.harvestCropsAround(world, player.blockPosition(), 10);

        if (!drops.isEmpty()) {
            drops.forEach(player::addItem);

            JobDataStorage.get(player).addXp(JobType.GARDENER, 5);
            EconomyUtils.giveMoney(player, 5);
            player.sendMessage(new StringTextComponent("✅ " + drops.size() + " items récoltés | +5 XP & Chelous"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("❌ Rien à récolter."), player.getUUID());
        }
    }
}
