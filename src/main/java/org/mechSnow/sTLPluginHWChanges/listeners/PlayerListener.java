package org.mechSnow.sTLPluginHWChanges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mechSnow.sTLPluginHWChanges.db.DbPlayerData;
import org.mechSnow.sTLPluginHWChanges.managers.BarrierManager;
import org.mechSnow.sTLPluginHWChanges.utils.ConfigUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitTask;

public class PlayerListener implements Listener {
    private final Plugin plugin;
    // private final ConfigUtil configUtil;
    private final HashMap<UUID, Boolean> onlineStatus = new HashMap<>();
    private final BarrierManager barrierManager; // ссылка на BarrierManager
    // private final DatabaseManager databaseManager;
    private DbPlayerData dbPlayerData;

    // Хранение задач для отображения температуры
    private final HashMap<UUID, BukkitTask> temperatureDisplayTasks = new HashMap<>();
    public PlayerListener(Plugin plugin, DbPlayerData dbPlayerData, BarrierManager barrierManager) {
        this.plugin = plugin;
        this.barrierManager = barrierManager;
        this.dbPlayerData = dbPlayerData;
        startPlayerPositionChecker();
        // this.configUtil = new ConfigUtil(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Проверяем зашёл ли игрок впервый раз на сервер
        if (!dbPlayerData.checkPlayerExists(player)) {
            plugin.getLogger().info(player.getName().toString() + " temperature: " + 268.00 + "°Kelvin" + " Player First Join To Server. Saved to DB.");
            this.updatePlayerTemperature(player, 268.00);
            dbPlayerData.savePlayerData(player.getUniqueId().toString(), player.getName(), 0, 0, 0, 0);
        }
        plugin.getLogger().info("Player " + player.getName() + " on db.");

        // configUtil.updatePlayerPosition(player);
        onlineStatus.put(playerId, true);
        // playerPositions.put(playerId, new PlayerPosition(
        //         player.getLocation().getX(),
        //         player.getLocation().getY(),
        //         player.getLocation().getZ()
        // ));
        dbPlayerData.updatePlayerPosition(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    }


    public void startPlayerPositionChecker() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();
                    Location playerLocation = player.getLocation();
                    double newX = playerLocation.getX();
                    double newY = playerLocation.getY();
                    double newZ = playerLocation.getZ();
    
                    double roundedX = Math.round(newX * 100.0) / 100.0;
                    double roundedY = Math.round(newY * 100.0) / 100.0;
                    double roundedZ = Math.round(newZ * 100.0) / 100.0;
                    
                    PlayerPosition playerPosition = PlayerListener.this.getPlayerPosition(player.getUniqueId());
                    if (playerPosition != null) {
                        player.sendMessage(null, "HASHMAP -> X: " + playerPosition.getX() + " Y: " + playerPosition.getY() + " Z: " + playerPosition.getZ());
                    }
                    // else {
                    //     player.sendMessage(null, "Player position not found for player " + player.getName());
                    // }
                    double temperature = dbPlayerData.getPlayerTemperature(playerId);
                    player.sendMessage("Player " + player.getName() + " temperature: " + temperature + " °Kelvin");

                    dbPlayerData.updatePlayerPosition(player, roundedX, roundedY, roundedZ);
                    player.sendMessage(null, "X: " + roundedX + " Y: " + roundedY + " Z: " + roundedZ);
                }
            }
        }, 0L, 5L);
    }


    // @EventHandler
    // public void onPlayerMove(PlayerMoveEvent event) {
    //     Player player = event.getPlayer();
    //     UUID playerId = player.getUniqueId();

    //     // Получаем текущие координаты игрока
    //     Location playerLocation = player.getLocation();
    //     double newX = playerLocation.getX();
    //     double newY = playerLocation.getY();
    //     double newZ = playerLocation.getZ();

    //     // Округляем координаты
    //     double roundedX = Math.round(newX * 100.0) / 100.0;
    //     double roundedY = Math.round(newY * 100.0) / 100.0;
    //     double roundedZ = Math.round(newZ * 100.0) / 100.0;

    //     // Обновляем позицию игрока
    //     playerPositions.put(playerId, new PlayerPosition(roundedX, roundedY, roundedZ));

    //     // Проверяем, находится ли игрок за границей барьера и обрабатываем его позицию
    //     // barrierManager.handlePlayerPosition(player);
    // }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // configUtil.updatePlayerPosition(player);

        double currentTemperature = getPlayerTemperature(playerId);
        dbPlayerData.savePlayerData(playerId.toString(), player.getName(), currentTemperature, 0, 0, 0);

        // Остановка отображения температуры, если оно активно
        stopTemperatureDisplay(playerId);
        onlineStatus.put(playerId, false);
    }

    public double getPlayerTemperature(UUID playerId) {
        Double temperature = dbPlayerData.getPlayerTemperature(playerId.toString());
        return temperature;
    }

    public void updatePlayerTemperature(Player player, double temperature) {
        dbPlayerData.updatePlayerTemperature(player, temperature);
    }
    public boolean isPlayerOnline(UUID playerId) {
        return onlineStatus.getOrDefault(playerId, false);
    }

    public PlayerPosition getPlayerPosition(UUID playerId) {
        return dbPlayerData.getPlayerPosition(playerId);
    }

    // Метод для начала отображения температуры
    public void startTemperatureDisplay(UUID playerId, BukkitTask task) {
        temperatureDisplayTasks.put(playerId, task);
    }

    // Метод для остановки отображения температуры
    public void stopTemperatureDisplay(UUID playerId) {
        BukkitTask task = temperatureDisplayTasks.remove(playerId);
        if (task != null) {
            task.cancel(); // Останавливаем таск
        }
    }

    public static class PlayerPosition {
        private final double x;
        private final double y;
        private final double z;

        public PlayerPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
