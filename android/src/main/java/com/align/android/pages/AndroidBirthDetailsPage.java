package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.BirthDetailsPage;
import com.align.utils.GestureUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

public class AndroidBirthDetailsPage extends BasePage implements BirthDetailsPage {

    // Locators captured live from com.dailyinsights birth-details screen.
    private static final By BIRTH_DATE     = By.id("com.dailyinsights:id/txtBirthDate");
    private static final By BIRTH_TIME     = By.id("com.dailyinsights:id/txtBirthTime");
    private static final By BIRTH_LOCATION = By.id("com.dailyinsights:id/txtBirthLocation");
    private static final By NEXT_BUTTON    = By.id("com.dailyinsights:id/imgNext");

    // Shared Set button used by both the date and time picker dialogs.
    private static final By PICKER_SET = By.id("com.dailyinsights:id/ButtonSet");

    // Date dialog: three NumberPicker spinners, order is [0]=month, [1]=day, [2]=year.
    private static final By DATE_SPINNER = By.id("android:id/numberpicker_input");

    // Time dialog: plain EditText fields (24-hour format).
    private static final By TIME_HOURS   = By.id("com.dailyinsights:id/editTextHours");
    private static final By TIME_MINUTES = By.id("com.dailyinsights:id/editTextMinutes");

    // Google Places autocomplete (location search screen).
    private static final By PLACES_SEARCH      = By.id("com.dailyinsights:id/places_autocomplete_search_bar");
    private static final By PLACES_FIRST_MATCH = By.id("com.dailyinsights:id/places_autocomplete_prediction_primary_text");

    private static final int MONTH_INDEX = 0;
    private static final int DAY_INDEX   = 1;
    private static final int YEAR_INDEX  = 2;

    private final Random random = new Random();

    @Override
    public boolean isBirthDetailsScreenVisible() {
        return isElementVisible(BIRTH_DATE);
    }

    /**
     * Opens the date picker and scrolls each spinner down a random number of steps,
     * landing on a random PAST date (the year always decreases from the current year),
     * then confirms. Exact value is intentionally not targeted — a random past date is enough.
     */
    @Override
    public void setBirthDate() {
        tap(BIRTH_DATE);
        waitForVisible(PICKER_SET);
        spinDown(YEAR_INDEX, 20 + random.nextInt(26)); // 20–45 years back
        spinDown(MONTH_INDEX, random.nextInt(12));     // 0–11 months back
        spinDown(DAY_INDEX, random.nextInt(16));       // 0–15 days back
        tap(PICKER_SET);
    }

    /**
     * Opens the time picker and types a random 24-hour time, then confirms.
     */
    @Override
    public void setBirthTime() {
        tap(BIRTH_TIME);
        waitForVisible(PICKER_SET);
        sendKeys(TIME_HOURS, String.format("%02d", random.nextInt(24)));
        sendKeys(TIME_MINUTES, String.format("%02d", random.nextInt(60)));
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

    // Scrolls a NumberPicker column down (toward smaller/earlier values) a fixed number of steps.
    // Re-finds the spinner each step because the element goes stale after a swipe.
    private void spinDown(int spinnerIndex, int steps) {
        for (int i = 0; i < steps; i++) {
            List<WebElement> spinners = getDriver().findElements(DATE_SPINNER);
            if (spinners.size() <= spinnerIndex) {
                return;
            }
            Rectangle r = spinners.get(spinnerIndex).getRect();
            int cx = r.getX() + r.getWidth() / 2;
            int cy = r.getY() + r.getHeight() / 2;
            // Span wider than the EditText so the NumberPicker actually scrolls one step.
            GestureUtils.swipe(getDriver(), cx, cy - 140, cx, cy + 140);
        }
    }
}
