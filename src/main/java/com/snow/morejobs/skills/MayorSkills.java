package com.snow.morejobs.skills;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class MayorSkills {

    public static void sendAnnouncement(ServerPlayerEntity player, String message) {
        ServerWorld world = (ServerWorld) player.level;

        String formatted = TextFormatting.GOLD + "[Annonce du Maire] " + TextFormatting.RESET + message;

        for (ServerPlayerEntity target : world.players()) {
            // Affichage prolongé via title
            target.connection.send(new STitlePacket(STitlePacket.Type.ACTIONBAR, new StringTextComponent(formatted)));
            target.connection.send(new STitlePacket(STitlePacket.Type.TIMES, null, 10, 100, 20));

            // Son discret type cloche
            target.level.playSound(null, target.blockPosition(), SoundEvents.NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        // Envoi webhook Discord
        sendWebhook(player.getName().getString(), message);
    }

    private static String readWebhookURL() {
        try {
            Path path = Paths.get("config/morejobs_webhook.secret");
            return Files.readAllLines(path).get(0).trim();
        } catch (IOException e) {
            System.err.println("[MoreJobs] Impossible de lire le webhook : " + e.getMessage());
            return "";
        }
    }

    private static void sendWebhook(String playerName, String message) {
        String webhookUrl = readWebhookURL();
        if (webhookUrl.isEmpty()) return;

        try {
            URL url = new URL(webhookUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = "{"
                    + "\"embeds\": [{"
                    +     "\"title\": \"📢 Annonce du Maire\","
                    +     "\"description\": \"" + escape(message) + "\","
                    +     "\"footer\": {\"text\": \"Envoy\\u00e9 par " + escape(playerName) + "\"},"
                    +     "\"color\": 16753920"
                    + "}]}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            connection.getInputStream().close();
        } catch (Exception e) {
            System.err.println("[MoreJobs] Échec d'envoi webhook : " + e.getMessage());
        }
    }

    private static String escape(String input) {
        return input.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
