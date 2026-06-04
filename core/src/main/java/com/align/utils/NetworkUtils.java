package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.connection.ConnectionState;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import io.appium.java_client.android.connection.HasNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
    private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

    private NetworkUtils() {}

    public static void toggleWifi(AppiumDriver driver, boolean enable) {
        requireAndroidNetwork(driver, "toggleWifi");
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withWiFiEnabled().build()
                : new ConnectionStateBuilder().withWiFiDisabled().build();
        ((HasNetworkConnection) driver).setConnection(state);
        log.info("WiFi {}", enable ? "enabled" : "disabled");
    }

    public static void toggleMobileData(AppiumDriver driver, boolean enable) {
        requireAndroidNetwork(driver, "toggleMobileData");
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withDataEnabled().build()
                : new ConnectionStateBuilder().withDataDisabled().build();
        ((HasNetworkConnection) driver).setConnection(state);
        log.info("Mobile data {}", enable ? "enabled" : "disabled");
    }

    public static void toggleAirplaneMode(AppiumDriver driver, boolean enable) {
        requireAndroidNetwork(driver, "toggleAirplaneMode");
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withAirplaneModeEnabled().build()
                : new ConnectionStateBuilder().withAirplaneModeDisabled().build();
        ((HasNetworkConnection) driver).setConnection(state);
        log.info("Airplane mode {}", enable ? "enabled" : "disabled");
    }

    public static ConnectionState getNetworkConnection(AppiumDriver driver) {
        requireAndroidNetwork(driver, "getNetworkConnection");
        return ((HasNetworkConnection) driver).getConnection();
    }

    private static void requireAndroidNetwork(AppiumDriver driver, String method) {
        if (!(driver instanceof HasNetworkConnection)) {
            throw new UnsupportedOperationException(method + "() is Android-only");
        }
    }
}
