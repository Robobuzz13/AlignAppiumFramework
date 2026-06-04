package com.align.tests;

import com.align.steps.OnboardingSteps;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class OnboardingFlowTest extends BaseTest {

    private final OnboardingSteps steps = new OnboardingSteps();

    @Test(description = "Onboarding flow of the app")
    @Description("Drives the full onboarding flow of the app")
    public void onboardingThroughSignup() {
        steps.verifyInsightAndAdvance();
        steps.advanceThroughSplashCarousel();
        steps.signUpWithRandomCredentials();
        steps.enterRandomName();
        steps.fillBirthDetails();
        steps.confirmBirthChartSummary();
        steps.passChartGenerationInsight();
        steps.continueFromLocationAccess();
        steps.allowLocationPermission();
        steps.confirmGpsDialog();
        steps.enableGpsAndReturnToApp();
        steps.advancePastLocationAccess();
        steps.dismissInterstitialInsights();
        steps.selectOptionAndContinue("Explore My Birth Chart");
        steps.selectOptionAndContinue("Google Search");
        steps.verifyPaymentPage();
    }
}
