package com.snow.morejobs.skills;

import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;

public class FateTellerSkills {

    public static boolean applyEffect(ServerPlayerEntity player, String effect, ServerPlayerEntity target) {
        switch (effect.toLowerCase()) {
            case "luck":
                target.addEffect(new EffectInstance(Effects.LUCK, 20 * 60 * 3, 0)); // 3 min
                player.sendMessage(new StringTextComponent("✨ Tu as béni " + target.getName().getString()), player.getUUID());
                target.sendMessage(new StringTextComponent("✨ Un devin t’a béni !"), target.getUUID());
                break;

            case "badomen":
                target.addEffect(new EffectInstance(Effects.BAD_OMEN, 20 * 60 * 3, 0)); // 3 min
                player.sendMessage(new StringTextComponent("⚠️ Tu as maudit " + target.getName().getString()), player.getUUID());
                target.sendMessage(new StringTextComponent("⚠️ Un devin t’a maudit..."), target.getUUID());
                break;

            default:
                player.sendMessage(new StringTextComponent("❌ Effet inconnu : luck | badomen"), player.getUUID());
                return false;
        }

        JobDataStorage.get(player).addXp(JobType.FATETELLER, 5);
        return true;
    }
}
