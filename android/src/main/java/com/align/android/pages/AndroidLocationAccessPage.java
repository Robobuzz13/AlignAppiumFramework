package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.LocationAccessPage;
import org.openqa.selenium.By;

public class AndroidLocationAccessPage extends BasePage implements LocationAccessPage {

    // Locators captured live from com.dailyinsights Location Access screen.
    // The title reuses the txtSignup id, so confirm by its text.
    private static final By TITLE           = By.id("com.dailyinsights:id/txtSignup");
    private static final By CONTINUE_BUTTON = By.id("com.dailyinsights:id/txtContinue");

    private static final String TITLE_TEXT = "Location Access";

    @Override
    public boolean isLocationAccessDisplayed() {
        return isElementVisible(TITLE) && TITLE_TEXT.equalsIgnoreCase(getText(TITLE).trim());
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
