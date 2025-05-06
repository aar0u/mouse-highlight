package com.github.aar0u.mousehighlight.service;

import com.github.aar0u.mousehighlight.ui.ShapedWindow.ColorTheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "mouse-highlight.properties";
    private static final String COLOR_THEME_KEY = "color.theme";
    private final Logger logger = new Logger(this.getClass().getSimpleName());
    private final Properties properties;
    private final File configFile;

    public ConfigManager() {
        properties = new Properties();
        String jarPath = ConfigManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFolder = new File(jarPath).getParentFile();
        configFile = new File(jarFolder, CONFIG_FILE);
        logger.info("Loading config file: " + configFile.getAbsolutePath());
        loadConfig();
    }

    private void loadConfig() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                logger.err("Failed to load config: {}", e.getMessage());
            }
        }
    }

    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Mouse Highlight Configuration");
        } catch (IOException e) {
            logger.err("Failed to save config: {}", e.getMessage());
        }
    }

    public void setColorTheme(ColorTheme theme) {
        properties.setProperty(COLOR_THEME_KEY, theme.name());
        saveConfig();
    }

    public ColorTheme getColorTheme() {
        String themeName = properties.getProperty(COLOR_THEME_KEY);
        if (themeName != null) {
            try {
                return ColorTheme.valueOf(themeName);
            } catch (IllegalArgumentException e) {
                logger.err("Invalid color theme in config: {}", themeName);
            }
        }
        return ColorTheme.YELLOW; // Default theme
    }
}