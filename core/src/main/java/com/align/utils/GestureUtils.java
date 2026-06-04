package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class GestureUtils {
    private GestureUtils() {}

    private static final int SWIPE_DURATION_MS = 600;
    private static final int MAX_SCROLL_SWIPES = 5;

    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        swipe(driver, x, (int)(size.height * 0.8), x, (int)(size.height * 0.2));
    }

    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        swipe(driver, x, (int)(size.height * 0.2), x, (int)(size.height * 0.8));
    }

    public static void swipeLeft(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y = size.height / 2;
        swipe(driver, (int)(size.width * 0.8), y, (int)(size.width * 0.2), y);
    }

    public static void swipeRight(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y = size.height / 2;
        swipe(driver, (int)(size.width * 0.2), y, (int)(size.width * 0.8), y);
    }

    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(SWIPE_DURATION_MS),
                        PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void scrollToElement(AppiumDriver driver, By locator) {
        for (int i = 0; i < MAX_SCROLL_SWIPES; i++) {
            if (WaitUtils.isVisible(driver, locator, 2)) return;
            swipeUp(driver);
        }
        throw new RuntimeException("Element not found after " + MAX_SCROLL_SWIPES + " swipes: " + locator);
    }

    public static void scrollToText(AppiumDriver driver, String text) {
        scrollToElement(driver,
                By.xpath("//*[@text='" + text + "' or @label='" + text + "']"));
    }

    public static void longPress(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(1500),
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void doubleTap(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void tap(AppiumDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void pinchZoomIn(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        Point center = element.getLocation();
        Dimension size = element.getSize();
        int cx = center.x + size.width / 2;
        int cy = center.y + size.height / 2;

        PointerInput f1 = new PointerInput(PointerInput.Kind.TOUCH, "f1");
        PointerInput f2 = new PointerInput(PointerInput.Kind.TOUCH, "f2");

        Sequence seq1 = new Sequence(f1, 0)
                .addAction(f1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy))
                .addAction(f1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx - 150, cy))
                .addAction(f1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence seq2 = new Sequence(f2, 0)
                .addAction(f2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy))
                .addAction(f2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx + 150, cy))
                .addAction(f2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(seq1, seq2));
    }

    public static void pinchZoomOut(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        Point center = element.getLocation();
        Dimension size = element.getSize();
        int cx = center.x + size.width / 2;
        int cy = center.y + size.height / 2;

        PointerInput f1 = new PointerInput(PointerInput.Kind.TOUCH, "f1");
        PointerInput f2 = new PointerInput(PointerInput.Kind.TOUCH, "f2");

        Sequence seq1 = new Sequence(f1, 0)
                .addAction(f1.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), cx - 150, cy))
                .addAction(f1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx, cy))
                .addAction(f1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence seq2 = new Sequence(f2, 0)
                .addAction(f2.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), cx + 150, cy))
                .addAction(f2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx, cy))
                .addAction(f2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(seq1, seq2));
    }

    public static void dragAndDrop(AppiumDriver driver, By source, By target) {
        WebElement srcEl = WaitUtils.waitForVisible(driver, source, 10);
        WebElement tgtEl = WaitUtils.waitForVisible(driver, target, 10);
        Point srcPt = srcEl.getLocation();
        Point tgtPt = tgtEl.getLocation();
        int srcCx = srcPt.x + srcEl.getSize().width / 2;
        int srcCy = srcPt.y + srcEl.getSize().height / 2;
        int tgtCx = tgtPt.x + tgtEl.getSize().width / 2;
        int tgtCy = tgtPt.y + tgtEl.getSize().height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), srcCx, srcCy))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(1000),
                        PointerInput.Origin.viewport(), tgtCx, tgtCy))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }
}
