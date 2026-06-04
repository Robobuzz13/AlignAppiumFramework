package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardUtils {
    private static final Logger log = LoggerFactory.getLogger(KeyboardUtils.class);

    private KeyboardUtils() {}

    public static void hideKeyboard(AppiumDriver driver) {
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            log.warn("Could not hide keyboard: {}", e.getMessage());
        }
    }

    public static boolean isKeyboardShown(AppiumDriver driver) {
        try {
            return driver.isKeyboardShown();
        } catch (Exception e) {
            return false;
        }
    }

    public static void pressEnter(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.ENTER);
    }

    public static void pressBack(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.BACK);
    }

    public static void pressHome(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.HOME);
    }

    public static void pressAndroidKey(AppiumDriver driver, AndroidKey key) {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(key));
        } else {
            log.warn("pressAndroidKey called on non-Android driver — skipped");
        }
    }

    public static void typeWithKeyboard(AppiumDriver driver, By locator, String text) {
        WebElement element = WaitUtils.waitForClickable(driver, locator, 10);
        element.click();
        element.clear();
        element.sendKeys(text);
        if (isKeyboardShown(driver)) {
            hideKeyboard(driver);
        }
    }
}
