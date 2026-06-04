package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.SplashCarouselPage;
import com.align.utils.GestureUtils;
import org.openqa.selenium.By;

public class IOSSplashCarouselPage extends BasePage implements SplashCarouselPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By SKIP_BUTTON = By.xpath("//XCUIElementTypeButton[@name='skip']");
    private static final By PAGE_DOT    = By.xpath("//XCUIElementTypeImage[@name='page_dot']");
    private static final By TITLE       = By.xpath("//XCUIElementTypeStaticText[@name='splash_title']");

    @Override
    public boolean isSkipButtonVisible() {
        return isElementVisible(SKIP_BUTTON);
    }

    @Override
    public int getSplashOptionCount() {
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
