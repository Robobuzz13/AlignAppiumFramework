package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HasDeviceTime;
import io.appium.java_client.LocksDevice;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.ShakesDevice;
import io.appium.java_client.remote.SupportsRotation;
import org.openqa.selenium.ScreenOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DeviceUtils {
    private static final Logger log = LoggerFactory.getLogger(DeviceUtils.class);

    private DeviceUtils() {}

    public static void lockDevice(AppiumDriver driver) {
        ((LocksDevice) driver).lockDevice();
        log.debug("Device locked");
    }

    public static void lockDevice(AppiumDriver driver, Duration duration) {
        ((LocksDevice) driver).lockDevice(duration);
    }

    public static void unlockDevice(AppiumDriver driver) {
        ((LocksDevice) driver).unlockDevice();
        log.debug("Device unlocked");
    }

    public static boolean isDeviceLocked(AppiumDriver driver) {
        return ((LocksDevice) driver).isDeviceLocked();
    }

    public static void rotatePortrait(AppiumDriver driver) {
        ((SupportsRotation) driver).rotate(ScreenOrientation.PORTRAIT);
    }

    public static void rotateLandscape(AppiumDriver driver) {
        ((SupportsRotation) driver).rotate(ScreenOrientation.LANDSCAPE);
    }

    public static ScreenOrientation getDeviceOrientation(AppiumDriver driver) {
        return ((SupportsRotation) driver).getOrientation();
    }

    public static void shake(AppiumDriver driver) {
        if (driver instanceof IOSDriver) {
            ((ShakesDevice) driver).shake();
        } else {
            log.warn("shake() is iOS simulator only");
        }
    }

    public static String getDeviceTime(AppiumDriver driver) {
        return ((HasDeviceTime) driver).getDeviceTime();
    }
}
