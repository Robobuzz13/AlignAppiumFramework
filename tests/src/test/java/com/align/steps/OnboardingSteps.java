package com.align.steps;

import com.align.driver.DriverManager;
import com.align.factory.PageFactory;
import com.align.pages.BirthChartSummaryPage;
import com.align.pages.BirthDetailsPage;
import com.align.pages.GpsDialogPage;
import com.align.pages.InsightPage;
import com.align.pages.LocationAccessPage;
import com.align.pages.LocationSettingsPage;
import com.align.pages.NamePage;
import com.align.pages.OptionListPage;
import com.align.pages.SignupPage;
import com.align.pages.SplashCarouselPage;
import com.align.utils.PermissionUtils;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.UUID;

/**
 * Reusable onboarding flow steps for com.dailyinsights. Each method is one Allure {@link Step}
 * that drives a single screen (assertion + interaction), keeping test classes thin — they only
 * orchestrate the sequence of steps.
 */
public class OnboardingSteps {

    // Guards the swipe loop so a missing signup screen fails fast instead of looping forever.
    private static final int MAX_SPLASH_SWIPES = 10;

    @Step("Verify the insight screen and tap Next")
    public void verifyInsightAndAdvance() {
        InsightPage insightPage = PageFactory.getInsightPage();
        Assert.assertTrue(insightPage.isNextButtonVisible(), "Next button should be visible on insight screen");
        insightPage.tapNext();
    }

    @Step("Verify the splash carousel and swipe through every splash to the signup screen")
    public void advanceThroughSplashCarousel() {
        SplashCarouselPage splashPage = PageFactory.getSplashCarouselPage();
        Assert.assertTrue(splashPage.isSkipButtonVisible(), "Skip button should be visible on splash carousel");
        Assert.assertTrue(splashPage.getSplashOptionCount() > 1,
                "Splash carousel should offer more than one splash option");

        SignupPage signupPage = PageFactory.getSignupPage();
        for (int i = 0; i < MAX_SPLASH_SWIPES && !signupPage.isSignupScreenVisible(); i++) {
            splashPage.swipeToNextSplash();
        }
        Assert.assertTrue(signupPage.isSignupScreenVisible(), "Signup screen should be visible after swiping splashes");
    }

    @Step("Sign up with a random email and password")
    public void signUpWithRandomCredentials() {
        SignupPage signupPage = PageFactory.getSignupPage();
        String email = "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String password = "Pass" + UUID.randomUUID().toString().substring(0, 6) + "!";
        signupPage.enterEmail(email);
        signupPage.enterPassword(password);
        signupPage.tapSignup();
    }

    @Step("Enter a random name and tap Next")
    public void enterRandomName() {
        NamePage namePage = PageFactory.getNamePage();
        Assert.assertTrue(namePage.isNameScreenVisible(), "Name screen should be visible after signup");
        namePage.enterName("QA " + UUID.randomUUID().toString().substring(0, 5));
        namePage.tapNext();
    }

    @Step("Fill a random birth date, time and location, then tap Next")
    public void fillBirthDetails() {
        BirthDetailsPage birthPage = PageFactory.getBirthDetailsPage();
        Assert.assertTrue(birthPage.isBirthDetailsScreenVisible(), "Birth details screen should be visible after name");
        birthPage.setBirthDate();
        birthPage.setBirthTime();
        birthPage.setBirthLocation("London");
        birthPage.tapNext();
    }

    @Step("Verify Edit Birth Information on the summary and tap Continue")
    public void confirmBirthChartSummary() {
        BirthChartSummaryPage summaryPage = PageFactory.getBirthChartSummaryPage();
        Assert.assertTrue(summaryPage.isEditBirthInfoDisplayed(),
                "Edit Birth Information should be displayed on the birth-chart summary");
        summaryPage.tapContinue();
    }

    @Step("Verify the personalized insight after chart generation and tap Next")
    public void passChartGenerationInsight() {
        // The post-chart insight reuses the opening insight layout, so InsightPage applies again.
        InsightPage chartInsight = PageFactory.getInsightPage();
        Assert.assertTrue(chartInsight.isNextButtonVisible(),
                "Personalized insight screen should appear after chart generation");
        chartInsight.tapNext();
    }

    @Step("Verify the Location Access screen and tap Continue")
    public void continueFromLocationAccess() {
        LocationAccessPage locationPage = PageFactory.getLocationAccessPage();
        Assert.assertTrue(locationPage.isLocationAccessDisplayed(),
                "Location Access screen should be displayed after the personalized insight");
        locationPage.tapContinue();
    }

    @Step("Allow the system location permission")
    public void allowLocationPermission() {
        PermissionUtils.allowPermission(DriverManager.getDriver());
    }

    @Step("Confirm the GPS-enable dialog with YES")
    public void confirmGpsDialog() {
        GpsDialogPage gpsDialog = PageFactory.getGpsDialogPage();
        Assert.assertTrue(gpsDialog.isGpsDialogDisplayed(),
                "GPS-enable dialog should be displayed after allowing location permission");
        gpsDialog.tapYes();
    }

    @Step("Enable GPS in system settings and return to the app")
    public void enableGpsAndReturnToApp() {
        LocationSettingsPage locationSettings = PageFactory.getLocationSettingsPage();
        Assert.assertTrue(locationSettings.isLocationSettingsDisplayed(),
                "System Location settings should open after tapping YES on the GPS dialog");
        locationSettings.enableLocation();
        locationSettings.returnToApp();
    }

    @Step("Advance past the Location Access screen once GPS is enabled")
    public void advancePastLocationAccess() {
        LocationAccessPage locationPage = PageFactory.getLocationAccessPage();
        Assert.assertTrue(locationPage.isLocationAccessDisplayed(),
                "App should return to the Location Access screen after enabling GPS");
        locationPage.tapContinue();
        Assert.assertFalse(locationPage.isLocationAccessDisplayed(),
                "Location Access screen should be dismissed once GPS is enabled and Continue is tapped");
    }

    @Step("Select option '{option}' from the list and tap Continue")
    public void selectOptionAndContinue(String option) {
        OptionListPage listPage = PageFactory.getOptionListPage();
        Assert.assertTrue(listPage.isDisplayed(), "A single-select list screen should be displayed");
        listPage.selectOption(option);
        listPage.tapContinue();
    }
}
