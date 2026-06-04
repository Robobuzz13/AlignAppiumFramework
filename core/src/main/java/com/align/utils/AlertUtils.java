package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AlertUtils {
    private static final Logger log = LoggerFactory.getLogger(AlertUtils.class);

    private AlertUtils() {}

    public static void acceptAlert(AppiumDriver driver) {
        waitForAlert(driver);
        driver.switchTo().alert().accept();
        log.debug("Alert accepted");
    }

    public static void dismissAlert(AppiumDriver driver) {
        waitForAlert(driver);
        driver.switchTo().alert().dismiss();
        log.debug("Alert dismissed");
    }

    public static String getAlertText(AppiumDriver driver) {
        waitForAlert(driver);
        return driver.switchTo().alert().getText();
    }

    public static boolean isAlertPresent(AppiumDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static void handlePermissionAlert(AppiumDriver driver, String action) {
        if (!isAlertPresent(driver)) return;
        if ("allow".equalsIgnoreCase(action)) {
            acceptAlert(driver);
        } else {
            dismissAlert(driver);
        }
    }

    private static void waitForAlert(AppiumDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
    }
}
