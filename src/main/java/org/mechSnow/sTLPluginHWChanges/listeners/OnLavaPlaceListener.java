package org.mechSnow.sTLPluginHWChanges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mechSnow.sTLPluginHWChanges.STLPluginHWChanges;

public class OnLavaPlaceListener extends ChunkGenerator implements Listener {
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();

        if (bucket == Material.LAVA_BUCKET) {
            Block lavaBlock = event.getBlockClicked().getRelative(event.getBlockFace());

            // Check if the world is the Overworld
            if (event.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (lavaBlock.getType() == Material.LAVA) {
                            int y = lavaBlock.getY();
                            Bukkit.getLogger().info("Player " + event.getPlayer().getName() + " used lava bucket at coordinates (" + lavaBlock.getX() + ", " + y + ", " + lavaBlock.getZ() + ")");

                            lavaBlock.setType(Material.OBSIDIAN);
                            Bukkit.getLogger().info("Converted lava to obsidian at coordinates (" + lavaBlock.getX() + ", " + y + ", " + lavaBlock.getZ() + ")");
                        }
                    }
                }.runTaskLater(JavaPlugin.getPlugin(STLPluginHWChanges.class), 1L); // Выполнение с задержкой в 1 тик
            }
        }
        Bukkit.getLogger().info("PlayerBucketEmptyEvent finished");
    }
}
