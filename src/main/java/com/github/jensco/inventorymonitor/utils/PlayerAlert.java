package com.github.jensco.inventorymonitor.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerAlert(String itemName, int amount, long timestamp, String playerName, UUID playerUUID, AlarmType alarmType) {

    @Contract(pure = true)
    public @NotNull String getAlertKey() {
        return itemName + "_" + amount + "_" + timestamp;
    }

    public @NotNull String getReadableTimestamp() {
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(timestamp));
    }
}