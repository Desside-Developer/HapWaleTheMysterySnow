package org.mechSnow.sTLPluginHWChanges;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.mechSnow.sTLPluginHWChanges.commands.GetTemperatureCommand;
import org.mechSnow.sTLPluginHWChanges.commands.TestCommandsFN;
import org.mechSnow.sTLPluginHWChanges.managers.BarrierManager;
import org.mechSnow.sTLPluginHWChanges.commands.BarrierCommand;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;

public final class STLPluginHWChanges extends JavaPlugin {
    private BarrierManager barrierManager; // Менеджер барьеров
    private PlayerListener playerListener; // Слушатель игроков
    private BukkitAudiences audiences; // Для отправки сообщений

    @Override
    public void onEnable() {
        // Инициализация BukkitAudiences
        audiences = BukkitAudiences.create(this); // Передаем текущий экземпляр плагина

        // Инициализация BarrierManager
        barrierManager = new BarrierManager(audiences, this); // Инициализируем поле класса

        // Инициализация и регистрация PlayerListener
        playerListener = new PlayerListener(this, barrierManager);
        getServer().getPluginManager().registerEvents(playerListener, this);

        // Регистрация команд
        getCommand("getTemperature").setExecutor(new GetTemperatureCommand(playerListener, audiences, this));
        getCommand("testCMFN").setExecutor(new TestCommandsFN(playerListener));
        getCommand("setcenter").setExecutor(new BarrierCommand(barrierManager));
        getCommand("setradius").setExecutor(new BarrierCommand(barrierManager));
        getCommand("expandradius").setExecutor(new BarrierCommand(barrierManager));

        getLogger().info("STLPluginHWChanges успешно активирован!");
    }

    @Override
    public void onDisable() {
        audiences.close(); // Закрываем BukkitAudiences при отключении плагина
        getLogger().info("STLPluginHWChanges отключен.");
    }
}
