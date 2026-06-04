package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.LocationAccessPage;
import org.openqa.selenium.By;

public class IOSLocationAccessPage extends BasePage implements LocationAccessPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By TITLE           = By.xpath("//XCUIElementTypeStaticText[@name='Location Access']");
    private static final By CONTINUE_BUTTON = By.xpath("//XCUIElementTypeButton[@name='Continue']");

    @Override
    public boolean isLocationAccessDisplayed() {
        return isElementVisible(TITLE);
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
