package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.SplashCarouselPage;
import com.align.utils.GestureUtils;
import org.openqa.selenium.By;

public class AndroidSplashCarouselPage extends BasePage implements SplashCarouselPage {

    // Locators captured live from com.dailyinsights splash carousel
    private static final By SKIP_BUTTON = By.id("com.dailyinsights:id/txtSkip");
    private static final By PAGE_DOT    = By.id("com.dailyinsights:id/worm_dot");
    private static final By TITLE       = By.id("com.dailyinsights:id/txtTitle");

    @Override
    public boolean isSkipButtonVisible() {
        return isElementVisible(SKIP_BUTTON);
    }

    @Override
    public int getSplashOptionCount() {
        // One worm_dot per splash screen in the carousel.
        return getDriver().findElements(PAGE_DOT).size();
    }

    @Override
    public String getTitle() {
        return getText(TITLE);
    }

    @Override
    public void tapSkip() {
        tap(SKIP_BUTTON);
    }

    @Override
    public void swipeToNextSplash() {
        GestureUtils.swipeLeft(getDriver());
    }
}
