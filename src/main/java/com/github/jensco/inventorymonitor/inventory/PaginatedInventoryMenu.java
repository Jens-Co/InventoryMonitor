package com.github.jensco.inventorymonitor.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class PaginatedInventoryMenu {

    protected static final int INVENTORY_SIZE = 54;
    protected Inventory inventory;
    protected Player player;
    protected String menuTitle;
    protected int currentPage;
    protected List<ItemStack> items;

    public PaginatedInventoryMenu(Player player, String menuTitle, List<ItemStack> items) {
        this.player = player;
        this.menuTitle = menuTitle;
        this.items = items;
        this.currentPage = 0;
        initializeInventory();
    }

    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, Component.text(menuTitle + " - Page " + (currentPage + 1)));
        loadPage(currentPage);
        addNavigationButtons();
        player.openInventory(inventory);
    }

    protected abstract void loadPage(int page);

    protected void addNavigationButtons() {
        createNavigationButton("Previous Page", INVENTORY_SIZE - 9);
        createNavigationButton("Next Page", INVENTORY_SIZE - 1);
    }

    private void createNavigationButton(String name, int slot) {
        ItemStack navigationButton = new ItemStack(Material.ARROW);
        ItemMeta meta = navigationButton.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).color(NamedTextColor.YELLOW));
            navigationButton.setItemMeta(meta);
            inventory.setItem(slot, navigationButton);
        }
    }

    protected void setPage(int page) {
        currentPage = Math.max(0, Math.min(page, getTotalPages() - 1));
        initializeInventory();
    }

    protected int getTotalPages() {
        return (int) Math.ceil((double) items.size() / (INVENTORY_SIZE - 9));
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
