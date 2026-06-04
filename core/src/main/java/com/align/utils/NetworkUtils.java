package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.connection.ConnectionState;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
    private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

    private NetworkUtils() {}

    public static void toggleWifi(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleWifi");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withWiFiEnabled().build()
                : new ConnectionStateBuilder().withWiFiDisabled().build();
        android.setConnection(state);
        log.info("WiFi {}", enable ? "enabled" : "disabled");
    }

    public static void toggleMobileData(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleMobileData");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withDataEnabled().build()
                : new ConnectionStateBuilder().withDataDisabled().build();
        android.setConnection(state);
        log.info("Mobile data {}", enable ? "enabled" : "disabled");
    }

    public static void toggleAirplaneMode(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleAirplaneMode");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withAirplaneModeEnabled().build()
                : new ConnectionStateBuilder().withAirplaneModeDisabled().build();
        android.setConnection(state);
        log.info("Airplane mode {}", enable ? "enabled" : "disabled");
    }

    public static ConnectionState getNetworkConnection(AppiumDriver driver) {
        requireAndroid(driver, "getNetworkConnection");
        return ((AndroidDriver) driver).getConnection();
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android-only");
        }
    }
}
