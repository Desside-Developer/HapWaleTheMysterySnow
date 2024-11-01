package org.mechSnow.sTLPluginHWChanges.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class IceDropListener implements Listener {

    @EventHandler
    public void onIceBreak(BlockBreakEvent event) {
        // Проверка, что мир — обычный, и блок — лед, плотный или синий лед
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.NETHER && event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) {
            Material blockType = event.getBlock().getType();
            
            if (blockType == Material.ICE || blockType == Material.PACKED_ICE || blockType == Material.BLUE_ICE) {
                // Отменяем стандартное разрушение, чтобы избежать превращения в воду
                event.setCancelled(true);

                // Убираем блок и добавляем дроп соответствующего вида льда
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(blockType));
            }
        }
    }
}
