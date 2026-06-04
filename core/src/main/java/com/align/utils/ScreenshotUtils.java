package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtils() {}

    public static byte[] capture(AppiumDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static Path captureAndSave(AppiumDriver driver, String testName) {
        try {
            byte[] data = capture(driver);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String fileName = testName + "_" + LocalDateTime.now().format(TS) + ".png";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, data);
            log.info("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to save screenshot", e);
            return null;
        }
    }
}
