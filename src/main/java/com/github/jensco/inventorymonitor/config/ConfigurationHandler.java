package com.github.jensco.inventorymonitor.config;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import com.github.jensco.inventorymonitor.utils.AlarmType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationHandler implements ConfigurationInterface {
    private final FileConfiguration config;
    private final InventoryMonitor plugin;

    public ConfigurationHandler(@NotNull InventoryMonitor plugin) {
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    @Override
    public Map<Material, Integer> getMonitoredItems() {
        Map<Material, Integer> monitoredItems = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("monitored-items");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                Material material = Material.getMaterial(key);
                if (material != null) {
                    int amount = section.getInt(key);
                    monitoredItems.put(material, amount);
                }
            }
        }
        return monitoredItems;
    }

    @Override
    public void savePlayerAlert(UUID playerUUID, @NotNull PlayerAlert alert) {
        File playerFile = getPlayerFile(playerUUID);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        String key = "alerts." + alert.timestamp();

        config.set(key + ".itemName", alert.itemName());
        config.set(key + ".amount", alert.amount());
        config.set(key + ".timestamp", alert.timestamp());
        config.set(key + ".readableTimestamp", alert.getReadableTimestamp());
        config.set(key + ".playerName", alert.playerName());
        config.set(key + ".playerUUID", alert.playerUUID().toString());
        config.set(key + ".alarmType", alert.alarmType().toString());

        List<String> triggeredAlerts = config.getStringList("triggeredAlerts");
        triggeredAlerts.add(alert.getAlertKey());
        config.set("triggeredAlerts", triggeredAlerts);

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAlertMessage() {
        return config.getString("alert-message", "<red>[ALERT] <gold>{player} has {amount} {item}!");
    }

    @Override
    public long getTimeBetweenAlert() {
        return config.getLong("alert-time", 5);
    }

    @Override
    public List<PlayerAlert> getPlayerAlerts(UUID playerUUID) {
        File playerFile = getPlayerFile(playerUUID);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        List<PlayerAlert> alerts = new ArrayList<>();

        if (config.contains("alerts")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("alerts")).getKeys(false)) {
                String itemName = config.getString("alerts." + key + ".itemName");
                int amount = config.getInt("alerts." + key + ".amount");
                long timestamp = config.getLong("alerts." + key + ".timestamp");
                String playerName = config.getString("alerts." + key + ".playerName");
                UUID uuid = UUID.fromString(Objects.requireNonNull(config.getString("alerts." + key + ".playerUUID")));
                String typeStr = config.getString("alerts." + key + ".alarmType");
                AlarmType type = AlarmType.valueOf(typeStr);
                alerts.add(new PlayerAlert(itemName, amount, timestamp, playerName, uuid, type));
            }
        }

        return alerts;
    }

    @Override
    public Set<String> getTriggeredAlerts(UUID playerUUID) {
        File playerFile = getPlayerFile(playerUUID);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        return new HashSet<>(config.getStringList("triggeredAlerts"));
    }

    @Override
    public void deletePlayerAlert(UUID playerUUID, PlayerAlert alert) {
        File playerFile = getPlayerFile(playerUUID);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        String alertKey = "alerts." + alert.timestamp();

        config.set(alertKey, null);

        List<String> triggeredAlerts = config.getStringList("triggeredAlerts");
        triggeredAlerts.remove(alert.getAlertKey());
        config.set("triggeredAlerts", triggeredAlerts);

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private @NotNull File getPlayerFile(UUID playerUUID) {
        File playerDir = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }

        return new File(playerDir, playerUUID.toString() + ".yml");
    }
}
