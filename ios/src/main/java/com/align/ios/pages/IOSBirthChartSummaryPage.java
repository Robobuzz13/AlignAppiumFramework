package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.BirthChartSummaryPage;
import org.openqa.selenium.By;

public class IOSBirthChartSummaryPage extends BasePage implements BirthChartSummaryPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By EDIT_BIRTH_INFO = By.xpath("//XCUIElementTypeStaticText[@name='Edit Birth Information']");
    private static final By CONTINUE_BUTTON = By.xpath("//XCUIElementTypeButton[@name='Continue']");

    @Override
    public boolean isEditBirthInfoDisplayed() {
        return isElementVisible(EDIT_BIRTH_INFO);
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
