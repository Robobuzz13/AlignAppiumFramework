package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.LoginPage;
import org.openqa.selenium.By;

public class AndroidLoginPage extends BasePage implements LoginPage {

    // Locators — update to match actual app resource IDs
    private static final By USERNAME_FIELD  = By.id("com.align.app:id/et_username");
    private static final By PASSWORD_FIELD  = By.id("com.align.app:id/et_password");
    private static final By LOGIN_BUTTON    = By.id("com.align.app:id/btn_login");
    private static final By ERROR_MESSAGE   = By.id("com.align.app:id/tv_error");
    private static final By LOGIN_PAGE_ROOT = By.id("com.align.app:id/login_root");

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
