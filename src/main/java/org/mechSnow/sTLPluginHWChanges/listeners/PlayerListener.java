package org.mechSnow.sTLPluginHWChanges.listeners;

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
import org.bukkit.scheduler.BukkitTask;

public class PlayerListener implements Listener {
    private final Plugin plugin;
    private final ConfigUtil configUtil;
    private final HashMap<UUID, PlayerPosition> playerPositions = new HashMap<>();
    private final HashMap<UUID, Boolean> onlineStatus = new HashMap<>();
    private final HashMap<UUID, Double> playerTemperature = new HashMap<>();
    private final BarrierManager barrierManager; // ссылка на BarrierManager
    // private final DatabaseManager databaseManager;
    private DbPlayerData dbPlayerData;

    // Хранение задач для отображения температуры
    private final HashMap<UUID, BukkitTask> temperatureDisplayTasks = new HashMap<>();
    public PlayerListener(Plugin plugin, DbPlayerData dbPlayerData, BarrierManager barrierManager) {
        this.plugin = plugin;
        this.barrierManager = barrierManager;
        this.dbPlayerData = dbPlayerData;
        this.configUtil = new ConfigUtil(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Проверяем данные игрока и создаем, если необходимо

        // Обновляем данные при входе
        configUtil.updatePlayerPosition(player);
        onlineStatus.put(playerId, true);
        playerPositions.put(playerId, new PlayerPosition(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()
        ));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Получаем текущие координаты игрока
        Location playerLocation = player.getLocation();
        double newX = playerLocation.getX();
        double newY = playerLocation.getY();
        double newZ = playerLocation.getZ();

        // Округляем координаты
        double roundedX = Math.round(newX * 100.0) / 100.0;
        double roundedY = Math.round(newY * 100.0) / 100.0;
        double roundedZ = Math.round(newZ * 100.0) / 100.0;

        // Обновляем позицию игрока
        playerPositions.put(playerId, new PlayerPosition(roundedX, roundedY, roundedZ));

        // Проверяем, находится ли игрок за границей барьера и обрабатываем его позицию
        barrierManager.handlePlayerPosition(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Проверяем данные игрока и создаем, если необходимо

        // Обновляем данные перед выходом
        configUtil.updatePlayerPosition(player);

        // Сохранение текущей температуры перед выходом
        double currentTemperature = getPlayerTemperature(playerId);
        // databaseManager.savePlayerData(playerId.toString(), player.getName(), currentTemperature);
        dbPlayerData.savePlayerData(playerId.toString(), player.getName(), currentTemperature, 0, 0, 0);
        configUtil.updatePlayerTemperature(player, currentTemperature);

        // Остановка отображения температуры, если оно активно
        stopTemperatureDisplay(playerId);
        onlineStatus.put(playerId, false);
    }

    // Метод получения температуры игрока
    public double getPlayerTemperature(UUID playerId) {
        return playerTemperature.getOrDefault(playerId, 268.15); // Возвращаем базовую температуру по умолчанию
    }

    // Метод обновления температуры игрока в хэшмапе и конфиге
    public void updatePlayerTemperature(Player player, double temperature) {
        UUID playerId = player.getUniqueId();

        // Обновление температуры в playerTemperature HashMap
        playerTemperature.put(playerId, temperature);

        // Обновление температуры в конфиге
        configUtil.updatePlayerTemperature(player, temperature);
    }

    public boolean isPlayerOnline(UUID playerId) {
        return onlineStatus.getOrDefault(playerId, false);
    }

    public PlayerPosition getPlayerPosition(UUID playerId) {
        return playerPositions.get(playerId);
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
