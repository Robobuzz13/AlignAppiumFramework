package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.clipboard.HasClipboard;

public class ClipboardUtils {
    private ClipboardUtils() {}

    private static HasClipboard clipboard(AppiumDriver driver) {
        return (HasClipboard) driver;
    }

    public static void setClipboard(AppiumDriver driver, String text) {
        clipboard(driver).setClipboardText(text);
    }

    public static String getClipboard(AppiumDriver driver) {
        return clipboard(driver).getClipboardText();
    }

    public static void clearClipboard(AppiumDriver driver) {
        clipboard(driver).setClipboardText("");
    }
}
