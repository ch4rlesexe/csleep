package org.charliegpt.cSleep;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final CSleep plugin;

    public ReloadCommand(CSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadMessagesConfig();
            sender.sendMessage(ChatColor.GREEN + "CSleep configuration reloaded!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /csleep reload");
            return false;
        }
    }
}
