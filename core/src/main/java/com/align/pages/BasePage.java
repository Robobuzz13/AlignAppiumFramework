package com.align.pages;

import com.align.driver.DriverManager;
import com.align.exceptions.PageException;
import com.align.utils.WaitUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {
    private static final Logger log = LoggerFactory.getLogger(BasePage.class);
    private static final int DEFAULT_TIMEOUT = 10;

    protected AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected String getPageName() {
        return getClass().getSimpleName();
    }

    protected WebElement waitForVisible(By locator) {
        return waitForVisible(locator, DEFAULT_TIMEOUT);
    }

    protected WebElement waitForVisible(By locator, int timeoutSeconds) {
        try {
            return WaitUtils.waitForVisible(getDriver(), locator, timeoutSeconds);
        } catch (Exception e) {
            throw new PageException(getPageName(), locator.toString(),
                    "Element not visible after " + timeoutSeconds + "s", e);
        }
    }

    protected void tap(By locator) {
        log.debug("[{}] tap: {}", getPageName(), locator);
        try {
            WaitUtils.waitForClickable(getDriver(), locator, DEFAULT_TIMEOUT).click();
        } catch (Exception e) {
            throw new PageException(getPageName(), locator.toString(), "Not clickable", e);
        }
    }

    protected void sendKeys(By locator, String text) {
        log.debug("[{}] sendKeys: {} = '{}'", getPageName(), locator, text);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isElementVisible(By locator) {
        return WaitUtils.isVisible(getDriver(), locator, 5);
    }

    protected void waitForInvisible(By locator) {
        WaitUtils.waitForInvisible(getDriver(), locator, DEFAULT_TIMEOUT);
    }

    protected void waitForInvisible(By locator, int timeoutSeconds) {
        WaitUtils.waitForInvisible(getDriver(), locator, timeoutSeconds);
    }
}
