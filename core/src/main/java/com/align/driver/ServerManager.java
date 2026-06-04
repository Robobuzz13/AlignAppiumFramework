package com.align.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class ServerManager {
    private static final Logger log = LoggerFactory.getLogger(ServerManager.class);
    private static AppiumDriverLocalService service;

    private ServerManager() {}

    public static synchronized void startServer() {
        if (service != null && service.isRunning()) {
            log.info("Appium server already running");
            return;
        }
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(4723)
                .withTimeout(Duration.ofSeconds(60));

        String appiumPath = System.getenv("APPIUM_PATH");
        if (appiumPath != null && !appiumPath.isBlank()) {
            builder.withAppiumJS(new File(appiumPath));
        }

        service = builder.build();
        service.start();
        log.info("Appium server started at http://127.0.0.1:4723");
    }

    public static synchronized void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            log.info("Appium server stopped");
        }
    }

    public static boolean isRunning() {
        return service != null && service.isRunning();
    }
}
