package com.snow.morejobs.skills;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InteriorDesignerSkills {
    private static final Set<UUID> activePalette = new HashSet<>();

    public static void setPalette(PlayerEntity player, boolean enable) {
        UUID id = player.getUUID();
        if (enable) {
            activePalette.add(id);
            player.sendMessage(new StringTextComponent("Color palette activée !"), id);
        } else {
            activePalette.remove(id);
            player.sendMessage(new StringTextComponent("Color palette désactivée."), id);
        }
    }

    public static boolean isPaletteActive(PlayerEntity player) {
        return activePalette.contains(player.getUUID());
    }
}
