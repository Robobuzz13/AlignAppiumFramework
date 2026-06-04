package com.align.utils;

import com.align.config.ConfigLoader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.screenrecording.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.screenrecording.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class VideoUtils {
    private static final Logger log = LoggerFactory.getLogger(VideoUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private VideoUtils() {}

    public static boolean isEnabled() {
        String env = ConfigLoader.getInstance().getProperty("env", "local");
        if ("cloud".equalsIgnoreCase(env)) return false;
        return ConfigLoader.getInstance().getBooleanProperty("video.recording.enabled", false);
    }

    public static void startRecording(AppiumDriver driver) {
        if (!(driver instanceof CanRecordScreen)) {
            log.warn("Driver does not support screen recording");
            return;
        }
        try {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).startRecordingScreen(
                        new AndroidStartScreenRecordingOptions()
                                .withBitRate(4_000_000)
                                .withTimeLimit(Duration.ofMinutes(30)));
            } else if (driver instanceof IOSDriver) {
                ((IOSDriver) driver).startRecordingScreen(
                        new IOSStartScreenRecordingOptions()
                                .withVideoQuality(IOSStartScreenRecordingOptions.VideoQuality.MEDIUM)
                                .withTimeLimit(Duration.ofMinutes(30)));
            }
            log.debug("Screen recording started");
        } catch (Exception e) {
            log.warn("Failed to start screen recording: {}", e.getMessage());
        }
    }

    public static Path stopAndSave(AppiumDriver driver, String testName) {
        try {
            String base64Video = ((CanRecordScreen) driver).stopRecordingScreen();
            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            Path dir = Paths.get("target", "videos");
            Files.createDirectories(dir);
            String fileName = testName + "_" + LocalDateTime.now().format(TS) + ".mp4";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, videoBytes);
            log.info("Video saved: {}", filePath);
            return filePath;
        } catch (Exception e) {
            log.warn("Failed to save video: {}", e.getMessage());
            return null;
        }
    }

    public static void stopAndDiscard(AppiumDriver driver) {
        try {
            ((CanRecordScreen) driver).stopRecordingScreen();
            log.debug("Screen recording discarded");
        } catch (Exception e) {
            log.warn("Failed to stop screen recording: {}", e.getMessage());
        }
    }
}
