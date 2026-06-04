package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.BirthChartSummaryPage;
import org.openqa.selenium.By;

public class AndroidBirthChartSummaryPage extends BasePage implements BirthChartSummaryPage {

    // Locators captured live from com.dailyinsights birth-chart summary screen.
    private static final By EDIT_BIRTH_INFO = By.id("com.dailyinsights:id/txtEditBirthInfo");
    private static final By CONTINUE_BUTTON = By.id("com.dailyinsights:id/imgNext");

    @Override
    public boolean isEditBirthInfoDisplayed() {
        return isElementVisible(EDIT_BIRTH_INFO);
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
