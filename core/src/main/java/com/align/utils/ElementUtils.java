package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ElementUtils {
    private ElementUtils() {}

    public static String getAttribute(AppiumDriver driver, By locator, String attribute) {
        return WaitUtils.waitForVisible(driver, locator, 10).getAttribute(attribute);
    }

    public static String getText(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getText();
    }

    public static boolean isEnabled(AppiumDriver driver, By locator) {
        try {
            return WaitUtils.waitForVisible(driver, locator, 5).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSelected(AppiumDriver driver, By locator) {
        try {
            return WaitUtils.waitForVisible(driver, locator, 5).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void highlightElement(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='3px solid red'", element);
    }

    public static Point getElementLocation(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getLocation();
    }

    public static Dimension getElementSize(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getSize();
    }

    public static int getElementCount(AppiumDriver driver, By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.size();
    }
}
