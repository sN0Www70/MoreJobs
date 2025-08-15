package com.snow.morejobs.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "morejobs", value = Dist.CLIENT)
public class CooldownOverlay {

    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 4;

    // Espacement entre deux barres verticales
    private static final int VERTICAL_SPACING = 14;

    // Marges pour le coin haut-gauche
    private static final int MARGIN_LEFT = 8;
    private static final int MARGIN_TOP  = 8;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || mc.options.hideGui) return;

        Map<String, Long> cooldowns = ClientCooldownManager.getCooldowns();
        if (cooldowns == null || cooldowns.isEmpty()) return;

        long now = System.currentTimeMillis();

        // Compte les cooldowns actifs
        int activeCooldowns = 0;
        for (long endTime : cooldowns.values()) {
            if (endTime > now) activeCooldowns++;
        }
        if (activeCooldowns == 0) return;

        FontRenderer font = mc.font;
        MatrixStack matrixStack = event.getMatrixStack();

        // Point de départ en HAUT GAUCHE
        int x = MARGIN_LEFT;
        int y = MARGIN_TOP;

        for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            String skill = entry.getKey();
            long endTime = entry.getValue();
            long remaining = Math.max(endTime - now, 0);
            if (remaining <= 0) continue;

            long totalDuration = ClientCooldownManager.getDuration(skill);
            if (totalDuration <= 0) totalDuration = 1; // garde-fou
            float progress = 1.0f - ((float) remaining / totalDuration);

            // Fond
            AbstractGui.fill(matrixStack, x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0x88333333);

            // Progression
            int progressWidth = (int) (BAR_WIDTH * progress);
            if (progressWidth > 0) {
                AbstractGui.fill(matrixStack, x, y, x + progressWidth, y + BAR_HEIGHT, 0xFF00AA00);
            }

            // Contour
            AbstractGui.fill(matrixStack, x - 1, y - 1, x + BAR_WIDTH + 1, y, 0x66FFFFFF);
            AbstractGui.fill(matrixStack, x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0x66FFFFFF);
            AbstractGui.fill(matrixStack, x - 1, y, x, y + BAR_HEIGHT, 0x66FFFFFF);
            AbstractGui.fill(matrixStack, x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, 0x66FFFFFF);

            // Nom du skill (au-dessus de la barre)
            String displayName = skill.length() > 6 ? skill.substring(0, 6) : skill;
            matrixStack.pushPose();
            matrixStack.scale(0.6f, 0.6f, 1.0f);
            int textWidth = font.width(displayName);
            // On convertit les coords pour l'échelle
            float nameDrawX = (x + (BAR_WIDTH - (int)(textWidth * 0.6f)) / 2f) / 0.6f;
            float nameDrawY = (y - 8) / 0.6f;
            font.drawShadow(matrixStack, displayName, nameDrawX, nameDrawY, 0xCCCCCC);
            matrixStack.popPose();

            // Temps restant (sous la barre)
            String timeText = formatTimeShort(remaining);
            matrixStack.pushPose();
            matrixStack.scale(0.5f, 0.5f, 1.0f);
            int timeWidth = font.width(timeText);
            float timeDrawX = (x + (BAR_WIDTH - (int)(timeWidth * 0.5f)) / 2f) / 0.5f;
            float timeDrawY = (y + BAR_HEIGHT + 2) / 0.5f;
            font.drawShadow(matrixStack, timeText, timeDrawX, timeDrawY, 0x888888);
            matrixStack.popPose();

            // Passe à la ligne suivante (pile verticalement)
            y += BAR_HEIGHT + VERTICAL_SPACING;
        }
    }

    private static String formatTimeShort(long ms) {
        long sec = ms / 1000;
        if (sec < 60) return sec + "s";
        long min = sec / 60;
        if (min < 60) return min + "m";
        return (min / 60) + "h";
    }
}
