package org.mechSnow.sTLPluginHWChanges.utils;

import com.google.gson.*;
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
    // private final Plugin plugin;
    // private final Gson gson;
    private final Map<String, Map<String, Object>> allPlayerData = new HashMap<>();
    private File configFile; // Объявляем переменную без инициализации

    // public ConfigUtil(Plugin plugin) {
    //     this.plugin = plugin;
    //     // loadAllPlayerData();
    // }

    private void logInfo(String message) {
        // plugin.getLogger().log(Level.INFO, message);
    }

    private void logAndShutdown(String message) {
        // plugin.getLogger().log(Level.SEVERE, message);
        // plugin.getServer().shutdown();
    }

    public void configSetup() {}


//  Logic PlayerData.json

//  PlayerFirstJoin - save all data on json
    public void savePlayerFirstJoin(Player player) {}



    public void updatePlayerDataTemperature() {}
    public void updatePlayerDataPositions() {}

    public void getPlayerData(Player player) {}

//  Logic Barrier.json

    public void updateBarrierSettingsDataCenter() {}
    public void updateBarrierSettingsDataRadius() {}

    private void getBarrierSettingsDataCenter() {}
    private void getBarrierSettingsDataRadius() {}
}
