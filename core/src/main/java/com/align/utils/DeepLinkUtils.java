package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DeepLinkUtils {
    private static final Logger log = LoggerFactory.getLogger(DeepLinkUtils.class);

    private DeepLinkUtils() {}

    public static void openDeepLink(AppiumDriver driver, String url) {
        log.info("Opening deep link: {}", url);
        if (driver instanceof AndroidDriver) {
            driver.executeScript("mobile: deepLink", Map.of(
                    "url", url,
                    "package", getPackageName(driver)
            ));
        } else {
            driver.executeScript("mobile: openUrl", Map.of("url", url));
        }
    }

    public static void openUniversalLink(AppiumDriver driver, String url) {
        log.info("Opening universal link: {}", url);
        driver.executeScript("mobile: openUrl", Map.of("url", url));
    }

    private static String getPackageName(AppiumDriver driver) {
        try {
            return (String) ((AndroidDriver) driver).getCurrentPackage();
        } catch (Exception e) {
            return "";
        }
    }
}
