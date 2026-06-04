package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.LocationSettingsPage;
import com.align.utils.KeyboardUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AndroidLocationSettingsPage extends BasePage implements LocationSettingsPage {

    // Android system Settings -> Location screen (com.android.settings).
    private static final By LOCATION_SWITCH = By.id("com.android.settings:id/switch_widget");

    @Override
    public boolean isLocationSettingsDisplayed() {
        return isElementVisible(LOCATION_SWITCH);
    }

    @Override
    public void enableLocation() {
        WebElement toggle = waitForVisible(LOCATION_SWITCH);
        if (!Boolean.parseBoolean(toggle.getAttribute("checked"))) {
            tap(LOCATION_SWITCH);
        }
    }

    @Override
    public void returnToApp() {
        // Settings was launched from the app, so a single Back returns to it.
        KeyboardUtils.pressBack(getDriver());
    }
}
