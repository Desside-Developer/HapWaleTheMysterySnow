package org.mechSnow.sTLPluginHWChanges.db;

import org.bukkit.Location;
import org.mechSnow.sTLPluginHWChanges.managers.DatabaseManager;

import java.sql.*;

public class DbBarrierData {
    private final DatabaseManager dbManager;

    public DbBarrierData(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        createTableBarrier();
    }

    private void createTableBarrier() {
        try (Statement stmt = dbManager.getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS barrierData (id INTEGER PRIMARY KEY AUTOINCREMENT, center_x REAL, center_y REAL, center_z REAL, radius REAL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to create the barrierData table: " + e.getMessage());
        }
    }

    public void saveBarrierData(Location center, double radius) {
        String sql = "INSERT OR REPLACE INTO barrierData (center_x, center_y, center_z, radius) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, center.getX());
            stmt.setDouble(2, center.getY());
            stmt.setDouble(3, center.getZ());
            stmt.setDouble(4, radius);
            stmt.executeUpdate();
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to save barrier data: " + e.getMessage());
        }
    }

    public Location loadBarrierData() {
        String sql = "SELECT center_x, center_y, center_z, radius FROM barrierData LIMIT 1";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double x = rs.getDouble("center_x");
                double y = rs.getDouble("center_y");
                double z = rs.getDouble("center_z");
                double radius = rs.getDouble("radius");

                return new Location(null, x, y, z); // Здесь, возможно, нужно будет указать правильный мир
            }
        } catch (SQLException e) {
            dbManager.getPlugin().getLogger().severe("Failed to load barrier data: " + e.getMessage());
        }
        return null;
    }
}
