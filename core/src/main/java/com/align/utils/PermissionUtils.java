package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PermissionUtils {
    private static final Logger log = LoggerFactory.getLogger(PermissionUtils.class);

    private static final By ALLOW_BUTTON = By.id("com.android.permissioncontroller:id/permission_allow_button");
    private static final By DENY_BUTTON  = By.id("com.android.permissioncontroller:id/permission_deny_button");
    private static final By ALLOW_FOREGROUND = By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
    private static final By IOS_ALLOW = By.xpath("//XCUIElementTypeButton[@name='Allow']");
    private static final By IOS_DENY  = By.xpath("//XCUIElementTypeButton[@name=\"Don't Allow\"]");

    private PermissionUtils() {}

    public static void allowPermission(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            if (WaitUtils.isVisible(driver, ALLOW_BUTTON, 3)) {
                driver.findElement(ALLOW_BUTTON).click();
            } else if (WaitUtils.isVisible(driver, ALLOW_FOREGROUND, 3)) {
                driver.findElement(ALLOW_FOREGROUND).click();
            }
        } else {
            if (WaitUtils.isVisible(driver, IOS_ALLOW, 3)) {
                driver.findElement(IOS_ALLOW).click();
            }
        }
    }

    public static void denyPermission(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            if (WaitUtils.isVisible(driver, DENY_BUTTON, 3)) {
                driver.findElement(DENY_BUTTON).click();
            }
        } else {
            if (WaitUtils.isVisible(driver, IOS_DENY, 3)) {
                driver.findElement(IOS_DENY).click();
            }
        }
    }

    public static void grantPermission(AppiumDriver driver, String packageName, String permission) {
        if (driver instanceof AndroidDriver) {
            String cmd = String.format("pm grant %s %s", packageName, permission);
            ((AndroidDriver) driver).executeScript("mobile: shell", Map.of("command", cmd));
            log.info("Granted permission {} to {}", permission, packageName);
        } else {
            log.warn("grantPermission via ADB is Android-only");
        }
    }

    public static void resetPermissions(AppiumDriver driver, String packageName) {
        if (driver instanceof AndroidDriver) {
            String cmd = "pm reset-permissions " + packageName;
            ((AndroidDriver) driver).executeScript("mobile: shell", Map.of("command", cmd));
            log.info("Reset permissions for {}", packageName);
        }
    }
}
