package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.SignupPage;
import org.openqa.selenium.By;

public class IOSSignupPage extends BasePage implements SignupPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By EMAIL_FIELD    = By.xpath("//XCUIElementTypeTextField[@name='email']");
    private static final By PASSWORD_FIELD = By.xpath("//XCUIElementTypeSecureTextField[@name='password']");
    private static final By SIGNUP_BUTTON  = By.xpath("//XCUIElementTypeButton[@name='Signup']");

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
        tap(SIGNUP_BUTTON);
    }
}
