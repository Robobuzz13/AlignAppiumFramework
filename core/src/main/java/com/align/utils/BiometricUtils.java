package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AuthenticatesByFinger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BiometricUtils {
    private static final Logger log = LoggerFactory.getLogger(BiometricUtils.class);

    private BiometricUtils() {}

    public static void simulateFingerprintSuccess(AppiumDriver driver) {
        requireAndroidFingerprint(driver, "simulateFingerprintSuccess");
        ((AuthenticatesByFinger) driver).fingerPrint(1);
        log.info("Fingerprint success simulated (fingerprintId=1)");
    }

    public static void simulateFingerprintFailure(AppiumDriver driver) {
        requireAndroidFingerprint(driver, "simulateFingerprintFailure");
        ((AuthenticatesByFinger) driver).fingerPrint(2);
        log.info("Fingerprint failure simulated (fingerprintId=2)");
    }

    public static void simulateFaceIdSuccess(AppiumDriver driver) {
        if (driver instanceof AuthenticatesByFinger) {
            throw new UnsupportedOperationException("simulateFaceIdSuccess() is iOS simulator only");
        }
        driver.executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", true));
        log.info("Face ID success simulated");
    }

    public static void simulateFaceIdFailure(AppiumDriver driver) {
        if (driver instanceof AuthenticatesByFinger) {
            throw new UnsupportedOperationException("simulateFaceIdFailure() is iOS simulator only");
        }
        driver.executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", false));
        log.info("Face ID failure simulated");
    }

    private static void requireAndroidFingerprint(AppiumDriver driver, String method) {
        if (!(driver instanceof AuthenticatesByFinger)) {
            throw new UnsupportedOperationException(method + "() is Android emulator only");
        }
    }
}
