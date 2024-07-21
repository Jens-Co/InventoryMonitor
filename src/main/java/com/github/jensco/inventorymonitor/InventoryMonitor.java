package com.github.jensco.inventorymonitor;

import com.github.jensco.inventorymonitor.commands.CommandManager;
import com.github.jensco.inventorymonitor.config.ConfigurationHandler;
import com.github.jensco.inventorymonitor.inventory.InventoryListeners;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class InventoryMonitor extends JavaPlugin {

    public static Logger logger;
    private ConfigurationHandler config;
    public static InventoryMonitor instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        logger = this.getLogger();
        // load configuration.
        configFiles();
        config = new ConfigurationHandler(this);
        Bukkit.getPluginManager().registerEvents(new InventoryListeners(this), this);
        // Initialize CommandManager
        CommandManager commandManager = new CommandManager();
        Objects.requireNonNull(getCommand("monitor")).setExecutor(commandManager);
        Objects.requireNonNull(getCommand("monitor")).setTabCompleter(commandManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Load config or create.
     */
    public void configFiles() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        // Create file
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        // load config
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Could not load config.yml" + e.getMessage());
        }
    }
    // Reload Config file.
    public void reloadConfigFiles() {
        this.reloadConfig();
        config = new ConfigurationHandler( this);
    }

    public @NotNull ConfigurationHandler getConfiguration() {
        return config;
    }

    public static InventoryMonitor getInstance() {
        return instance;
    }
}
