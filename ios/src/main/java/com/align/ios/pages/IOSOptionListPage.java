package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.OptionListPage;
import org.openqa.selenium.By;

public class IOSOptionListPage extends BasePage implements OptionListPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By LIST            = By.xpath("//XCUIElementTypeCollectionView");
    private static final By CONTINUE_BUTTON = By.xpath("//XCUIElementTypeButton[@name='Continue']");

    @Override
    public boolean isDisplayed() {
        return isElementVisible(LIST);
    }

    @Override
    public String getTitle() {
        return getText(By.xpath("(//XCUIElementTypeStaticText)[1]"));
    }

    @Override
    public void selectOption(String name) {
        tap(By.xpath("//XCUIElementTypeStaticText[@name='" + name + "']"));
    }

    @Override
    public void tapContinue() {
        tap(CONTINUE_BUTTON);
    }
}
