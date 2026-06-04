package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.NamePage;
import org.openqa.selenium.By;

public class AndroidNamePage extends BasePage implements NamePage {

    // Locators captured live from com.dailyinsights name screen (post-signup).
    private static final By NAME_FIELD  = By.id("com.dailyinsights:id/edtUserName");
    private static final By NEXT_BUTTON = By.id("com.dailyinsights:id/txtContinue");

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
