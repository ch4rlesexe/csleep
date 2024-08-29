package org.charlie.cSleep;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepListener implements Listener {

    private final CSleep plugin;
    private BukkitRunnable nightSkipTask;
    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();
    private final Map<UUID, Boolean> wasKickedOut = new HashMap<>();

    public SleepListener(CSleep plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        long time = player.getWorld().getTime();
        if (time < 12541 || time > 23458) return; // Ensure it's night time

        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessagesConfig();

        String playerSleepingMessage = messages.getString("messages.playerSleeping");
        if (playerSleepingMessage != null && !playerSleepingMessage.isEmpty()) {
            playerSleepingMessage = playerSleepingMessage.replace("%player%", player.getName()).replace("&", "§");

            // Split the message around the clickable part
            String[] parts = playerSleepingMessage.split("\\[Click here to tell them to get up\\]", 2);

            TextComponent baseMessage = new TextComponent(parts[0]);

            // Create the clickable part
            TextComponent clickMessage = new TextComponent("[Click here to tell them to get up]");
            clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wake " + player.getName()));
            clickMessage.setColor(net.md_5.bungee.api.ChatColor.GREEN);

            baseMessage.addExtra(clickMessage);

            // Add the rest of the message, if any
            if (parts.length > 1) {
                baseMessage.addExtra(new TextComponent(parts[1]));
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(baseMessage);
            }
        }

        // Additional logic for skipping the night...
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if the player was manually kicked out
        String stoppingNightSkipMessage = plugin.getMessagesConfig().getString("messages.stoppingNightSkip");
        if (wasKickedOut.getOrDefault(playerUUID, false)) {
            if (nightSkipTask != null) {
                nightSkipTask.cancel();
                nightSkipTask = null;

                if (stoppingNightSkipMessage != null && !stoppingNightSkipMessage.isEmpty()) {
                    stoppingNightSkipMessage = stoppingNightSkipMessage.replace("%player%", player.getName()).replace("&", "§");
                    Bukkit.broadcastMessage(stoppingNightSkipMessage);
                }
            }

            wasKickedOut.remove(playerUUID);
        }

        // Remove the player's task if it exists
        if (playerTasks.containsKey(playerUUID)) {
            playerTasks.remove(playerUUID).cancel();
        }
    }

    public void kickPlayerOutOfBed(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Mark the player as kicked out
        wasKickedOut.put(playerUUID, true);

        // Wake up the player
        player.wakeup(true);

        // Send a kicked out message
        String kickedOutMessage = plugin.getMessagesConfig().getString("messages.kickedOutOfBed");
        if (kickedOutMessage != null && !kickedOutMessage.isEmpty()) {
            player.sendMessage(kickedOutMessage.replace("&", "§"));
        }
    }
}
