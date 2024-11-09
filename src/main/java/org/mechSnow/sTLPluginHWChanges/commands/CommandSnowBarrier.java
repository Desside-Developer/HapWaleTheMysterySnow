package org.mechSnow.sTLPluginHWChanges.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Optional;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("snowBarrier")
public class CommandSnowBarrier extends BaseCommand {
    private final Plugin plugin;

    public CommandSnowBarrier(Plugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Используйте: /snowBarrier <add|remove|update|list>")
    public void onDefault(CommandSender sender) {
        sender.sendMessage("Используйте: /snowBarrier <add|remove|update|list>");
    }

    @Subcommand("add")
    @Syntax("<x> <y> <z> <radius> <name>")
    @Description("Добавить новый барьер")
    @CommandCompletion("@coordinates @range:1-100 @string")
    public void addBarrier(Player player,
            @Optional int x,
            @Optional int y,
            @Optional int z,
            @Optional int radius,
            @Optional String name) {
        if (Objects.nonNull(x) && Objects.nonNull(y) && Objects.nonNull(z) && Objects.nonNull(radius) && Objects.nonNull(name)) {
            Location barrierCenter = new Location(player.getWorld(), x, y, z);
            // Здесь будет логика для добавления барьера
            player.sendMessage("Добавлен барьер с именем: " + name + " с радиусом: " + radius);
        } else {
            player.sendMessage("Недостаточно аргументов");
        }
    }

    @Subcommand("remove")
    @Syntax("<name>")
    @Description("Удалить барьер по имени")
    public void removeBarrier(Player player, @Optional String name) {
        if (name != null) {
            // Здесь будет логика для удаления барьера
            player.sendMessage("Удалён барьер с именем: " + name);
        } else {
            player.sendMessage("Недостаточно аргументов");
        }
    }

    @Subcommand("update")
    @Syntax("<name> <newValue>")
    @Description("Обновить барьер")
    public void updateBarrier(Player player,
            @Optional String name,
            @Optional String newValue) {
        if (name != null && newValue != null) {
            // Здесь будет логика для обновления барьера
            player.sendMessage("Обновлён барьер с именем: " + name + " до значения: " + newValue);
        } else {
            player.sendMessage("Недостаточно аргументов");
        }
    }

    @Subcommand("list")
    @Description("Показать список барьеров")
    public void listBarriers(Player player) {
        // Здесь будет логика для отображения списка барьеров
        player.sendMessage("Список барьеров: ..."); // Пример
    }

    // Опционально можно добавить метод помощи
    @Subcommand("help")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}