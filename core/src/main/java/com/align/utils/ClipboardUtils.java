package com.align.utils;

import io.appium.java_client.AppiumDriver;

public class ClipboardUtils {
    private ClipboardUtils() {}

    public static void setClipboard(AppiumDriver driver, String text) {
        driver.setClipboardText(text);
    }

    public static String getClipboard(AppiumDriver driver) {
        return driver.getClipboardText();
    }

    public static void clearClipboard(AppiumDriver driver) {
        driver.setClipboardText("");
    }
}
