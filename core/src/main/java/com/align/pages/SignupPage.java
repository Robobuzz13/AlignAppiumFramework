package com.align.pages;

public interface SignupPage {
    boolean isSignupScreenVisible();
    void enterEmail(String email);
    void enterPassword(String password);
    void tapSignup();
}
