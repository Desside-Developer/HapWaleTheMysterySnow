package org.mechSnow.sTLPluginHWChanges.managers;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.mechSnow.sTLPluginHWChanges.db.DbPlayerData;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class TemperatureManager implements Listener {

    private final Map<UUID, Double> timeModify = new HashMap<>();
    private final Map<UUID, Double> blockModify = new HashMap<>();
    private final Map<UUID, Double> weatherModify = new HashMap<>();
    private final Map<UUID, Double> clothModify = new HashMap<>();
    private final Map<UUID, Double> barrierModify = new HashMap<>();
    private final Map<UUID, Double> shelterModify = new HashMap<>();
    private final Map<UUID, Double> heightModify = new HashMap<>();

    
    private final Map<String, Double> tickrateValues = new HashMap<>();
    private final Map<UUID, Map<String, Double>> blockValues = new HashMap<>();
    private final Map<UUID, Map<String, Double>> weatherValues = new HashMap<>();
    private final Map<UUID, Map<String, Double>> clothValues = new HashMap<>();
    private final Map<UUID, Map<String, Double>> barrierValues = new HashMap<>();
    private final Map<UUID, Map<String, Double>> heightValues = new HashMap<>();
    private final Map<String, Double> shelterValues = new HashMap<>();



    private final Plugin plugin;
    private final DbPlayerData dbPlayerData;
    private final PlayerListener playerListener;

    public TemperatureManager(Plugin plugin, DbPlayerData dbPlayerData, PlayerListener playerListener) {
        this.playerListener = playerListener;
        this.dbPlayerData = dbPlayerData;
        this.plugin = plugin;
        // plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }




    public void setTickrateValues(double priority, double speed, int targetTemp) {
        tickrateValues.put("priority", priority);
        tickrateValues.put("speed", speed);
        tickrateValues.put("targetTemp", (double) targetTemp);
    }

    public void setBlockValues(UUID playerId, double priority, double speed, int targetTemp) {
        Map<String, Double> values = blockValues.computeIfAbsent(playerId, k -> new HashMap<>());
        values.put("priority", priority);
        values.put("speed", speed);
        values.put("targetTemp", (double) targetTemp);
    }
    
    public void setWeatherValues(UUID playerId, double priority, double speed, int targetTemp) {
        Map<String, Double> values = weatherValues.computeIfAbsent(playerId, k -> new HashMap<>());
        values.put("priority", priority);
        values.put("speed", speed);
        values.put("targetTemp", (double) targetTemp);
    }
    
    public void setClothValues(UUID playerId, double priority, double speed, int targetTemp) {
        Map<String, Double> values = clothValues.computeIfAbsent(playerId, k -> new HashMap<>());
        values.put("priority", priority);
        values.put("speed", speed);
        values.put("targetTemp", (double) targetTemp);
    }
    
    public void setBarrierValues(UUID playerId, double priority, double speed, int targetTemp) {
        Map<String, Double> values = barrierValues.computeIfAbsent(playerId, k -> new HashMap<>());
        values.put("priority", priority);
        values.put("speed", speed);
        values.put("targetTemp", (double) targetTemp);
    }
    
    public void setHeightValues(UUID playerId, double priority, double speed, int targetTemp) {
        Map<String, Double> values = heightValues.computeIfAbsent(playerId, k -> new HashMap<>());
        values.put("priority", priority);
        values.put("speed", speed);
        values.put("targetTemp", (double) targetTemp);
    }

    // public void setShelterValues(UUID playerId, double priority, double speed, int targetTemp) {
    //     values.put("priority", priority);
    //     values.put("speed", speed);
    //     values.put("targetTemp", (double) targetTemp);
    // }


    public double getTickrateValue(String key) {
        return tickrateValues.getOrDefault(key, 0.0);
    }

    public void applyTickrateValues() {
        double priority = tickrateValues.getOrDefault("priority", 1.0);
        double speed = tickrateValues.getOrDefault("speed", 1.0);
        double targetTemp = tickrateValues.getOrDefault("targetTemp", 268.0);
    
        plugin.getLogger().info("ControllerTickrate: Priority: " + priority + ", Speed: " + speed + ", Target Temperature: " + targetTemp);
    
        // Применение к каждому онлайн-игроку
        playerListener.getOnlinePlayers().forEach(player -> {
            UUID playerId = player.getUniqueId();
    
            // Получение текущей температуры игрока
            double currentTemp = getPlayerTemperature(playerId);
    
            // Вычисление новой температуры на основе приоритета, скорости и целевой температуры
            double deltaTemp = (targetTemp - currentTemp) * (speed / priority);
            double newTemp = currentTemp + deltaTemp;
    
            // Сохранение обновленной температуры
            updatePlayerTemperature(player, newTemp);
    
            // Логирование значений для каждого игрока
            plugin.getLogger().info("Player: " + player.getName() + ", Current Temperature: " + currentTemp +
                                    ", New Temperature: " + newTemp);
        });
    }
    
    // Метод для получения температуры игрока
    public double getPlayerTemperature(UUID playerId) {
        return dbPlayerData.getPlayerTemperature(playerId.toString());
    }
    
    // Метод для обновления температуры игрока
    public void updatePlayerTemperature(Player player, double newTemp) {
        dbPlayerData.updatePlayerTemperature(player, newTemp);
    }

    // Метод для обновления температуры игрока на основе данных из TickManager
//     public void updatePlayerTemperature(Player player, int baseTemperature, double speed, double priority) {
//         UUID playerId = player.getUniqueId();
// //        baseTemperatures.put(playerId, baseTemperature);
// //        previousTemperatures.put(playerId, baseTemperature);

//         int totalTemperature = getTotalTemperature(player);
//         player.sendMessage("Скорость изменения температуры: " + speed);
//         player.sendMessage("Приоритет изменения температуры: " + priority);
//         player.sendMessage("Ваша общая температура: " + totalTemperature + " К.");
//     }

//     // Метод для получения полной температуры игрока
//     private int getTotalTemperature(Player player) {
//         UUID playerId = player.getUniqueId();
//         return 1; // baseTemperatures.getOrDefault(playerId, 0);
//     }

//     // Метод для получения предыдущей температуры игрока
//     public int getPreviousTemperature(UUID playerId) {
//         return 1; // previousTemperatures.getOrDefault(playerId, 1000);
//     }


//     public void TemperatureTracker(Player player, Double playerTemperature) {
//         List<Factor> factors = new ArrayList<>();

//         factors.add(updateTemperatureTick(player));
//         factors.add(updateWeatherTemperature(player));
//         factors.add(updateBarrierTemperature(player));
//         factors.add(updateBlockTemperature(player));
//         factors.add(updateShelterTemperature(player));
//         factors.add(updateAltitudeTemperature(player));
//         factors.add(updateClothingTemperature(player));

//         // Рассчитываем новую температуру
//         double newTemperature = calculateTemperature(player.getUniqueId(), playerTemperature, factors);

//         // Логика применения новой температуры и возможных эффектов
//         applyTemperatureEffects(player, newTemperature);
//     }

//     private Factor updateTemperatureTick(Player player) {
//         // Логика для получения температуры на основе времени дня
//         double targetTemp = 1;  // Установите целевую температуру на основе времени
//         double speed = 1;       // Установите скорость воздействия
//         int priority = 5;         // Пример приоритета для времени дня

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateWeatherTemperature(Player player) {
//         // Логика для получения температуры на основе текущей погоды
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 6;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateBarrierTemperature(Player player) {
//         // Логика для получения температуры на основе барьера
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 10;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateBlockTemperature(Player player) {
//         // Логика для получения температуры от блоков вокруг
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 7;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateShelterTemperature(Player player) {
//         // Логика для получения температуры в зависимости от нахождения в помещении
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 4;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateAltitudeTemperature(Player player) {
//         // Логика для получения температуры в зависимости от высоты
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 3;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private Factor updateClothingTemperature(Player player) {
//         // Логика для получения температуры в зависимости от одежды
//         double targetTemp = 1;
//         double speed = 1;
//         int priority = 8;

//         return new Factor(priority, speed, targetTemp);
//     }

//     private double calculateTemperature(UUID playerId, double currentTemperature, List<Factor> factors) {
//         // Сортируем факторы по приоритету (высший приоритет - первыми)
//         factors.sort((f1, f2) -> Integer.compare(f2.getPriority(), f1.getPriority()));

//         for (Factor factor : factors) {
//             double targetTemp = factor.getTargetTemperature();
//             double speed = factor.getSpeed();
//             int priority = factor.getPriority();

//             // Усиливаем скорость с учетом приоритета
//             double effectiveSpeed = speed + (priority * PRIORITY_INFLUENCE);
//             effectiveSpeed = Math.min(effectiveSpeed, 1.0);

//             // Рассчитываем изменение температуры с учетом усиленной скорости
//             double temperatureDelta = (targetTemp - currentTemperature) * effectiveSpeed;
//             currentTemperature += temperatureDelta;
//         }

//         // Ограничиваем итоговую температуру в пределах допустимых значений
//         return Math.max(MIN_TEMPERATURE, Math.min(MAX_TEMPERATURE, currentTemperature));
//     }

//     private void applyTemperatureEffects(Player player, double temperature) {
//         // Применение эффектов к игроку на основе температуры
//         // (например, дебаффы или специальные эффекты при экстремальных температурах)
//         // ...
//     }

//     // Класс для представления факторов температуры
//     class Factor {
//         private int priority;        // Приоритет фактора
//         private double speed;        // Базовая скорость воздействия
//         private double targetTemperature; // Целевая температура

//         public Factor(int priority, double speed, double targetTemperature) {
//             this.priority = priority;
//             this.speed = speed;
//             this.targetTemperature = targetTemperature;
//         }

//         public int getPriority() {
//             return priority;
//         }

//         public double getSpeed() {
//             return speed;
//         }

//         public double getTargetTemperature() {
//             return targetTemperature;
//         }
//     }
}