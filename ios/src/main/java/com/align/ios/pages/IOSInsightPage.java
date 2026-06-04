package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.InsightPage;
import org.openqa.selenium.By;

public class IOSInsightPage extends BasePage implements InsightPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By NEXT_BUTTON  = By.xpath("//XCUIElementTypeButton[@name='Next']");
    private static final By INSIGHT_TEXT = By.xpath("//XCUIElementTypeStaticText[@name='insight_text']");

    @Override
    public boolean isNextButtonVisible() {
        return isElementVisible(NEXT_BUTTON);
    }

    @Override
    public void tapNext() {
        tap(NEXT_BUTTON);
    }

    @Override
    public String getInsightText() {
        return getText(INSIGHT_TEXT);
    }
}
