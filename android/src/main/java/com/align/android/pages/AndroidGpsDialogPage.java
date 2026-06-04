package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.GpsDialogPage;
import org.openqa.selenium.By;

public class AndroidGpsDialogPage extends BasePage implements GpsDialogPage {

    // Standard Android AlertDialog ids; this app uses it for the "GPS disabled" prompt.
    private static final By MESSAGE = By.id("android:id/message");
    private static final By YES     = By.id("android:id/button1");
    private static final By NO      = By.id("android:id/button2");

    @Override
    public boolean isGpsDialogDisplayed() {
        return isElementVisible(MESSAGE) && getText(MESSAGE).toLowerCase().contains("gps");
    }

    @Override
    public void tapYes() {
        tap(YES);
    }

    @Override
    public void tapNo() {
        tap(NO);
    }
}
