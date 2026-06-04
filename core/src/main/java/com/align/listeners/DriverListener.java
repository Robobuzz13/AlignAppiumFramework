package com.align.listeners;

import com.align.driver.DriverManager;
import com.align.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

// Use DriverListener as a standalone screenshot listener OR alongside TestListener.
// When both are registered, onTestFailure captures two screenshots (one per listener).
// TestListener already captures to both Allure and Extent; DriverListener adds a disk copy.
public class DriverListener extends TestListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(DriverListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        if (DriverManager.hasDriver()) {
            log.debug("DriverListener: capturing screenshot for failed test {}",
                    result.getMethod().getMethodName());
            ScreenshotUtils.captureAndSave(DriverManager.getDriver(),
                    result.getMethod().getMethodName());
        }
    }
}
