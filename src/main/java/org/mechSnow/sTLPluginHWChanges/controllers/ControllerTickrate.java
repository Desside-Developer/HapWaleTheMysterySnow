package org.mechSnow.sTLPluginHWChanges.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mechSnow.sTLPluginHWChanges.managers.TemperatureManager;

public class ControllerTickrate {
    private final Plugin plugin;
    private final Random random = new Random();
    private final TemperatureManager temperatureManager;

    public ControllerTickrate(TemperatureManager temperatureManager, Plugin plugin) {
        this.temperatureManager = temperatureManager;
        this.plugin = plugin;
        plugin.getLogger().info("ControllerTickrate initialized");
        startTrackingTickrate();
    }
    private void startTrackingTickrate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                if (world != null) {
                    long currentTick = world.getTime();
                    updateTemperature(currentTick);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateTemperature(long currentTick) {
        double timeOfDay = calculateTimeOfDay(currentTick);
        double priority = calculatePriority(timeOfDay);
        double speed = calculateSpeed(timeOfDay);
        int targetTemp = calculateTargetTemp(timeOfDay);

        temperatureManager.setTickrateValues(priority, speed, targetTemp);
    }

    private double calculateTimeOfDay(long currentTick) {
        return (currentTick % 24000) / 24000.0;
    }

    private double calculatePriority(double timeOfDay) {
        if (timeOfDay < 0.5) {
            return 3.0;
        } else if (timeOfDay < 0.8) {
            return 7.0;
        } else {
            return 14.0;
        }
    }

    private double calculateSpeed(double timeOfDay) {
        if (timeOfDay < 0.5) {
            return 0.3;
        } else if (timeOfDay < 0.8) {
            return 0.7;
        } else {
            return 2.0;
        }
    }

    private int calculateTargetTemp(double timeOfDay) {
        if (timeOfDay < 0.5) {
            return 250;
        } else if (timeOfDay < 0.8) {
            return 233;
        } else {
            return 200;
        }
    }

}
