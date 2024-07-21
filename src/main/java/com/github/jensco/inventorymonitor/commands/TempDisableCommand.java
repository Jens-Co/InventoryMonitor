package com.github.jensco.inventorymonitor.commands;

import com.github.jensco.inventorymonitor.handler.Monitor;
import com.github.jensco.inventorymonitor.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TempDisableCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permission.TEMPDISABLE.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /monitor tempdisable <player>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage("Player not found or not online.");
            return true;
        }

        UUID playerUUID = targetPlayer.getUniqueId();
        if (Monitor.tempDisablePlayers.contains(playerUUID)) {
            Monitor.tempDisablePlayers.remove(playerUUID);
            sender.sendMessage("Alerts for " + playerName + " have been enabled.");
        } else {
            Monitor.tempDisablePlayers.add(playerUUID);
            sender.sendMessage("Alerts for " + playerName + " have been disabled.");
        }
        return true;
    }
}
