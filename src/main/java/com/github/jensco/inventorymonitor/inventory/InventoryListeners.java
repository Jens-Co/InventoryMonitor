package com.github.jensco.inventorymonitor.inventory;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.handler.Monitor;
import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import com.github.jensco.inventorymonitor.utils.AlarmType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class InventoryListeners implements Listener {

    private final Monitor monitor;
    private final InventoryMonitor plugin;

    public InventoryListeners(InventoryMonitor plugin) {
        this.monitor = new Monitor(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(@NotNull EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            monitor.checkPlayer(player, AlarmType.PickupItem);
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        monitor.checkPlayer(player, AlarmType.Inventory);
        InventoryView inventoryView = event.getView();
        Component titleComponent = inventoryView.title();

        if (titleComponent instanceof TextComponent textComponent &&
                textComponent.content().startsWith("Player")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            if (clickedItem.getType() == Material.PAPER) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta == null) return;

                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                NamespacedKey uuidKey = new NamespacedKey(plugin, "playerUUID");
                NamespacedKey timestampKey = new NamespacedKey(plugin, "timestamp");

                if (!dataContainer.has(uuidKey, PersistentDataType.STRING) || !dataContainer.has(timestampKey, PersistentDataType.LONG)) {
                    return;
                }

                String playerUUIDStr = dataContainer.get(uuidKey, PersistentDataType.STRING);
                long timestamp = dataContainer.get(timestampKey, PersistentDataType.LONG);

                assert playerUUIDStr != null;
                UUID playerUUID = UUID.fromString(playerUUIDStr);

                List<PlayerAlert> alerts = plugin.getConfiguration().getPlayerAlerts(playerUUID);
                PlayerAlert alertToDelete = alerts.stream()
                        .filter(alert -> alert.timestamp() == timestamp)
                        .findFirst()
                        .orElse(null);

                if (alertToDelete != null) {
                    plugin.getConfiguration().deletePlayerAlert(playerUUID, alertToDelete);
                    player.closeInventory();
                    player.sendMessage(Component.text("Alert deleted."));
                }
                return;
            }

            if (clickedItem.getType() != Material.ARROW) return;
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String[] titleParts = textComponent.content().split(" ");
            if (titleParts.length < 2) return;
            String targetName = titleParts[1];

            Player target = Bukkit.getPlayer(targetName);
            if (target == null) return;

            List<PlayerAlert> alerts = plugin.getConfiguration().getPlayerAlerts(target.getUniqueId());
            AlertInventory alertInventory = new AlertInventory(player, target, alerts);

            Component displayName = meta.displayName();
            if (displayName instanceof TextComponent displayNameText) {
                String displayNameContent = displayNameText.content();
                if (displayNameContent.contains("Previous Page")) {
                    alertInventory.open(alertInventory.getCurrentPage() - 1);
                } else if (displayNameContent.contains("Next Page")) {
                    alertInventory.open(alertInventory.getCurrentPage() + 1);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            monitor.checkPlayer(player, AlarmType.Inventory);
        }
    }
}
