package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.OptionListPage;
import org.openqa.selenium.By;

public class AndroidOptionListPage extends BasePage implements OptionListPage {

    // Locators shared by every com.dailyinsights single-select list screen.
    private static final By LIST            = By.id("com.dailyinsights:id/rvInsightsList");
    private static final By TITLE           = By.id("com.dailyinsights:id/txtTitle");
    private static final By CONTINUE_BUTTON = By.id("com.dailyinsights:id/txtSubmit");

    @Override
    public boolean isDisplayed() {
        return isElementVisible(LIST);
    }

    @Override
    public String getTitle() {
        // The header title and each row reuse the txtTitle id; the header is the first match.
        return waitForVisible(LIST) != null ? getDriver().findElements(TITLE).get(0).getText() : "";
    }

    @Override
    public void selectOption(String name) {
        // Match the row by its text; tapping the title cell selects the row.
        By option = By.xpath("//*[@resource-id='com.dailyinsights:id/txtTitle' and @text='" + name + "']");
        tap(option);
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
