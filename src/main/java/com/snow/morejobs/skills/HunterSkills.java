package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.EntityUtils;
import com.snow.morejobs.util.EconomyUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;

public class HunterSkills {

    public static void track(PlayerEntity player, boolean hostile) {
        LivingEntity target = EntityUtils.getNearestEntity(player, hostile);
        if (target != null) {
            EntityUtils.applyGlow(target, 20 * 30);
            player.sendMessage(new StringTextComponent("Cible repérée : " + target.getName().getString()), player.getUUID());

            if (!player.level.isClientSide && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                JobDataStorage data = JobDataStorage.get(serverPlayer);
                if (data.hasActive(JobType.HUNTER)) {
                    data.addXp(JobType.HUNTER, 10);
                    data.save();
                    EconomyUtils.giveMoney(serverPlayer, 2);
                }
            }
        } else {
            player.sendMessage(new StringTextComponent("Aucune cible trouvée."), player.getUUID());
        }
    }

    public static void bloodrush(PlayerEntity player) {
        int duration = 20 * 20;

        player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, duration, 1, false, false));
        player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, duration, 0, false, false));
        player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, duration, 0, false, false));
        player.addEffect(new EffectInstance(Effects.NIGHT_VISION, duration, 0, false, false));

        player.sendMessage(new StringTextComponent("Bloodrush activé !"), player.getUUID());

        if (!player.level.isClientSide && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            JobDataStorage data = JobDataStorage.get(serverPlayer);
            if (data.hasActive(JobType.HUNTER)) {
                data.addXp(JobType.HUNTER, 5);
                data.save();
                EconomyUtils.giveMoney(serverPlayer, 1);
            }
        }
    }
}
