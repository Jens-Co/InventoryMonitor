package com.github.jensco.inventorymonitor.handler;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.config.ConfigurationInterface;
import com.github.jensco.inventorymonitor.utils.AlarmType;
import com.github.jensco.inventorymonitor.utils.Permission;
import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Monitor {
    private final ConfigurationInterface configHandler;
    private final MiniMessage miniMessage;
    public static Set<UUID> tempDisablePlayers = Collections.synchronizedSet(new HashSet<>());
    private final Map<UUID, Long> lastCheckTime = Collections.synchronizedMap(new HashMap<>());

    private static final long CHECK_INTERVAL = 60000; // Check interval in milliseconds (60 seconds)

    @Contract(pure = true)
    public Monitor(@NotNull InventoryMonitor plugin) {
        this.configHandler = plugin.getConfiguration();
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void checkPlayer(@NotNull Player player, AlarmType type) {
        if (tempDisablePlayers.contains(player.getUniqueId())) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lastCheckTime.containsKey(playerId) && (currentTime - lastCheckTime.get(playerId)) < CHECK_INTERVAL) {
            return; // Skip check if still in the check interval period
        }

        Map<Material, Integer> itemCounts = new HashMap<>();

        // Include player's inventory contents
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                itemCounts.put(item.getType(), itemCounts.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        // Include the item on the player's cursor
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem != null && cursorItem.getType() != Material.AIR) {
            itemCounts.put(cursorItem.getType(), itemCounts.getOrDefault(cursorItem.getType(), 0) + cursorItem.getAmount());
        }

        Map<Material, Integer> monitoredItems = configHandler.getMonitoredItems();
        List<PlayerAlert> previousAlerts = configHandler.getPlayerAlerts(player.getUniqueId());

        for (Map.Entry<Material, Integer> entry : monitoredItems.entrySet()) {
            Material material = entry.getKey();
            int threshold = entry.getValue();
            int count = itemCounts.getOrDefault(material, 0);

            boolean recentlyTriggered = previousAlerts.stream()
                    .anyMatch(alert -> alert.itemName().equals(material.name()) &&
                            alert.amount() == count &&
                            (currentTime - alert.timestamp()) < getMillisecondsFromMinutes(configHandler.getTimeBetweenAlert()));// 5 minutes

            if (count > threshold && !recentlyTriggered) {
                PlayerAlert alert = new PlayerAlert(material.name(), count, currentTime, player.getName(), player.getUniqueId(), type);
                alert(alert);
                configHandler.savePlayerAlert(player.getUniqueId(), alert);
            }
        }

        lastCheckTime.put(playerId, currentTime);
    }

    public void alert(@NotNull PlayerAlert alert) {
        String materialName = alert.itemName().toLowerCase().replace('_', ' ');
        String messageTemplate = configHandler.getAlertMessage();
        String formattedMessage = messageTemplate
                .replace("{player}", alert.playerName())
                .replace("{amount}", String.valueOf(alert.amount()))
                .replace("{type}", alert.alarmType().toString())
                .replace("{item}", materialName);

        Component message = miniMessage.deserialize("<red>[InventoryMonitor]<reset> " + formattedMessage);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(Permission.ALERT.getPermission()))
                .forEach(p -> p.sendMessage(message));
    }

    private long getMillisecondsFromMinutes(long minutes) {
        return minutes * 60 * 1000; // Convert minutes to milliseconds
    }
}

