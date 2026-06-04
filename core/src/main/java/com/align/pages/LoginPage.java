package com.align.pages;

public interface LoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void tapLogin();
    boolean isLoginPageVisible();
    String getErrorMessage();
}
