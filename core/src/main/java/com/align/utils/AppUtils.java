package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.ApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AppUtils {
    private static final Logger log = LoggerFactory.getLogger(AppUtils.class);

    private AppUtils() {}

    private static InteractsWithApps apps(AppiumDriver driver) {
        return (InteractsWithApps) driver;
    }

    public static void installApp(AppiumDriver driver, String appPath) {
        apps(driver).installApp(appPath);
        log.info("App installed: {}", appPath);
    }

    public static void removeApp(AppiumDriver driver, String bundleIdOrPackage) {
        apps(driver).removeApp(bundleIdOrPackage);
        log.info("App removed: {}", bundleIdOrPackage);
    }

    public static boolean isAppInstalled(AppiumDriver driver, String bundleIdOrPackage) {
        return apps(driver).isAppInstalled(bundleIdOrPackage);
    }

    public static void activateApp(AppiumDriver driver, String bundleIdOrPackage) {
        apps(driver).activateApp(bundleIdOrPackage);
        log.info("App activated: {}", bundleIdOrPackage);
    }

    public static void terminateApp(AppiumDriver driver, String bundleIdOrPackage) {
        apps(driver).terminateApp(bundleIdOrPackage);
        log.info("App terminated: {}", bundleIdOrPackage);
    }

    public static ApplicationState getAppState(AppiumDriver driver, String bundleIdOrPackage) {
        return apps(driver).queryAppState(bundleIdOrPackage);
    }

    public static void runAppInBackground(AppiumDriver driver, Duration duration) {
        apps(driver).runAppInBackground(duration);
        log.debug("App ran in background for {}", duration);
    }

    public static void resetApp(AppiumDriver driver, String bundleIdOrPackage) {
        terminateApp(driver, bundleIdOrPackage);
        activateApp(driver, bundleIdOrPackage);
    }
}
