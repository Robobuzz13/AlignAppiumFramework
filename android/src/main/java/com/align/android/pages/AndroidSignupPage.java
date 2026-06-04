package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.SignupPage;
import com.align.utils.KeyboardUtils;
import org.openqa.selenium.By;

public class AndroidSignupPage extends BasePage implements SignupPage {

    // Locators captured live from com.dailyinsights signup screen.
    // Note: the clickable Signup button id is misspelled "txtSingup" in the app;
    // "txtSignup" is the (non-clickable) header.
    private static final By EMAIL_FIELD    = By.id("com.dailyinsights:id/edtEmail");
    private static final By PASSWORD_FIELD = By.id("com.dailyinsights:id/edtPassword");
    private static final By SIGNUP_BUTTON  = By.id("com.dailyinsights:id/txtSingup");

    @Override
    public boolean isSignupScreenVisible() {
        return isElementVisible(EMAIL_FIELD);
    }

    @Override
    public void enterEmail(String email) {
        sendKeys(EMAIL_FIELD, email);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void tapSignup() {
        // The soft keyboard from the password field covers the Signup button; hide it first.
        KeyboardUtils.hideKeyboard(getDriver());
        tap(SIGNUP_BUTTON);
    }
}
