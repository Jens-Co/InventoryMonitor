package com.github.jensco.inventorymonitor.inventory;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlertInventory extends PaginatedInventoryMenu {

    private final List<PlayerAlert> alerts;

    public AlertInventory(Player player, @NotNull Player target, List<PlayerAlert> alerts) {
        super(player, "Player " + target.getName() + " Alerts", convertToItemStacks(alerts));
        this.alerts = alerts;
        initializeInventory();
    }

    @Override
    protected void loadPage(int page) {
        inventory.clear();
        addNavigationButtons();

        int startIndex = page * (INVENTORY_SIZE - 9);
        int endIndex = Math.min(startIndex + (INVENTORY_SIZE - 9), items.size());

        for (int i = startIndex; i < endIndex; i++) {
            inventory.addItem(items.get(i));
        }
    }

    private static @NotNull List<ItemStack> convertToItemStacks(@NotNull List<PlayerAlert> alerts) {
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey uuidKey = new NamespacedKey(InventoryMonitor.getInstance(), "playerUUID");
        NamespacedKey timestampKey = new NamespacedKey(InventoryMonitor.getInstance(), "timestamp");

        for (PlayerAlert alert : alerts) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.displayName(Component.text(alert.playerName() + " Alert").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                meta.lore(List.of(
                        Component.text("Item: ").color(NamedTextColor.GRAY)
                                .append(Component.text(alert.itemName()).color(NamedTextColor.WHITE)),
                        Component.text("Amount: ").color(NamedTextColor.GRAY)
                                .append(Component.text(String.valueOf(alert.amount())).color(NamedTextColor.WHITE)),
                        Component.text("Timestamp: ").color(NamedTextColor.GRAY)
                                .append(Component.text(alert.getReadableTimestamp()).color(NamedTextColor.WHITE)),
                        Component.text("Alarm Type: ").color(NamedTextColor.GRAY)
                                .append(Component.text(alert.alarmType().toString()).color(NamedTextColor.WHITE)),
                        Component.text("Click to delete alert").color(NamedTextColor.AQUA)
                ));
                meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, alert.playerUUID().toString());
                meta.getPersistentDataContainer().set(timestampKey, PersistentDataType.LONG, alert.timestamp());
                item.setItemMeta(meta);
                items.add(item);
            }
        }

        return items;
    }

    @Override
    protected int getTotalPages() {
        return (int) Math.ceil(alerts.size() / (double) (INVENTORY_SIZE - 9));
    }

    public void open(int page) {
        setPage(page);
    }
}
