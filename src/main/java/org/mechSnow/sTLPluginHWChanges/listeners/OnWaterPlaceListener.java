package org.mechSnow.sTLPluginHWChanges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mechSnow.sTLPluginHWChanges.STLPluginHWChanges;

public class OnWaterPlaceListener implements Listener {
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();

        Bukkit.getLogger().info("PlayerBucketEmptyEvent triggered");
        Bukkit.getLogger().info("Bucket type: " + bucket);
        Bukkit.getLogger().info("Block type clicked: " + event.getBlockClicked().getType());
        Bukkit.getLogger().info("Block Y level: " + event.getBlockClicked().getY());

        if (bucket == Material.WATER_BUCKET) {
            Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());

            new BukkitRunnable() {
                @Override
                public void run() {
                    // Проверка, что блок действительно стал водой
                    if (waterBlock.getType() == Material.WATER) {
                        int y = waterBlock.getY();
                        Bukkit.getLogger().info("Player " + event.getPlayer().getName() + " used water bucket at coordinates (" + waterBlock.getX() + ", " + y + ", " + waterBlock.getZ() + ")");

                        if (y >= 62) {
                            waterBlock.setType(Material.BLUE_ICE);
                            Bukkit.getLogger().info("Converted water to blue ice at coordinates (" + waterBlock.getX() + ", " + y + ", " + waterBlock.getZ() + ")");
                        } else if (y >= 0) {
                            waterBlock.setType(Material.PACKED_ICE);
                            Bukkit.getLogger().info("Converted water to packed ice at coordinates (" + waterBlock.getX() + ", " + y + ", " + waterBlock.getZ() + ")");
                        } else {
                            waterBlock.setType(Material.ICE);
                            Bukkit.getLogger().info("Converted water to ice at coordinates (" + waterBlock.getX() + ", " + y + ", " + waterBlock.getZ() + ")");
                        }
                    }
                }
            }.runTaskLater(JavaPlugin.getPlugin(STLPluginHWChanges.class), 1L); // Выполнение с задержкой в 1 тик
        }
        Bukkit.getLogger().info("PlayerBucketEmptyEvent finished");
    }
}
