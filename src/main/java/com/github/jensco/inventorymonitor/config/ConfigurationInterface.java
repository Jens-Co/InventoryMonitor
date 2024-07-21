package com.github.jensco.inventorymonitor.config;

import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ConfigurationInterface {

    Map<Material, Integer> getMonitoredItems();

    void savePlayerAlert(UUID playerUUID, PlayerAlert alert);

    String getAlertMessage();

    long getTimeBetweenAlert();

    List<PlayerAlert> getPlayerAlerts(UUID playerUUID);

    Set<String> getTriggeredAlerts(UUID playerUUID);

    void deletePlayerAlert(UUID playerUUID, PlayerAlert alert);
}
