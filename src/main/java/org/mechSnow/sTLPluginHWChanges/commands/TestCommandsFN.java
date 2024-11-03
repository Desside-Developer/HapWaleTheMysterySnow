package org.mechSnow.sTLPluginHWChanges.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.mechSnow.sTLPluginHWChanges.utils.RegisterCommandsUtil;

public class TestCommandsFN implements CommandExecutor {
    private final Plugin plugin;

    public TestCommandsFN(Plugin plugin) {
        this.plugin = plugin;
    }

    public static void registerCommands(Plugin plugin) {
        RegisterCommandsUtil.registerCommand(plugin, "testCMFN", new TestCommandsFN(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("admin")) {
            player.sendMessage("У вас нет доступа к этой команде.");
            return true;
        }
        openAdminMenu(player);
        return true;
    }

    private void openAdminMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Меню Админа");

        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Бриллиант");
            item.setItemMeta(meta);
        }
        inventory.setItem(0, item);

        player.openInventory(inventory);
    }
}
