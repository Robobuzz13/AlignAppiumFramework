package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.NamePage;
import org.openqa.selenium.By;

public class IOSNamePage extends BasePage implements NamePage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By NAME_FIELD  = By.xpath("//XCUIElementTypeTextField[@name='name']");
    private static final By NEXT_BUTTON = By.xpath("//XCUIElementTypeButton[@name='Next']");

    @Override
    public boolean isNameScreenVisible() {
        return isElementVisible(NAME_FIELD);
    }

    @Override
    public void enterName(String name) {
        sendKeys(NAME_FIELD, name);
    }

    @Override
    public void tapNext() {
        tap(NEXT_BUTTON);
    }
}
