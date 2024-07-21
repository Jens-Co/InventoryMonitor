package com.github.jensco.inventorymonitor.commands;

import com.github.jensco.inventorymonitor.InventoryMonitor;
import com.github.jensco.inventorymonitor.utils.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permission.RELOAD.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        InventoryMonitor.getInstance().reloadConfigFiles();
        sender.sendMessage("Configuration reloaded successfully.");
        return true;
    }
}

