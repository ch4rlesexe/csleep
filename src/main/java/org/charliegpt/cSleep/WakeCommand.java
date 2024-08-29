package org.charliegpt.cSleep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WakeCommand implements CommandExecutor {

    private final CSleep plugin;

    public WakeCommand(CSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the permission requirement is enabled in the config
        boolean requiresPermission = plugin.getConfig().getBoolean("wake-requires-permission", false);

        if (requiresPermission && sender instanceof Player) {
            Player player = (Player) sender;
            String noPermissionMessage = plugin.getMessagesConfig().getString("messages.noPermission");
            if (!player.hasPermission("csleep.wake")) {
                if (noPermissionMessage != null && !noPermissionMessage.isEmpty()) {
                    sender.sendMessage(noPermissionMessage.replace("&", "§"));
                }
                return true; // Return true to avoid showing the usage message
            }
        }

        // Ensure the command has exactly one argument (the player's name)
        String usageMessage = plugin.getMessagesConfig().getString("messages.usageWake");
        if (args.length != 1) {
            if (usageMessage != null && !usageMessage.isEmpty()) {
                sender.sendMessage(usageMessage.replace("&", "§"));
            }
            return true; // Return true to prevent the default usage message
        }

        Player target = Bukkit.getPlayer(args[0]);
        String playerNotFoundMessage = plugin.getMessagesConfig().getString("messages.playerNotFound");
        if (target == null) {
            if (playerNotFoundMessage != null && !playerNotFoundMessage.isEmpty()) {
                sender.sendMessage(playerNotFoundMessage.replace("&", "§"));
            }
            return true;
        }

        if (target.isSleeping()) {
            SleepListener sleepListener = plugin.getSleepListener();
            sleepListener.kickPlayerOutOfBed(target);

            String playerWokeUpMessage = plugin.getMessagesConfig().getString("messages.playerWokeUp");
            if (playerWokeUpMessage != null && !playerWokeUpMessage.isEmpty()) {
                sender.sendMessage(playerWokeUpMessage.replace("%player%", target.getName()).replace("&", "§"));
            }
        } else {
            String playerNotInBedMessage = plugin.getMessagesConfig().getString("messages.playerNotInBed");
            if (playerNotInBedMessage != null && !playerNotInBedMessage.isEmpty()) {
                sender.sendMessage(playerNotInBedMessage.replace("&", "§"));
            }
        }

        return true; // Command executed successfully, return true
    }
}
