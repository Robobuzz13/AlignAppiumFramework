package com.align.listeners;

import com.align.driver.DriverManager;
import com.align.reporting.ExtentManager;
import com.align.utils.ScreenshotUtils;
import com.align.utils.VideoUtils;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Base64;

public class TestListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        log.info("Starting test: {}", testName);
        ExtentManager.createTest(testName, description);
        if (VideoUtils.isEnabled() && DriverManager.hasDriver()) {
            VideoUtils.startRecording(DriverManager.getDriver());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info("PASS: {}", testName);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) test.pass("Test passed");

        if (VideoUtils.isEnabled() && DriverManager.hasDriver()) {
            boolean saveOnPass = Boolean.parseBoolean(
                    System.getProperty("video.save.on.pass", "false"));
            if (saveOnPass) {
                VideoUtils.stopAndSave(DriverManager.getDriver(), testName);
            } else {
                VideoUtils.stopAndDiscard(DriverManager.getDriver());
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("FAIL: {} — {}", testName, result.getThrowable().getMessage());

        if (DriverManager.hasDriver()) {
            byte[] screenshot = ScreenshotUtils.capture(DriverManager.getDriver());

            Allure.addAttachment("Screenshot on Failure",
                    "image/png", new ByteArrayInputStream(screenshot), "png");

            ExtentTest test = ExtentManager.getTest();
            if (test != null) {
                try {
                    test.fail(result.getThrowable(),
                            MediaEntityBuilder.createScreenCaptureFromBase64String(
                                    Base64.getEncoder().encodeToString(screenshot)).build());
                } catch (Exception e) {
                    log.warn("Failed to attach screenshot to Extent: {}", e.getMessage());
                }
            }

            if (VideoUtils.isEnabled()) {
                Path videoPath = VideoUtils.stopAndSave(DriverManager.getDriver(), testName);
                if (videoPath != null && test != null) {
                    test.info("Video: " + videoPath.toAbsolutePath());
                }
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.warn("SKIP: {}", testName);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.skip(result.getThrowable() != null
                    ? result.getThrowable().getMessage() : "Skipped");
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        log.info("Suite finished: passed={}, failed={}, skipped={}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }
}
