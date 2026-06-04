package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationUtils {
    private static final Logger log = LoggerFactory.getLogger(NotificationUtils.class);

    private NotificationUtils() {}

    public static void openNotificationCenter(AppiumDriver driver) {
        requireAndroid(driver, "openNotificationCenter");
        ((AndroidDriver) driver).openNotifications();
        log.info("Notification center opened");
    }

    public static void clearNotifications(AppiumDriver driver) {
        requireAndroid(driver, "clearNotifications");
        ((AndroidDriver) driver).openNotifications();
        By clearAll = By.id("com.android.systemui:id/dismiss_text");
        if (WaitUtils.isVisible(driver, clearAll, 3)) {
            driver.findElement(clearAll).click();
        }
        log.info("Notifications cleared");
    }

    public static List<String> getNotificationText(AppiumDriver driver) {
        requireAndroid(driver, "getNotificationText");
        ((AndroidDriver) driver).openNotifications();
        By notifTitle = By.id("android:id/title");
        List<WebElement> notifications = driver.findElements(notifTitle);
        return notifications.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android-only");
        }
    }
}
