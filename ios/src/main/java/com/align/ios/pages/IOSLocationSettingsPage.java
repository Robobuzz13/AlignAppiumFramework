package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.LocationSettingsPage;
import org.openqa.selenium.By;

public class IOSLocationSettingsPage extends BasePage implements LocationSettingsPage {

    // TODO: inspect on real iOS device — iOS location services live in a different
    // Settings layout; placeholders keep the PageFactory contract intact.
    private static final By LOCATION_SWITCH = By.xpath("//XCUIElementTypeSwitch[@name='Location Services']");

    @Override
    public boolean isLocationSettingsDisplayed() {
        return isElementVisible(LOCATION_SWITCH);
    }

    @Override
    public void enableLocation() {
        if (!Boolean.parseBoolean(waitForVisible(LOCATION_SWITCH).getAttribute("value"))) {
            tap(LOCATION_SWITCH);
        }
    }

    @Override
    public void returnToApp() {
        // iOS Settings -> app return is app-specific; left as a placeholder.
    }
}
