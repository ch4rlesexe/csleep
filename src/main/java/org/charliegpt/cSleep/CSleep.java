package org.charliegpt.cSleep;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CSleep extends JavaPlugin {

    private FileConfiguration messagesConfig;
    private SleepListener sleepListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        updateConfig(); // Update the config with new values if necessary
        loadMessagesConfig();

        sleepListener = new SleepListener(this);
        getServer().getPluginManager().registerEvents(sleepListener, this);

        getCommand("wake").setExecutor(new WakeCommand(this));
        getCommand("csleep").setExecutor(new ReloadCommand(this));
    }

    // This is the method that returns the SleepListener instance
    public SleepListener getSleepListener() {
        return sleepListener;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void loadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(new File(getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        } else {
            try {
                InputStream defaultConfigStream = getResource("config.yml");
                if (defaultConfigStream != null) {
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
                    FileConfiguration currentConfig = getConfig();

                    for (String key : defaultConfig.getKeys(true)) {
                        if (!currentConfig.contains(key)) {
                            currentConfig.set(key, defaultConfig.get(key));
                        }
                    }

                    currentConfig.save(configFile);
                }
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not update config.yml", e);
            }
        }
    }
}
