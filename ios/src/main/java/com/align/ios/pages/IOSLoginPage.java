package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.LoginPage;
import org.openqa.selenium.By;

public class IOSLoginPage extends BasePage implements LoginPage {

    // Locators — update to match actual app accessibility IDs
    private static final By USERNAME_FIELD  = By.xpath("//XCUIElementTypeTextField[@name='username']");
    private static final By PASSWORD_FIELD  = By.xpath("//XCUIElementTypeSecureTextField[@name='password']");
    private static final By LOGIN_BUTTON    = By.xpath("//XCUIElementTypeButton[@name='Login']");
    private static final By ERROR_MESSAGE   = By.xpath("//XCUIElementTypeStaticText[@name='error_message']");
    private static final By LOGIN_PAGE_ROOT = By.xpath("//XCUIElementTypeOther[@name='login_screen']");

    @Override
    public void enterUsername(String username) {
        sendKeys(USERNAME_FIELD, username);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void tapLogin() {
        tap(LOGIN_BUTTON);
    }

    @Override
    public boolean isLoginPageVisible() {
        return isElementVisible(LOGIN_PAGE_ROOT);
    }

    @Override
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
