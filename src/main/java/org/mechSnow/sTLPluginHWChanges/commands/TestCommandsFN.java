package org.mechSnow.sTLPluginHWChanges.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mechSnow.sTLPluginHWChanges.listeners.PlayerListener;
import java.util.UUID;

public class TestCommandsFN implements CommandExecutor {
    private final PlayerListener playerListener;
    public TestCommandsFN(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        PlayerListener.PlayerPosition position = playerListener.getPlayerPosition(playerId);
        if (position != null) {
            player.sendMessage("Текущая позиция: X: " + position.getX() + ", Y: " + position.getY() + ", Z: " + position.getZ());
        } else {
            player.sendMessage("Позиция игрока не найдена.");
        }

        boolean isOnline = playerListener.isPlayerOnline(playerId);
        player.sendMessage("Статус игрока: " + (isOnline ? "онлайн" : "не в сети"));

        return true;
    }
}
