package com.align.driver;

import com.align.exceptions.DriverInitException;
import io.appium.java_client.AppiumDriver;

public class DriverManager {
    private static final ThreadLocal<AppiumDriver> driverThread = new ThreadLocal<>();

    private DriverManager() {}

    public static void setDriver(AppiumDriver driver) {
        driverThread.set(driver);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver == null) {
            throw new DriverInitException(
                "No AppiumDriver initialized for thread: " + Thread.currentThread().getName());
        }
        return driver;
    }

    public static void removeDriver() {
        driverThread.remove();
    }

    public static boolean hasDriver() {
        return driverThread.get() != null;
    }
}
