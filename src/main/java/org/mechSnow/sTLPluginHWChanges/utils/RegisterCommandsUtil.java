package org.mechSnow.sTLPluginHWChanges.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;


public class RegisterCommandsUtil {
    public static void registerCommand(Plugin plugin, String commandName, CommandExecutor executor) {
        plugin.getServer().getPluginCommand(commandName).setExecutor(executor);
    }
}