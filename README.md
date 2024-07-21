InventoryMonitor Plugin
Overview

InventoryMonitor is a Minecraft plugin designed to monitor player inventories and alert administrators when certain conditions are met. It tracks specific items in player inventories and generates alerts if players exceed predefined thresholds. The alerts can be viewed and managed through an in-game inventory interface.
Features

    Monitor specific items in player inventories.
    Generate alerts when item counts exceed thresholds.
    Persistent storage of player alerts.
    In-game inventory interface to view and manage alerts.
    Command-based interface for administrative tasks.
    Temporarily disable monitoring for specific players.

Installation

    Download the latest release of InventoryMonitor.
    Place the InventoryMonitor.jar file into your server's plugins directory.
    Start your Minecraft server to generate the default configuration files.
    Edit the configuration file located at plugins/InventoryMonitor/config.yml to set up monitored items and other settings.
    Restart the server to apply the configuration changes.

Configuration

The configuration file (config.yml) contains settings for monitored items, alert messages, and time intervals between alerts. Example configuration:

yaml

monitored-items:
  DIAMOND: 10
  GOLD_INGOT: 20

alert-message: "<red>[ALERT] <gold>{player} has {amount} {item}!"
alert-time: 5 # Time in minutes between alerts for the same item and player

Commands

InventoryMonitor provides several commands for administrative tasks:

    /monitor reload - Reloads the plugin configuration.
    /monitor check <player> - Manually check a player's inventory.
    /monitor tempdisable <player> - Temporarily disable monitoring for a player.

Permissions

Permissions control access to commands and alert messages:

    inventorymonitor.alert - Receive alert messages.
    inventorymonitor.command.reload - Use /monitor reload command.
    inventorymonitor.command.check - Use /monitor check command.
    inventorymonitor.command.tempdisable - Use /monitor tempdisable command.

Usage
Monitoring and Alerts

InventoryMonitor automatically checks player inventories at regular intervals. If a player possesses more than the configured threshold of a monitored item, an alert is generated and broadcast to players with the inventorymonitor.alert permission.
Viewing and Managing Alerts

Alerts can be viewed and managed using an in-game inventory interface. To open the alert inventory, use the appropriate command or interact with the provided GUI elements.
Temporarily Disabling Monitoring

To temporarily disable monitoring for a specific player, use the command:

bash

/monitor tempdisable <player>

Alert Inventory Interface

The alert inventory interface displays a list of alerts for a specific player. Each alert includes details about the item, amount, timestamp, and alarm type. Alerts can be deleted by clicking on them.
