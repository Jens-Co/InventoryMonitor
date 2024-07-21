package com.github.jensco.inventorymonitor.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, CommandExecutor> subcommands;

    public CommandManager() {
        this.subcommands = new HashMap<>();

        // Register subcommands
        registerSubcommand("reload", new ReloadCommand());
        registerSubcommand("check", new CheckCommand());
        registerSubcommand("tempdisable", new TempDisableCommand());
    }

    public void registerSubcommand(@NotNull String subcommandName, CommandExecutor executor) {
        subcommands.put(subcommandName.toLowerCase(), executor);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("monitor")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /monitor <subcommand>");
                return true;
            }

            String subcommand = args[0].toLowerCase();
            CommandExecutor executor = subcommands.get(subcommand);
            if (executor != null) {
                // Remove the subcommand from the arguments and pass the remaining args to the subcommand executor
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, args.length - 1);
                return executor.onCommand(sender, command, label, subArgs);
            } else {
                sender.sendMessage("Unknown subcommand. Use /monitor help for a list of commands.");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return filterSubcommands(args[0]);
        }
        return null;
    }

    private @NotNull List<String> filterSubcommands(String input) {
        List<String> matches = new ArrayList<>();
        for (String subcommand : subcommands.keySet()) {
            if (subcommand.startsWith(input.toLowerCase())) {
                matches.add(subcommand);
            }
        }
        return matches;
    }
}