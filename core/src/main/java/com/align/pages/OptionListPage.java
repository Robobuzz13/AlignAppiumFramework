package com.align.pages;

/**
 * Generic single-select list screen used repeatedly in onboarding (interests,
 * "how did you hear about us", etc.): a header title, a list of tappable options,
 * and a Continue button enabled once an option is selected.
 */
public interface OptionListPage {
    boolean isDisplayed();
    String getTitle();
    void selectOption(String name);
    void tapContinue();
}
