package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BiometricUtils {
    private static final Logger log = LoggerFactory.getLogger(BiometricUtils.class);

    private BiometricUtils() {}

    public static void simulateFingerprintSuccess(AppiumDriver driver) {
        requireAndroid(driver, "simulateFingerprintSuccess");
        ((AndroidDriver) driver).fingerPrint(1);
        log.info("Fingerprint success simulated (fingerprintId=1)");
    }

    public static void simulateFingerprintFailure(AppiumDriver driver) {
        requireAndroid(driver, "simulateFingerprintFailure");
        ((AndroidDriver) driver).fingerPrint(2);
        log.info("Fingerprint failure simulated (fingerprintId=2)");
    }

    public static void simulateFaceIdSuccess(AppiumDriver driver) {
        requireIOS(driver, "simulateFaceIdSuccess");
        ((IOSDriver) driver).executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", true));
        log.info("Face ID success simulated");
    }

    public static void simulateFaceIdFailure(AppiumDriver driver) {
        requireIOS(driver, "simulateFaceIdFailure");
        ((IOSDriver) driver).executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", false));
        log.info("Face ID failure simulated");
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android emulator only");
        }
    }

    private static void requireIOS(AppiumDriver driver, String method) {
        if (!(driver instanceof IOSDriver)) {
            throw new UnsupportedOperationException(method + "() is iOS simulator only");
        }
    }
}
