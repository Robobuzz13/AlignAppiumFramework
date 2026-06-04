package com.align.reporting;

import com.align.config.ConfigLoader;
import com.align.driver.DriverManager;
import com.align.utils.ScreenshotUtils;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * Bridges Allure {@code @Step} events into the Extent report and, optionally, captures a
 * screenshot after every step. Allure weaves @Step via aspectj and fires lifecycle events;
 * this listener mirrors each started step onto the current ExtentTest. When the config flag
 * {@code screenshot.each.step} is true, it also attaches a screenshot of the result of each
 * step to both Allure and Extent. Registered via
 * META-INF/services/io.qameta.allure.listener.StepLifecycleListener.
 */
public class ExtentStepListener implements StepLifecycleListener {

    @Override
    public void afterStepStart(StepResult result) {
        ExtentTest test = ExtentManager.getTest();
        if (test != null && result != null && result.getName() != null) {
            test.info(result.getName());
        }
    }

    @Override
    public void afterStepStop(StepResult result) {
        if (!screenshotEachStep() || !DriverManager.hasDriver()) {
            return;
        }
        String label = (result != null && result.getName() != null) ? result.getName() : "step";
        try {
            byte[] png = ScreenshotUtils.capture(DriverManager.getDriver());
            Allure.addAttachment(label, "image/png", new ByteArrayInputStream(png), "png");
            ExtentTest test = ExtentManager.getTest();
            if (test != null) {
                test.info(label, MediaEntityBuilder
                        .createScreenCaptureFromBase64String(Base64.getEncoder().encodeToString(png))
                        .build());
            }
        } catch (Exception ignored) {
            // Per-step screenshots are best-effort; never fail a test because of them.
        }
    }

    private boolean screenshotEachStep() {
        return ConfigLoader.getInstance().getBooleanProperty("screenshot.each.step", false);
    }
}
