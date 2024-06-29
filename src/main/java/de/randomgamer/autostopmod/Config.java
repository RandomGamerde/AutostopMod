package de.randomgamer.autostopmod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;

public class Config {
    public static String webhookUrl;
    public static long checkIntervalMillis;
    public static long stopIntervalMillis;

    private static final String CONFIG_FILE_PATH = "config/playercheckmod.json";

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE_PATH);

        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        try (InputStream inputStream = new FileInputStream(configFile)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

            webhookUrl = json.get("webhookUrl").getAsString();
            checkIntervalMillis = json.get("checkIntervalSeconds").getAsLong() * 1000;
            stopIntervalMillis = json.get("stopIntervalMinutes").getAsLong() * 60 * 1000;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig(File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();

            JsonObject defaultConfig = new JsonObject();
            defaultConfig.addProperty("webhookUrl", "YOUR_DISCORD_WEBHOOK_URL_HERE");
            defaultConfig.addProperty("checkIntervalSeconds", 10);
            defaultConfig.addProperty("stopIntervalMinutes", 10);

            try (Writer writer = new FileWriter(configFile)) {
                new Gson().toJson(defaultConfig, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
