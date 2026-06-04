package com.align.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static ConfigLoader instance;
    private final Properties props = new Properties();

    public ConfigLoader(String fileName) {
        loadFile(fileName);
    }

    private ConfigLoader() {
        loadFile("config.properties");
        String platform = getProperty("platform", "android");
        String env = getProperty("env", "local");
        loadFile(platform + "-" + env + ".properties");
    }

    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    private void loadFile(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/" + fileName)) {
            if (is != null) {
                props.load(is);
                log.debug("Loaded config file: {}", fileName);
            } else {
                log.warn("Config file not found on classpath: config/{}", fileName);
            }
        } catch (IOException e) {
            log.error("Failed to load config file: {}", fileName, e);
        }
    }

    public String getProperty(String key, String defaultValue) {
        // Resolution: CLI -D > env var > properties file > default
        String value = System.getProperty(key);
        if (value != null) return value;

        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null) return value;

        return props.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return getProperty(key, "");
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
