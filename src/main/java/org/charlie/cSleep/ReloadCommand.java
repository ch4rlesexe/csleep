package org.charlie.cSleep;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {

    private final CSleep plugin;

    public ReloadCommand(CSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // Check if the sender has the required permission
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("csleep.reload")) {
                    String noPermissionMessage = plugin.getMessagesConfig().getString("messages.noPermission")
                            .replace("&", "§");
                    if (noPermissionMessage != null && !noPermissionMessage.isEmpty()) {
                        player.sendMessage(noPermissionMessage);
                    }
                    return true;
                }
            } else if (!sender.hasPermission("csleep.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to reload the CSleep plugin.");
                return true;
            }

            // Reload the plugin configuration
            plugin.reloadConfig();
            plugin.loadMessagesConfig();
            sender.sendMessage(ChatColor.GREEN + "CSleep configuration reloaded!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /csleep reload");
            return true; // Return true to prevent the default usage message from appearing
        }
    }
}
