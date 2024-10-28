package org.mechSnow.sTLPluginHWChanges.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigUtil {
    private final Plugin plugin;
    private final Gson gson;
    private final Map<String, Map<String, Object>> allPlayerData = new HashMap<>();
    private File configFile; // Объявляем переменную без инициализации

    public ConfigUtil(Plugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // Убедитесь, что папка данных плагина существует
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logAndShutdown("Не удалось создать папку плагина.");
            return;
        }

        // Инициализируем configFile после проверки существования папки данных
        this.configFile = new File(dataFolder, "PlayerData.json");

        // Загружаем данные игроков из файла при инициализации
        loadAllPlayerData();
    }

    public void isPlayerDataCheck(Player player) {
        if (player == null) {
            logAndShutdown("Player object is null.");
            return;
        }

        String uuid = player.getUniqueId().toString();

        // Check if the player data already exists
        if (!allPlayerData.containsKey(uuid)) {
            createNewPlayerData(player);
        }
    }

    private void createNewPlayerData(Player player) {
        String uuid = player.getUniqueId().toString(); // Get UUID here
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("UUID", uuid);
        playerData.put("name", player.getName());
        playerData.put("lastJoinTemperature", 268.00);
        playerData.put("lastJoinPositions", Map.of(
                "x", player.getLocation().getX(),
                "y", player.getLocation().getY(),
                "z", player.getLocation().getZ()
        ));
        playerData.put("lore", new HashMap<String, Object>()); // Placeholder for lore

        allPlayerData.put(uuid, playerData);
        saveAllPlayerData();
        logInfo("Player data for " + player.getName() + " has been successfully created.");
    }

    private void loadAllPlayerData() {
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    logInfo("Player data file has been created.");
                }
            } catch (IOException e) {
                logAndShutdown("Failed to create the file " + configFile.getName());
                return;
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            Map<String, Map<String, Object>> loadedData = gson.fromJson(reader, Map.class);
            if (loadedData != null) {
                allPlayerData.putAll(loadedData);
                logInfo("Player data has been successfully loaded.");
            }
        } catch (IOException e) {
            logAndShutdown("Failed to load data from file " + configFile.getName());
        }
    }

    public void updatePlayerPosition(Player player) {
        if (player == null) {
            logAndShutdown("Player object is null.");
            return;
        }

        String uuid = player.getUniqueId().toString();
        if (allPlayerData.containsKey(uuid)) {
            allPlayerData.get(uuid).put("lastJoinPositions", Map.of(
                    "x", player.getLocation().getX(),
                    "y", player.getLocation().getY(),
                    "z", player.getLocation().getZ()
            ));
            saveAllPlayerData();
            logInfo("Updated position for player " + player.getName());
        }
    }

    public void updatePlayerTemperature(Player player, double temperature) {
        if (player == null) {
            logAndShutdown("Player object is null.");
            return;
        }

        String uuid = player.getUniqueId().toString();
        if (allPlayerData.containsKey(uuid)) {
            allPlayerData.get(uuid).put("lastJoinTemperature", temperature);
            saveAllPlayerData();
            logInfo("Updated temperature for player " + player.getName() + " to " + temperature);
        }
    }

    private void saveAllPlayerData() {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(allPlayerData, writer);
            logInfo("Player data has been successfully saved to " + configFile.getName());
        } catch (IOException e) {
            logAndShutdown("Failed to save player data to file " + configFile.getName());
        }
    }

    private void logInfo(String message) {
        plugin.getLogger().log(Level.INFO, message);
    }

    private void logAndShutdown(String message) {
        plugin.getLogger().log(Level.SEVERE, message);
        plugin.getServer().shutdown();
    }
}
