package com.align.reporting;

import com.aventstack.extentreports.ExtentTest;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

/**
 * Bridges Allure {@code @Step} events into the Extent report. Allure weaves @Step via
 * aspectj and fires lifecycle events; this listener mirrors each started step onto the
 * current ExtentTest so the Extent report shows the same step breakdown as Allure.
 * Registered via META-INF/services/io.qameta.allure.listener.StepLifecycleListener.
 */
public class ExtentStepListener implements StepLifecycleListener {

    @Override
    public void afterStepStart(StepResult result) {
        ExtentTest test = ExtentManager.getTest();
        if (test != null && result != null && result.getName() != null) {
            test.info(result.getName());
        }
    }
}
