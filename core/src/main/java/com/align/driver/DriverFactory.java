package com.align.driver;

import com.align.config.DeviceConfig;
import com.align.exceptions.DriverInitException;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static AppiumDriver createDriver(DeviceConfig config) {
        if (config.isAndroid()) {
            return createAndroidDriver(config);
        } else if (config.isIOS()) {
            return createIOSDriver(config);
        }
        throw new DriverInitException("Unsupported platform: " + config.getPlatform()
                + ". Supported: android, ios");
    }

    private static AppiumDriver createAndroidDriver(DeviceConfig config) {
        try {
            Class<?> factoryClass = Class.forName("com.align.android.driver.AndroidDriverFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            return (AppiumDriver) factoryClass.getMethod("createDriver", DeviceConfig.class)
                    .invoke(factory, config);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new DriverInitException("Failed to create AndroidDriver: " +
                    (cause != null ? cause.getMessage() : e.getMessage()),
                    cause != null ? cause : e);
        } catch (Exception e) {
            throw new DriverInitException("Failed to create AndroidDriver: " + e.getMessage(), e);
        }
    }

    private static AppiumDriver createIOSDriver(DeviceConfig config) {
        try {
            Class<?> factoryClass = Class.forName("com.align.ios.driver.IOSDriverFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            return (AppiumDriver) factoryClass.getMethod("createDriver", DeviceConfig.class)
                    .invoke(factory, config);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new DriverInitException("Failed to create IOSDriver: " +
                    (cause != null ? cause.getMessage() : e.getMessage()),
                    cause != null ? cause : e);
        } catch (Exception e) {
            throw new DriverInitException("Failed to create IOSDriver: " + e.getMessage(), e);
        }
    }
}
