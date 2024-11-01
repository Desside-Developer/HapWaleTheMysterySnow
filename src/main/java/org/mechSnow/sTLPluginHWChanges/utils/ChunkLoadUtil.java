package org.mechSnow.sTLPluginHWChanges.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadUtil implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 62; y < event.getWorld().getMaxHeight(); y++) {
                    Block block = event.getChunk().getBlock(x, y, z);
                    if (block.getType() == Material.WATER && block.getLightFromSky() >= 15) {
                        block.setType(Material.ICE);
                    }
                }
            }
        }
    }
}
