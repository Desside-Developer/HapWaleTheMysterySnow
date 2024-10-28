package org.mechSnow.sTLPluginHWChanges.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mechSnow.sTLPluginHWChanges.managers.BarrierManager;

public class BarrierCommand implements CommandExecutor {
    private final BarrierManager barrierManager;

    public BarrierCommand(BarrierManager barrierManager) {
        this.barrierManager = barrierManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (label.equalsIgnoreCase("setcenter")) {
            if (args.length == 0) {
                // Установка центра на текущей позиции игрока
                barrierManager.setBarrierCenter(player.getLocation());
                player.sendMessage("Центр барьера установлен на вашей позиции.");
            } else if (args.length == 3) {
                // Установка центра по указанным координатам
                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    Location location = new Location(player.getWorld(), x, y, z);
                    barrierManager.setBarrierCenter(location);
                    player.sendMessage("Центр барьера установлен на координатах X: " + x + ", Y: " + y + ", Z: " + z);
                } catch (NumberFormatException e) {
                    player.sendMessage("Неправильный формат координат. Используйте числа.");
                }
            } else {
                player.sendMessage("Использование: /setcenter [x] [y] [z]");
            }
        } else if (label.equalsIgnoreCase("setradius")) {
            if (args.length == 1) {
                try {
                    double radius = Double.parseDouble(args[0]);
                    barrierManager.setBarrierRadius(radius);
                    player.sendMessage("Радиус барьера установлен: " + radius);
                } catch (NumberFormatException e) {
                    player.sendMessage("Неправильный формат радиуса. Используйте число.");
                }
            } else {
                player.sendMessage("Использование: /setradius <радиус>");
            }
        } else if (label.equalsIgnoreCase("expandradius")) {
            if (args.length == 1) {
                try {
                    double additionalRadius = Double.parseDouble(args[0]);
                    barrierManager.expandBarrier(additionalRadius);
                    player.sendMessage("Радиус барьера увеличен на: " + additionalRadius);
                } catch (NumberFormatException e) {
                    player.sendMessage("Неправильный формат радиуса. Используйте число.");
                }
            } else {
                player.sendMessage("Использование: /expandradius <добавочный_радиус>");
            }
        }
        return true;
    }
}
