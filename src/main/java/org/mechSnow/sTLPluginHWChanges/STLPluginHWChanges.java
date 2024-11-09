package org.mechSnow.sTLPluginHWChanges;

import org.bukkit.plugin.Plugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.mechSnow.sTLPluginHWChanges.commands.GetTemperatureCommand;
import org.mechSnow.sTLPluginHWChanges.commands.TestCommandsFN;
import org.mechSnow.sTLPluginHWChanges.controllers.ControllerTickrate;
import org.mechSnow.sTLPluginHWChanges.db.DbBarrierData;
import org.mechSnow.sTLPluginHWChanges.db.DbPlayerData;
import org.mechSnow.sTLPluginHWChanges.managers.BarrierManager;
import org.mechSnow.sTLPluginHWChanges.commands.BarrierCommand;
import org.mechSnow.sTLPluginHWChanges.commands.CommandSnowBarrier;
import org.mechSnow.sTLPluginHWChanges.listeners.IceDropListener;
import org.mechSnow.sTLPluginHWChanges.listeners.OnLavaPlaceListener;
import org.mechSnow.sTLPluginHWChanges.listeners.OnWaterPlaceListener;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;
import org.mechSnow.sTLPluginHWChanges.utils.ChunkLoadUtil;
import org.mechSnow.sTLPluginHWChanges.utils.ConfigUtil;
import org.mechSnow.sTLPluginHWChanges.utils.RegisterCommandsUtil;

import co.aikar.commands.BukkitCommandManager;

import org.mechSnow.sTLPluginHWChanges.managers.DatabaseManager;
import org.mechSnow.sTLPluginHWChanges.managers.TemperatureManager;


import java.util.Objects;

    public final class STLPluginHWChanges extends JavaPlugin {

        private BarrierManager barrierManager;
        private PlayerListener playerListener;
        private BukkitAudiences audiences;
        private BarrierCommand barrierCommand;
        private ConfigUtil configUtil;
        private DatabaseManager databaseManager;
        private ChunkLoadUtil chunkLoadUtil;
        private IceDropListener iceDropListener;
        private OnWaterPlaceListener onWaterPlaceListener;
        private OnLavaPlaceListener onLavaPlaceListener;
        private BukkitCommandManager commandManager;

        // Controllers
        private ControllerTickrate controllerTickrate;

        private DbPlayerData dbPlayerData;

        @Override
        public void onEnable() {
            databaseManager = new DatabaseManager(this);
            
            // dbBarrierData = new DbBarrierData(databaseManager); // Здесь ты инициализируешь DbBarrierData без BarrierManager
            
            audiences = BukkitAudiences.create(this);
            
            barrierManager = new BarrierManager(audiences, this);
            barrierManager.loadBarrierData();
            barrierCommand = new BarrierCommand(barrierManager);
            // chunkLoadUtil = new ChunkLoadUtil();
            iceDropListener = new IceDropListener(); 
            onWaterPlaceListener = new OnWaterPlaceListener();
            onLavaPlaceListener = new OnLavaPlaceListener();
            TestCommandsFN.registerCommands(this);

            // Databases ( here )
            DbPlayerData dbPlayerData = new DbPlayerData(databaseManager, playerListener);
            this.playerListener = new PlayerListener(this, dbPlayerData, barrierManager);



            registerListeners();
            controllersRuns(dbPlayerData, playerListener);
            registerCommands();

            getLogger().info("STLPluginHWChanges activate!");
        }

        @Override
        public void onDisable() {
            barrierManager.saveBarrierData();
            if (audiences != null) {
                audiences.close();
            }
            if (databaseManager != null) {
                databaseManager.close();
            }
            getLogger().info("STLPluginHWChanges disable!.");
        }

        private void registerListeners() {
            getServer().getPluginManager().registerEvents(playerListener, this);
            getServer().getPluginManager().registerEvents(iceDropListener, this);
            getServer().getPluginManager().registerEvents(onWaterPlaceListener, this);
            getServer().getPluginManager().registerEvents(onLavaPlaceListener, this);
        }

        private void registerCommands() {
            commandManager = new BukkitCommandManager(this);
            TestCommandsFN testCommandsFN = new TestCommandsFN(this);
            RegisterCommandsUtil.registerCommand(this, "testCMFN", testCommandsFN);
            CommandSnowBarrier commandSnowBarrier = new CommandSnowBarrier(this);
            commandManager.registerCommand(commandSnowBarrier);
            Objects.requireNonNull(getCommand("setCenter"), "Команда setCenter не найдена в plugin.yml")
            .setExecutor(barrierCommand);
            Objects.requireNonNull(getCommand("setRadius"), "Команда setRadius не найдена в plugin.yml")
            .setExecutor(barrierCommand);
        }

        private void controllersRuns(DbPlayerData dbPlayerData, PlayerListener playerListener) {
            TemperatureManager temperatureManager = new TemperatureManager(this, dbPlayerData, playerListener);

            new ControllerTickrate(temperatureManager, this);
        }

        //     Objects.requireNonNull(getCommand("getTemperature"), "Команда getTemperature не найдена в plugin.yml")
        //             .setExecutor(new GetTemperatureCommand(playerListener, audiences, this));

        //     Objects.requireNonNull(getCommand("testCMFN"), "Команда testCMFN не найдена в plugin.yml")
        //             .setExecutor(new TestCommandsFN(this, playerListener));

        //     Objects.requireNonNull(getCommand("expandRadius"), "Команда expandRadius не найдена в plugin.yml")
        //             .setExecutor(barrierCommand);
}
