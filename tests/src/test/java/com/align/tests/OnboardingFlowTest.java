package com.align.tests;

import com.align.factory.PageFactory;
import com.align.pages.InsightPage;
import com.align.pages.SignupPage;
import com.align.pages.SplashCarouselPage;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class OnboardingFlowTest extends BaseTest {

    // Guards the swipe loop so a missing signup screen fails fast instead of looping forever.
    private static final int MAX_SPLASH_SWIPES = 10;

    @Test(description = "Onboarding: Next -> splash carousel -> swipe all -> signup with random credentials")
    @Description("Validates the full onboarding hand-off: insight Next button navigates to the "
            + "splash carousel with a Skip button and multiple options, swipes through every splash "
            + "screen to the signup screen, enters a random email/password and taps Signup")
    public void onboardingThroughSignup() {
        InsightPage insightPage = PageFactory.getInsightPage();
        Assert.assertTrue(insightPage.isNextButtonVisible(), "Next button should be visible on insight screen");

        advanceToSplash(insightPage);

        SplashCarouselPage splashPage = PageFactory.getSplashCarouselPage();
        Assert.assertTrue(splashPage.isSkipButtonVisible(), "Skip button should be visible on splash carousel");
        Assert.assertTrue(splashPage.getSplashOptionCount() > 1,
                "Splash carousel should offer more than one splash option");

        SignupPage signupPage = swipeThroughSplashesToSignup(splashPage);
        Assert.assertTrue(signupPage.isSignupScreenVisible(), "Signup screen should be visible after swiping splashes");

        signUpWithRandomCredentials(signupPage);
    }

    @Step("Tap Next to advance to splash carousel")
    private void advanceToSplash(InsightPage insightPage) {
        insightPage.tapNext();
    }

    @Step("Swipe through every splash screen until the signup screen appears")
    private SignupPage swipeThroughSplashesToSignup(SplashCarouselPage splashPage) {
        SignupPage signupPage = PageFactory.getSignupPage();
        for (int i = 0; i < MAX_SPLASH_SWIPES && !signupPage.isSignupScreenVisible(); i++) {
            splashPage.swipeToNextSplash();
        }
        return signupPage;
    }

    @Step("Enter random email/password and tap Signup")
    private void signUpWithRandomCredentials(SignupPage signupPage) {
        String email = "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String password = "Pass" + UUID.randomUUID().toString().substring(0, 6) + "!";
        signupPage.enterEmail(email);
        signupPage.enterPassword(password);
        signupPage.tapSignup();
    }
}
