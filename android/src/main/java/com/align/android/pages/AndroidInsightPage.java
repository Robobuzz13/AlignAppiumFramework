package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.InsightPage;
import org.openqa.selenium.By;

public class AndroidInsightPage extends BasePage implements InsightPage {

    // Locators captured live from com.dailyinsights DayNakshatraActivity
    private static final By NEXT_BUTTON  = By.id("com.dailyinsights:id/txtContinue");
    private static final By INSIGHT_TEXT = By.id("com.dailyinsights:id/txtContent");

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
