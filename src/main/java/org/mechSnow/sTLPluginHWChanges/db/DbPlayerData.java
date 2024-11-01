package org.mechSnow.sTLPluginHWChanges.db;

import org.mechSnow.sTLPluginHWChanges.managers.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbPlayerData {
    private final DatabaseManager dbManager;
    public DbPlayerData(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        createTablePlayer();
    }

    private void createTablePlayer() {
        try (Statement stmt = dbManager.getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS playerData (" +
                         "player_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "uuid TEXT UNIQUE NOT NULL," +
                         "name TEXT NOT NULL," +
                         "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                         "x_coord INTEGER NOT NULL," +
                         "y_coord INTEGER NOT NULL," +
                         "z_coord INTEGER NOT NULL," +
                         "temperature_id INTEGER," +
                         "FOREIGN KEY (temperature_id) REFERENCES temperatures(temp_id)" +
                         ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to create the playerData table: " + e.getMessage());
        }
    }

    public void savePlayerData(String uuid, String name, double temperature, int x, int y, int z) {
        String sql = "INSERT OR REPLACE INTO playerData (uuid, name, x_coord, y_coord, z_coord, temperature_id) VALUES (?, ?, ?, ?, ?, ?)";
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
        String sql = "SELECT temperature_id FROM playerData WHERE uuid = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("temperature_id");
            }
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to get player temperature: " + e.getMessage());
        }
        return 0.0;
    }
}
