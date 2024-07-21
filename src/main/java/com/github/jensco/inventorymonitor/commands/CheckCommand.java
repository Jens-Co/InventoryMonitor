package com.github.jensco.inventorymonitor.commands;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.inventory.AlertInventory;
import com.github.jensco.inventorymonitor.utils.PlayerAlert;
import com.github.jensco.inventorymonitor.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CheckCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission(Permission.Check.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /monitor check <player>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage("Player not found or not online.");
            return true;
        }

        UUID playerUUID = targetPlayer.getUniqueId();
        List<PlayerAlert> alerts = InventoryMonitor.getInstance().getConfiguration().getPlayerAlerts(playerUUID);

        if (alerts.isEmpty()) {
            sender.sendMessage("No alerts found for this player.");
            return true;
        }

        if (sender instanceof Player player) {
            AlertInventory alertInventory = new AlertInventory(player, targetPlayer, alerts);
            alertInventory.open(0); // Open the first page
        } else {
            sender.sendMessage("This command can only be executed by a player.");
        }
        return true;
    }
}