package org.mechSnow.sTLPluginHWChanges.commands;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;

import java.util.HashMap;
import java.util.UUID;

public class GetTemperatureCommand implements CommandExecutor {
    private final PlayerListener playerListener;
    private final BukkitAudiences adventure;
    private final Plugin plugin;
    private final long updateInterval = 20L; // Интервал обновления в тиках (20 тиков = 1 секунда)

    // Хранение состояния отображения температуры
    private final HashMap<UUID, BukkitRunnable> tasks = new HashMap<>();

    public GetTemperatureCommand(PlayerListener playerListener, BukkitAudiences adventure, Plugin plugin) {
        this.playerListener = playerListener;
        this.adventure = adventure;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        // Проверяем, является ли игрок оператором
        if (!player.isOp()) {
            player.sendMessage("У вас нет прав для использования этой команды.");
            return true;
        }

        // Если задача уже существует, останавливаем её
        if (tasks.containsKey(playerId)) {
            stopTemperatureDisplay(playerId);
            player.sendMessage("Отображение температуры отключено.");
        } else {
            startTemperatureDisplay(playerId);
            player.sendMessage("Отображение температуры включено.");
        }

        return true;
    }

    private void startTemperatureDisplay(UUID playerId) {
        // Создаем новую задачу для отображения температуры
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPlayer(playerId) == null) {
                    // Если игрок вышел, останавливаем задачу
                    stopTemperatureDisplay(playerId);
                    return;
                }

                Player player = Bukkit.getPlayer(playerId);
                // Получаем температуру игрока
                double temperature = playerListener.getPlayerTemperature(playerId);

                // Создаем текст для action bar с температурой
                Component temperatureText = Component.text("Температура: ")
                        .color(NamedTextColor.YELLOW)
                        .append(Component.text(temperature + " K")
                                .color(TextColor.color(0x00ADEF))
                                .decorate(TextDecoration.BOLD)
                        );

                // Отправляем текст на action bar игрока
                adventure.player(player).sendActionBar(temperatureText);
            }
        };

        // Запускаем задачу с обновлением каждую секунду
        task.runTaskTimer(plugin, 0, updateInterval);
        tasks.put(playerId, task); // Сохраняем задачу
    }

    private void stopTemperatureDisplay(UUID playerId) {
        BukkitRunnable task = tasks.remove(playerId);
        if (task != null) {
            task.cancel(); // Останавливаем задачу
        }
    }
}
