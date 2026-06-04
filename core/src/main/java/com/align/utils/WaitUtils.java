package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {
    private WaitUtils() {}

    public static WebElement waitForVisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitForInvisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean isVisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        try {
            waitForVisible(driver, locator, timeoutSeconds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement fluentWait(AppiumDriver driver, By locator,
                                        Duration timeout, Duration pollingInterval) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
