package org.mechSnow.sTLPluginHWChanges.managers;

import org.bukkit.plugin.Plugin;
import org.mechSnow.sTLPluginHWChanges.db.DbPlayerData;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;

public class DependencyManager {
    private DbPlayerData dbPlayerData;
    private PlayerListener playerListener;

    public DependencyManager(DatabaseManager databaseManager, Plugin plugin, BarrierManager barrierManager) {
        dbPlayerData = new DbPlayerData(databaseManager, playerListener);
        playerListener = new PlayerListener(plugin, dbPlayerData, barrierManager);
    }

    public DbPlayerData getDbPlayerData() {
        return dbPlayerData;
    }
    public PlayerListener getPlayerListener() {
        return playerListener;
    }
}