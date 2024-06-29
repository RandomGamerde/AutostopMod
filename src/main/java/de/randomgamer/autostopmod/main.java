package de.randomgamer.autostopmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

public class main implements ModInitializer {

    private long lastPlayerTime;

    @Override
    public void onInitialize() {
        // Load config
        Config.loadConfig();

        lastPlayerTime = System.currentTimeMillis();
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer server) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlayerTime >= Config.checkIntervalMillis) {
            checkPlayers(server, currentTime);
        }
    }

    private void checkPlayers(MinecraftServer server, long currentTime) {
        int playerCount = server.getPlayerManager().getPlayerList().size();
        if (playerCount > 0) {
            lastPlayerTime = currentTime;
        } else if (currentTime - lastPlayerTime >= Config.stopIntervalMillis) {
            // Stop the server
            stopServer(server);

            // Send message to Discord webhook
            String message = String.format(
                    "Server stopped, because their were no players Online for %d Minutes" +
                            Config.stopIntervalMillis
            );
            try {
                DiscordWebhook.sendMessage(Config.webhookUrl, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopServer(MinecraftServer server) {
        server.getPlayerManager().saveAllPlayerData();
        server.stop(false);
    }
}
