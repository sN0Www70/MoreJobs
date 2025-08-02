package com.snow.morejobs.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class CooldownNotifier {
    private static final Map<UUID, List<SkillNotice>> scheduled = new HashMap<>();

    public static void schedule(PlayerEntity player, String skillId, int delayTicks) {
        scheduled.computeIfAbsent(player.getUUID(), k -> new ArrayList<>())
                .add(new SkillNotice(skillId, delayTicks));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (UUID uuid : scheduled.keySet()) {
            List<SkillNotice> notices = scheduled.get(uuid);
            if (notices == null) continue;

            Iterator<SkillNotice> it = notices.iterator();
            while (it.hasNext()) {
                SkillNotice n = it.next();
                n.ticksLeft--;
                if (n.ticksLeft <= 0) {
                    PlayerEntity player = findPlayerByUUID(uuid);
                    if (player != null) {
                        player.sendMessage(new StringTextComponent("✅ " + n.skillId + " est prêt !"), uuid);
                    }
                    it.remove();
                }
            }
        }
    }

    private static PlayerEntity findPlayerByUUID(UUID uuid) {
        return net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer()
                .getPlayerList().getPlayers().stream()
                .filter(p -> p.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    private static class SkillNotice {
        String skillId;
        int ticksLeft;

        SkillNotice(String skillId, int ticksLeft) {
            this.skillId = skillId;
            this.ticksLeft = ticksLeft;
        }
    }
}
