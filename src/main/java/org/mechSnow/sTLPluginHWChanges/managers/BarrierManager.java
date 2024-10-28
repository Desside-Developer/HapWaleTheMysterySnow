package org.mechSnow.sTLPluginHWChanges.managers;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class BarrierManager {
    private Location center; // Центральная точка барьера
    private double radius; // Радиус барьера
    private final HashMap<UUID, Integer> frozenPlayers = new HashMap<>(); // Счетчик замерзания
    private final HashMap<UUID, Boolean> warnedPlayers = new HashMap<>(); // Статус предупреждения игроков
    private final BukkitAudiences audiences;
    private final Plugin plugin;

    // Конструктор класса
    public BarrierManager(BukkitAudiences audiences, Plugin plugin) {
        this.audiences = audiences; // Инициализация BukkitAudiences
        this.plugin = plugin;
    }

    public void setBarrierCenter(Location location) {
        this.center = location;
    }

    public void setBarrierRadius(double radius) {
        this.radius = radius;
    }

    public void expandBarrier(double additionalRadius) {
        this.radius += additionalRadius;
    }

    public boolean isPlayerOutsideBarrier(Location playerLocation) {
        if (center == null) return false;
        double distance = playerLocation.distance(center);
        return distance > radius; // Возвращает true, если игрок за границей
    }

    public void handlePlayerPosition(Player player) {
        if (center == null || radius <= 0) return;

        Location playerLocation = player.getLocation();
        double distance = playerLocation.distance(center);

        Bukkit.getScheduler().runTask(plugin, () -> {
            // Проверка на выход за границу
            if (distance > radius) {
                // Предупреждаем игрока о замерзании, если он еще не был предупрежден
                if (!warnedPlayers.getOrDefault(player.getUniqueId(), false)) {
                    warnedPlayers.put(player.getUniqueId(), true);
                    sendCenteredTitleMessage(player, "Что-то не так? Вы начали замерзать!");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1)); // 10 секунд замедления
                }

                // Логика замерзания
                int ticks = frozenPlayers.getOrDefault(player.getUniqueId(), 0);
                ticks++;

                if (ticks >= 60) { // Каждые 3 секунды (60 тиков)
                    if (player.getHealth() > 1) {
                        player.setHealth(player.getHealth() - 1); // Уменьшаем здоровье на 1
                        sendCenteredTitleMessage(player, "Вы теряете здоровье от замерзания!");
                    } else {
                        player.sendMessage("Вы погибли от замерзания!");
                        player.setHealth(0); // Игрок умирает
                    }
                    frozenPlayers.put(player.getUniqueId(), 0); // Сброс счетчика
                } else {
                    frozenPlayers.put(player.getUniqueId(), ticks);
                }
            } else {
                // Если игрок вернулся в безопасную зону, сбрасываем его статус
                warnedPlayers.remove(player.getUniqueId());
                frozenPlayers.remove(player.getUniqueId());
            }
        });
    }

//    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
//    public void EntityChangeBlockEvent(EntityChangeBlockEvent e) {
//        if(e.getEntity() instanceof Player) {
//            if(e.getBlock().getType().equals(Material.POWDER_SNOW)){
//                Player p = (Player)e.getEntity();
//                if (p.hasPermission("n2.snow")) {
//                    p.setFreezeTicks(0);
//                    p.sendMessage(":" + p.getFreezeTicks());
//                }
//            }
//        }
//    }

    // Метод для отправки сообщений в ActionBar
    private void sendCenteredTitleMessage(Player player, String message) {
        Audience audience = audiences.sender(player);
        Component title = Component.text(message).color(TextColor.fromHexString("#FF0000")); // Красный цвет
        Title.Times times = Title.Times.times(
                Duration.ofSeconds(1), // Задержка в 1 секунду
                Duration.ofSeconds(3), // Длительность в 3 секунды
                Duration.ofSeconds(1)  // Затухание в 1 секунду
        );
        audience.showTitle(Title.title(title, Component.empty(), times)); // Отправка Title сообщения
    }

    public void showBarrierParticles() {
        if (center == null || radius <= 0) return;
        double step = Math.PI / 8;
        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            double x = center.getX() + radius * Math.cos(theta);
            double z = center.getZ() + radius * Math.sin(theta);
            Location particleLocation = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1);
        }
    }
}
