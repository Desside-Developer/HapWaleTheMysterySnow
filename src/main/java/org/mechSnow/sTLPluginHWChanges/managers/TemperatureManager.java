package org.mechSnow.sTLPluginHWChanges.managers;
import org.bukkit.event.Listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TemperatureManager implements Listener {

    private final Map<UUID, Integer> timeModify = new HashMap<>();
    private final Map<UUID, Integer> blockModify = new HashMap<>();
    private final Map<UUID, Integer> weatherModify = new HashMap<>();
    private final Map<UUID, Integer> clothModify = new HashMap<>();
    private final Map<UUID, Integer> barrierModify = new HashMap<>();
    private final Map<UUID, Integer> shelterModify = new HashMap<>();
    private final Map<UUID, Double> heightModify = new HashMap<>();

    private static final double MIN_TEMPERATURE = 173.15;
    private static final double MAX_TEMPERATURE = 323.15;
    private static final double PRIORITY_INFLUENCE = 0.1;

    private final Map<UUID, Integer> currentTemperature = new HashMap<>();

    public TemperatureManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Метод для обновления температуры игрока на основе данных из TickManager
    public void updatePlayerTemperature(Player player, int baseTemperature, double speed, double priority) {
        UUID playerId = player.getUniqueId();
//        baseTemperatures.put(playerId, baseTemperature);
//        previousTemperatures.put(playerId, baseTemperature);

        int totalTemperature = getTotalTemperature(player);
        player.sendMessage("Скорость изменения температуры: " + speed);
        player.sendMessage("Приоритет изменения температуры: " + priority);
        player.sendMessage("Ваша общая температура: " + totalTemperature + " К.");
    }

    // Метод для получения полной температуры игрока
    private int getTotalTemperature(Player player) {
        UUID playerId = player.getUniqueId();
        return 1; // baseTemperatures.getOrDefault(playerId, 0);
    }

    // Метод для получения предыдущей температуры игрока
    public int getPreviousTemperature(UUID playerId) {
        return 1; // previousTemperatures.getOrDefault(playerId, 1000);
    }


    public void TemperatureTracker(Player player, Double playerTemperature) {
        List<Factor> factors = new ArrayList<>();

        factors.add(updateTemperatureTick(player));
        factors.add(updateWeatherTemperature(player));
        factors.add(updateBarrierTemperature(player));
        factors.add(updateBlockTemperature(player));
        factors.add(updateShelterTemperature(player));
        factors.add(updateAltitudeTemperature(player));
        factors.add(updateClothingTemperature(player));

        // Рассчитываем новую температуру
        double newTemperature = calculateTemperature(player.getUniqueId(), playerTemperature, factors);

        // Логика применения новой температуры и возможных эффектов
        applyTemperatureEffects(player, newTemperature);
    }

    private Factor updateTemperatureTick(Player player) {
        // Логика для получения температуры на основе времени дня
        double targetTemp = 1;  // Установите целевую температуру на основе времени
        double speed = 1;       // Установите скорость воздействия
        int priority = 5;         // Пример приоритета для времени дня

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateWeatherTemperature(Player player) {
        // Логика для получения температуры на основе текущей погоды
        double targetTemp = 1;
        double speed = 1;
        int priority = 6;

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateBarrierTemperature(Player player) {
        // Логика для получения температуры на основе барьера
        double targetTemp = 1;
        double speed = 1;
        int priority = 10;

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateBlockTemperature(Player player) {
        // Логика для получения температуры от блоков вокруг
        double targetTemp = 1;
        double speed = 1;
        int priority = 7;

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateShelterTemperature(Player player) {
        // Логика для получения температуры в зависимости от нахождения в помещении
        double targetTemp = 1;
        double speed = 1;
        int priority = 4;

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateAltitudeTemperature(Player player) {
        // Логика для получения температуры в зависимости от высоты
        double targetTemp = 1;
        double speed = 1;
        int priority = 3;

        return new Factor(priority, speed, targetTemp);
    }

    private Factor updateClothingTemperature(Player player) {
        // Логика для получения температуры в зависимости от одежды
        double targetTemp = 1;
        double speed = 1;
        int priority = 8;

        return new Factor(priority, speed, targetTemp);
    }

    private double calculateTemperature(UUID playerId, double currentTemperature, List<Factor> factors) {
        // Сортируем факторы по приоритету (высший приоритет - первыми)
        factors.sort((f1, f2) -> Integer.compare(f2.getPriority(), f1.getPriority()));

        for (Factor factor : factors) {
            double targetTemp = factor.getTargetTemperature();
            double speed = factor.getSpeed();
            int priority = factor.getPriority();

            // Усиливаем скорость с учетом приоритета
            double effectiveSpeed = speed + (priority * PRIORITY_INFLUENCE);
            effectiveSpeed = Math.min(effectiveSpeed, 1.0);

            // Рассчитываем изменение температуры с учетом усиленной скорости
            double temperatureDelta = (targetTemp - currentTemperature) * effectiveSpeed;
            currentTemperature += temperatureDelta;
        }

        // Ограничиваем итоговую температуру в пределах допустимых значений
        return Math.max(MIN_TEMPERATURE, Math.min(MAX_TEMPERATURE, currentTemperature));
    }

    private void applyTemperatureEffects(Player player, double temperature) {
        // Применение эффектов к игроку на основе температуры
        // (например, дебаффы или специальные эффекты при экстремальных температурах)
        // ...
    }

    // Класс для представления факторов температуры
    class Factor {
        private int priority;        // Приоритет фактора
        private double speed;        // Базовая скорость воздействия
        private double targetTemperature; // Целевая температура

        public Factor(int priority, double speed, double targetTemperature) {
            this.priority = priority;
            this.speed = speed;
            this.targetTemperature = targetTemperature;
        }

        public int getPriority() {
            return priority;
        }

        public double getSpeed() {
            return speed;
        }

        public double getTargetTemperature() {
            return targetTemperature;
        }
    }
}