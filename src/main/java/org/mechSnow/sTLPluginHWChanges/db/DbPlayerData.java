package org.mechSnow.sTLPluginHWChanges.db;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener.PlayerPosition;
import org.mechSnow.sTLPluginHWChanges.managers.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class DbPlayerData {
    private final HashMap<UUID, PlayerPosition> playerPositions = new HashMap<>();
    private final HashMap<UUID, Double> playerTemperature = new HashMap<>();

    private final DatabaseManager dbManager;
    private PlayerListener playerListener;

    public DbPlayerData(DatabaseManager dbManager, PlayerListener playerListener) {
        this.dbManager = dbManager;
        this.playerListener = playerListener;
        createTablePlayer();
    }

    private void createTablePlayer() {
        try (Statement stmt = dbManager.getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS playerData (player_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT UNIQUE NOT NULL, name TEXT NOT NULL, last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP, x_coord INTEGER NOT NULL, y_coord INTEGER NOT NULL, z_coord INTEGER NOT NULL, temperature INTEGER)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to create the playerData table: " + e.getMessage());
        }
    }


    public boolean checkPlayerExists(Player player) {
        UUID playerId = player.getUniqueId();
        try (Connection conn = dbManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM playerData WHERE uuid = ?");
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }


    public void loadTemperatures() {
    String sql = "SELECT uuid, temperature, x_coord, y_coord, z_coord FROM playerData";
    try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            UUID playerId = UUID.fromString(rs.getString("uuid"));
            double temperature = rs.getDouble("temperature");
            int x = rs.getInt("x_coord");
            int y = rs.getInt("y_coord");
            int z = rs.getInt("z_coord");

            // Обновляем хэшмапы
            playerTemperature.put(playerId, temperature);
            playerPositions.put(playerId, new PlayerPosition(x, y, z));
        }
    } catch (SQLException e) {
        dbManager.getPlugin().getLogger().severe("Failed to load temperatures: " + e.getMessage());
    }
}

    /**
     * Saves the player data to the database, overwriting any existing data.
     * 
     * @param uuid  The player's UUID
     * @param name  The player's name
     * @param temperature  The player's current temperature
     * @param x  The player's x coordinate
     * @param y  The player's y coordinate
     * @param z  The player's z coordinate
     */
    public void savePlayerData(String uuid, String name, double temperature, int x, int y, int z) {
        String sql = "INSERT OR REPLACE INTO playerData (uuid, name, x_coord, y_coord, z_coord, temperature) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, name);
            stmt.setInt(3, x);
            stmt.setInt(4, y);
            stmt.setInt(5, z);
            stmt.setInt(6, (int) temperature);
            stmt.executeUpdate();
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public double getPlayerTemperature(String uuid) {
        String sql = "SELECT temperature FROM playerData WHERE uuid = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("temperature");
            }
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to get player temperature: " + e.getMessage());
        }
        return 0.0;
    }


    
    public double getPlayerTemperature(UUID playerId) {
        Double temperature = playerTemperature.get(playerId);
        if (temperature == null) {
            String sql = "SELECT temperature FROM playerData WHERE uuid = ?";
            try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    temperature = rs.getDouble("temperature");
                    playerTemperature.put(playerId, temperature);
                }
            } catch (SQLException e) {
                dbManager.getPlugin().getLogger().severe("Failed to get player temperature: " + e.getMessage());
            }
        }
        return temperature == null ? 0.0 : temperature;
    }

    public void updatePlayerTemperature(Player player, double temperature) {
        UUID playerId = player.getUniqueId();
        playerTemperature.put(playerId, temperature);
        // configUtil.updatePlayerTemperature(player, temperature);
    }

    public PlayerPosition getPlayerPosition(UUID playerId) {
        return playerPositions.get(playerId);
    }

    public void updatePlayerPosition(Player player, double roundedX, double roundedY, double roundedZ) {
        UUID playerId = player.getUniqueId();
        playerPositions.put(playerId, new PlayerPosition(roundedX, roundedY, roundedZ));
    }
}
