package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {}

    public static void pushFileToDevice(AppiumDriver driver, String localPath, String devicePath) {
        try {
            byte[] data = Files.readAllBytes(Path.of(localPath));
            driver.pushFile(devicePath, data);
            log.info("Pushed file {} to device path {}", localPath, devicePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to push file: " + localPath, e);
        }
    }

    public static byte[] pullFileFromDevice(AppiumDriver driver, String devicePath) {
        byte[] data = driver.pullFile(devicePath);
        log.info("Pulled file from device path {}", devicePath);
        return data;
    }

    public static void pushMediaFile(AppiumDriver driver, String localPath) {
        String fileName = new File(localPath).getName();
        pushFileToDevice(driver, localPath, "/sdcard/Pictures/" + fileName);
    }
}
