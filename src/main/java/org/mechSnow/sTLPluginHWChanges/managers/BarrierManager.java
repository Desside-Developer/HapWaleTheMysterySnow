package org.mechSnow.sTLPluginHWChanges.managers;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BarrierManager {
//  Данные важные
    private Location center; // Центральная точка барьера
    private double radius; // Радиус барьера


    private final HashMap<UUID, Integer> frozenPlayers = new HashMap<>(); // Счетчик замерзания
    private final HashMap<UUID, Boolean> warnedPlayers = new HashMap<>(); // Статус предупреждения игроков
    private HashMap<UUID, Long> lastEffectApplied = new HashMap<>();
    private HashMap<UUID, Long> lastBlindnessApplied = new HashMap<>();
    private Map<UUID, Long> behindBarrierTime = new HashMap<>();
    private final BukkitAudiences audiences;
    private final Plugin plugin;

    public BarrierManager(BukkitAudiences audiences, Plugin plugin) {
        this.audiences = audiences; // Инициализация BukkitAudiences
        this.plugin = plugin;
    }


    // Метод для сохранения данных барьера
    // Метод для сохранения данных барьера в config.yml
    public void saveBarrierData() {
        FileConfiguration config = plugin.getConfig();
        if (center != null) {
            config.set("barrier.center.world", center.getWorld().getName());
            config.set("barrier.center.x", center.getX());
            config.set("barrier.center.y", center.getY());
            config.set("barrier.center.z", center.getZ());
        }
        config.set("barrier.radius", radius);
        plugin.saveConfig();
    }

    // Метод для загрузки данных барьера из config.yml
    public void loadBarrierData() {
        FileConfiguration config = plugin.getConfig();
        String worldName = config.getString("barrier.center.world");
        
        if (worldName == null || worldName.isEmpty()) {
            // Если данных о центре нет, то задаем значения по умолчанию
            plugin.getLogger().info("Барьер не найден в конфигурации. Используются значения по умолчанию.");
            this.center = null; // или можете задать, например, defaultLocation
            this.radius = 0; // Радиус по умолчанию
            return;
        }
    
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().severe("Мир с именем " + worldName + " не существует!");
            return;
        }
    
        double x = config.getDouble("barrier.center.x", 0); // Если x не найдено, используем 0
        double y = config.getDouble("barrier.center.y", 0); // Если y не найдено, используем 0
        double z = config.getDouble("barrier.center.z", 0); // Если z не найдено, используем 0
    
        this.center = new Location(world, x, y, z);
        this.radius = config.getDouble("barrier.radius", 0);  // Если радиус не задан, будет 0
    
        plugin.getLogger().info("Барьер загружен: Центр - " + this.center.toString() + ", Радиус - " + this.radius);
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

    // public void handlePlayerPosition(Player player) {
    //     if (center == null || radius <= 0) return;
    
    //     Location playerLocation = player.getLocation();
    //     double distance = playerLocation.distance(center);
    
    //     Bukkit.getScheduler().runTask(plugin, () -> {
    //         if (distance > radius) {
    //             if (!warnedPlayers.getOrDefault(player.getUniqueId(), false)) {
    //                 warnedPlayers.put(player.getUniqueId(), true);
    //                 sendCenteredTitleMessage(player, "Что-то не так? Вы начали замерзать!");
    //                 player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1)); // 10 секунд замедления
    //             }
    
    //             int ticks = frozenPlayers.getOrDefault(player.getUniqueId(), 0);
    //             ticks++;
    
    //             if (ticks >= 1) { // Каждые 3 секунды (60 тиков)
    //                 if (player.getHealth() > 1) {
    //                     // Наносим урон с эффектом удара, используя метод damage()
    //                     player.damage(1.0); // Наносим 1 единицу урона
    //                     player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0F, 1.0F); // Звук удара
    //                     player.spawnParticle(Particle.CRIT, player.getLocation(), 10); // Эффект удара (критические частицы)
    
    //                     sendCenteredTitleMessage(player, "Вы теряете здоровье от замерзания!");
    //                 } else {
    //                     player.sendMessage("Вы погибли от замерзания!");
    //                     player.setHealth(0); // Игрок умирает
    //                 }
    //                 frozenPlayers.put(player.getUniqueId(), 0); // Сброс счетчика
    //             } else {
    //                 frozenPlayers.put(player.getUniqueId(), ticks);
    //             }
    //         } else {
    //             warnedPlayers.remove(player.getUniqueId());
    //             frozenPlayers.remove(player.getUniqueId());
    //         }
    //     });
    // }
    public void handlePlayerPosition(Player player) {
        if (center == null || radius <= 0) return;
    
        Location playerLocation = player.getLocation();
        double distance = playerLocation.distance(center);
    
        Bukkit.getScheduler().runTask(plugin, () -> {
            // Если игрок за пределами барьера
            if (distance > radius) {
                // Проверяем, если игрок ещё не был предупреждён
                if (!warnedPlayers.getOrDefault(player.getUniqueId(), false)) {
                    warnedPlayers.put(player.getUniqueId(), true);
                    // sendCenteredTitleMessage(player, "Что-то не так? Вы начали замерзать!");
                }
    
                // Получаем количество времени игрока за барьером
                long timeBehindBarrier = behindBarrierTime.getOrDefault(player.getUniqueId(), 0L);
                timeBehindBarrier += 1; // Увеличиваем на 1 тик (20 тиков = 1 секунда)
                behindBarrierTime.put(player.getUniqueId(), timeBehindBarrier);
    
                // Плавное увеличение продолжительности эффекта замедления
                int slownessDuration = Math.min((int) timeBehindBarrier / 2 + 20, 200); // Начинается с 20 секунд и увеличивается
    
                // Если эффект замедления не наложен или его продолжительность меньше нужной, обновляем
                if (!player.hasPotionEffect(PotionEffectType.SLOWNESS) || player.getPotionEffect(PotionEffectType.SLOWNESS).getDuration() < slownessDuration) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDuration, 1));
                }
    
                // Эффект слепоты с фиксированной продолжительностью (например, 5 секунд)
                int blindnessDuration = 100; // 5 секунд = 100 тиков
    
                PotionEffect blindnessEffect = player.getPotionEffect(PotionEffectType.BLINDNESS);
    
                if (blindnessEffect == null) {
                    // Если эффекта слепоты нет, накладываем его
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration, 0));
                } else {
                    // Если эффект слепоты есть, обновляем его на фиксированную продолжительность
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration, 0));
                }
    
                // Наносим урон, если игрок всё ещё за барьером
                int ticks = frozenPlayers.getOrDefault(player.getUniqueId(), 0);
                ticks++;
    
                // Фиксированный урон
                double damage = 3.5; // Урон фиксированный - 0.5 за тик (по полсердечка)
    
                // Наносим урон каждые 5 тиков (примерно 0.25 секунды)
                if (ticks % 5 == 0) { // Урон наносится каждые 5 тиков
                    if (player.getHealth() > damage) {
                        player.damage(damage); // Наносим урон
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0F, 1.0F); // Звук удара
                        player.spawnParticle(Particle.CRIT, player.getLocation(), 10); // Эффект удара (критические частицы)
                        // sendCenteredTitleMessage(player, "Вы теряете здоровье от замерзания!");
                    } else {
                        player.setHealth(0); // Устанавливаем здоровье 0 для смерти
                        // sendCenteredTitleMessage(player, "Вы замёрзли до смерти!");
                    }
                }
    
                frozenPlayers.put(player.getUniqueId(), ticks); // Сохраняем количество тиков
            } else {
                // Когда игрок внутри барьера, сбрасываем время нахождения и эффекты
                warnedPlayers.remove(player.getUniqueId());
                frozenPlayers.remove(player.getUniqueId());
                behindBarrierTime.remove(player.getUniqueId()); // Сбрасываем время нахождения
    
                // Убираем эффекты, когда игрок внутри барьера
                player.removePotionEffect(PotionEffectType.SLOWNESS); // Убираем эффект замедления
                player.removePotionEffect(PotionEffectType.BLINDNESS); // Убираем эффект слепоты
            }
        });
    }
    
    private void sendCenteredTitleMessage(Player player, String message) {
        Audience audience = audiences.sender(player);
        Component title = Component.text(message).color(TextColor.fromHexString("#FF0000")); // Красный цвет
        Title.Times times = Title.Times.times(
                Duration.ofSeconds(1),
                Duration.ofSeconds(3),
                Duration.ofSeconds(1)
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
