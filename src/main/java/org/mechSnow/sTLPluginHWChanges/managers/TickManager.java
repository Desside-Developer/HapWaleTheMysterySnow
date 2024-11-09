// package org.mechSnow.sTLPluginHWChanges.managers;

// import org.bukkit.Bukkit;
// import org.bukkit.World;
// import org.bukkit.entity.Player;
// import org.bukkit.plugin.java.JavaPlugin;
// import org.bukkit.scheduler.BukkitRunnable;

// import java.util.Random;
// import java.util.UUID;

// public class TickManager {
//     private final TemperatureManager temperatureManager;
//     private final JavaPlugin plugin;
//     private final Random random = new Random();

//     public TickManager(JavaPlugin plugin, TemperatureManager temperatureManager) {
//         this.plugin = plugin;
//         this.temperatureManager = temperatureManager;
//         startTickTracking();  // Начинаем отслеживание тиков сразу при создании объекта
//     }

//     // Запускаем отслеживание тиков и вызываем обновление температуры для игроков
//     private void startTickTracking() {
//         new BukkitRunnable() {
//             @Override
//             public void run() {
//                 World world = Bukkit.getWorld("world");
//                 if (world != null) {
//                     long currentTick = world.getTime(); // Получаем текущее количество тиков в сутках
//                     updateTemperatureForAllPlayers(currentTick); // Обновляем температуру для всех игроков
//                 }
//             }
//         }.runTaskTimer(plugin, 0L, 20L); // Запускаем задачу на выполнение каждую секунду (20 тиков)
//     }

//     // Метод для обновления температуры для всех игроков
//     private void updateTemperatureForAllPlayers(long currentTick) {
//         for (Player player : Bukkit.getOnlinePlayers()) {
//             UUID playerId = player.getUniqueId();
//             double speed = calculateTemperatureSpeed(currentTick);
//             double priority = calculateTemperaturePriority(currentTick);
//             int baseTemperature = calculateBaseTemperature(currentTick, playerId);

//             // Передаем обновленные параметры в ListenerTemperature для обновления у игрока
//             temperatureManager.updatePlayerTemperature(player, baseTemperature, speed, priority);
//         }
//     }

//     // Рассчитывает базовую температуру в зависимости от текущего тика
//     private int calculateBaseTemperature(long currentTick, UUID playerId) {
//         int baseTemperature;
//         if (currentTick < 13000) {
//             baseTemperature = 150;
//         } else if (currentTick >= 13000 && currentTick <= 18000) {
//             int progress = (int) (currentTick - 13000);
//             baseTemperature = 200 + (int) ((1300.0 / 5000) * progress);
//         } else if (currentTick > 18000 && currentTick <= 23000) {
//             baseTemperature = random.nextInt(501) + 1000;
//         } else {
//             double decreaseFactor = (currentTick - 23000) / 1000.0;
//             baseTemperature = (int) (temperatureManager.getPreviousTemperature(playerId) - (temperatureManager.getPreviousTemperature(playerId) - 200) * decreaseFactor);
//         }
//         return baseTemperature;
//     }

//     // Рассчитывает скорость изменения температуры
//     private double calculateTemperatureSpeed(long currentTick) {
//         if (currentTick < 13000) {
//             return 0.2;
//         } else if (currentTick >= 13000 && currentTick <= 18000) {
//             double progress = (currentTick - 13000) / 5000.0;
//             return 0.2 + (0.3 * progress);
//         } else {
//             return 0.5;
//         }
//     }

//     // Рассчитывает приоритет изменения температуры
//     private double calculateTemperaturePriority(long currentTick) {
//         if (currentTick < 18000) {
//             return 5.0;
//         } else {
//             return 5.5;
//         }
//     }
// }
