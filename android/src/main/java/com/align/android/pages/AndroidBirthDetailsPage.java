package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.BirthDetailsPage;
import org.openqa.selenium.By;

public class AndroidBirthDetailsPage extends BasePage implements BirthDetailsPage {

    // Locators captured live from com.dailyinsights birth-details screen.
    private static final By BIRTH_DATE     = By.id("com.dailyinsights:id/txtBirthDate");
    private static final By BIRTH_TIME     = By.id("com.dailyinsights:id/txtBirthTime");
    private static final By BIRTH_LOCATION = By.id("com.dailyinsights:id/txtBirthLocation");
    private static final By NEXT_BUTTON    = By.id("com.dailyinsights:id/imgNext");

    // Shared Set button used by both the date and time picker dialogs.
    private static final By PICKER_SET = By.id("com.dailyinsights:id/ButtonSet");

    // Google Places autocomplete (location search screen).
    private static final By PLACES_SEARCH      = By.id("com.dailyinsights:id/places_autocomplete_search_bar");
    private static final By PLACES_FIRST_MATCH = By.id("com.dailyinsights:id/places_autocomplete_prediction_primary_text");

    @Override
    public boolean isBirthDetailsScreenVisible() {
        return isElementVisible(BIRTH_DATE);
    }

    @Override
    public void setBirthDate() {
        tap(BIRTH_DATE);
        tap(PICKER_SET);
    }

    @Override
    public void setBirthTime() {
        tap(BIRTH_TIME);
        tap(PICKER_SET);
    }

    @Override
    public void setBirthLocation(String city) {
        tap(BIRTH_LOCATION);
        sendKeys(PLACES_SEARCH, city);
        // Predictions render asynchronously; wait for the first row, then select it.
        // The prediction text is not itself clickable, so click selects the row.
        waitForVisible(PLACES_FIRST_MATCH).click();
    }

    @Override
    public void tapNext() {
        tap(NEXT_BUTTON);
    }
}
