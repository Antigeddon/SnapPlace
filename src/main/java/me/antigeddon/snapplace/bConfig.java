package me.antigeddon.snapplace;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class bConfig extends Configuration {

    private static final Logger logger = Bukkit.getServer().getLogger();
    private final File configFile;

    public bConfig(JavaPlugin plugin, String fileName) {
        super(new File(plugin.getDataFolder(), fileName));
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    @Override
    public void load() {
        createParentDirectories();

        if (!configFile.exists()) {
            copyDefaultConfig();
        }

        try {
            super.load();
        } catch (Exception e) {
            logger.severe("[SnapPlace] Failed to load configuration '" + configFile.getName() + "': " + e.getMessage());
        }
    }

    private void createParentDirectories() {
        try {
            Files.createDirectories(configFile.getParentFile().toPath());
        } catch (IOException e) {
            logger.severe("[SnapPlace] Failed to create configuration directory: " + e.getMessage());
        }
    }

    private void copyDefaultConfig() {
        String resourcePath = "/" + configFile.getName();

        try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
            if (input == null) {
                logger.severe("[SnapPlace] Default configuration '" + configFile.getName() + "' wasn't found.");
                return;
            }

            Files.copy(input, configFile.toPath());
        } catch (IOException e) {
            logger.severe("[SnapPlace] Failed to create default configuration '" + configFile.getName() + "': " + e.getMessage());
        }
    }

    public void loadAndLog() {
        try {
            this.load();
            logger.info("[SnapPlace] Configuration '" + configFile.getName() + "' loaded successfully.");
        } catch (Exception e) {
            logger.severe("[SnapPlace] Failed to load configuration '" + configFile.getName() + "': " + e.getMessage());
        }
    }

    public void saveAndLog() {
        try {
            this.save();
            logger.info("[SnapPlace] Configuration '" + configFile.getName() + "' saved successfully.");
        } catch (Exception e) {
            logger.severe("[SnapPlace] Failed to save configuration '" + configFile.getName() + "': " + e.getMessage());
        }
    }

    public Map<String, Object> getAll() {
        Map<String, Object> allConfig = new HashMap<>();
        List<String> keys = this.getKeys();

        for (String key : keys) {
            Object value = this.getProperty(key);
            allConfig.put(key, value);
        }

        return allConfig;
    }

    public void printAllConfig(Logger logger) {
        logger.info("[SnapPlace] ======= CONFIG.YML CONTENT =======");

        Map<String, Object> all = getAll();
        if (all.isEmpty()) {
            logger.warning("[SnapPlace] The configuration is empty or not found!");
            return;
        }

        for (Map.Entry<String, Object> entry : all.entrySet()) {
            logger.info("[SnapPlace] " + entry.getKey() + ": " + entry.getValue());
        }

        logger.info("[SnapPlace] ======= END OF CONFIG.YML =======");
    }
}
