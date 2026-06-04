package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.GpsDialogPage;
import org.openqa.selenium.By;

public class IOSGpsDialogPage extends BasePage implements GpsDialogPage {

    // TODO: inspect on real iOS device — iOS has no equivalent in-app GPS dialog; placeholders.
    private static final By MESSAGE = By.xpath("//XCUIElementTypeStaticText[contains(@name,'GPS')]");
    private static final By YES     = By.xpath("//XCUIElementTypeButton[@name='YES']");
    private static final By NO      = By.xpath("//XCUIElementTypeButton[@name='NO']");

    @Override
    public boolean isGpsDialogDisplayed() {
        return isElementVisible(MESSAGE);
    }

    @Override
    public void tapYes() {
        tap(YES);
    }

    @Override
    public void tapNo() {
        tap(NO);
    }
}
