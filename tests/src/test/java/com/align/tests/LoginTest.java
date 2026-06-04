package com.align.tests;

import com.align.factory.PageFactory;
import com.align.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials navigate to home screen")
    @Description("Logs in with valid credentials and verifies navigation to home screen")
    public void validLoginNavigatesToHome() {
        LoginPage loginPage = PageFactory.getLoginPage();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page should be visible");
        performLogin(loginPage, "testuser@align.com", "Password123!");
        // Add assertion for home screen visibility once HomePage interface is created
        // Example: Assert.assertTrue(PageFactory.getHomePage().isHomePageVisible());
    }

    @Test(description = "Invalid credentials show error message")
    @Description("Verifies error message appears for invalid credentials")
    public void invalidCredentialsShowError() {
        LoginPage loginPage = PageFactory.getLoginPage();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page should be visible");
        performLogin(loginPage, "wrong@align.com", "wrongpass");
        String error = loginPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(), "Error message should be visible");
    }

    @Step("Enter username: {username}, tap login")
    private void performLogin(LoginPage loginPage, String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.tapLogin();
    }
}
