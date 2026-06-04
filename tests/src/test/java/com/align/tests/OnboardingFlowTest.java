package com.align.tests;

import com.align.factory.PageFactory;
import com.align.pages.InsightPage;
import com.align.pages.SplashCarouselPage;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OnboardingFlowTest extends BaseTest {

    @Test(description = "Next button leads to splash carousel with Skip and multiple options")
    @Description("Validates the onboarding hand-off: insight screen Next button navigates "
            + "to the splash carousel, which shows a Skip button and more than one splash option")
    public void nextLeadsToSplashWithSkipAndOptions() {
        InsightPage insightPage = PageFactory.getInsightPage();
        Assert.assertTrue(insightPage.isNextButtonVisible(), "Next button should be visible on insight screen");

        advanceToSplash(insightPage);

        SplashCarouselPage splashPage = PageFactory.getSplashCarouselPage();
        Assert.assertTrue(splashPage.isSkipButtonVisible(), "Skip button should be visible on splash carousel");
        Assert.assertTrue(splashPage.getSplashOptionCount() > 1,
                "Splash carousel should offer more than one splash option");
    }

    @Step("Tap Next to advance to splash carousel")
    private void advanceToSplash(InsightPage insightPage) {
        insightPage.tapNext();
    }
}
