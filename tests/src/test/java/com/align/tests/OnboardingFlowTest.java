package com.align.tests;

import com.align.steps.OnboardingSteps;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class OnboardingFlowTest extends BaseTest {

    private final OnboardingSteps steps = new OnboardingSteps();

    @Test(description = "Onboarding: Next -> splash -> signup -> name -> birth details -> summary -> interests")
    @Description("Drives the full com.dailyinsights onboarding: insight Next, swipe through the "
            + "splash carousel to signup, sign up with random credentials, enter a random name, fill "
            + "random birth date/time/location, confirm the birth-chart summary, pass the post-chart "
            + "insight, grant location permission and enable GPS, then select an interest and an "
            + "acquisition source. Steps live in OnboardingSteps; this class only orchestrates them.")
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
        steps.selectOptionAndContinue("Explore My Birth Chart");
        steps.selectOptionAndContinue("Google Search");
    }
}
