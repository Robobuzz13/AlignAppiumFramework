package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.BirthDetailsPage;
import org.openqa.selenium.By;

public class IOSBirthDetailsPage extends BasePage implements BirthDetailsPage {

    // TODO: inspect on real iOS device — placeholder accessibility IDs
    private static final By BIRTH_DATE     = By.xpath("//XCUIElementTypeOther[@name='birth_date']");
    private static final By BIRTH_TIME     = By.xpath("//XCUIElementTypeOther[@name='birth_time']");
    private static final By BIRTH_LOCATION = By.xpath("//XCUIElementTypeOther[@name='birth_location']");
    private static final By NEXT_BUTTON    = By.xpath("//XCUIElementTypeButton[@name='Next']");
    private static final By PICKER_SET     = By.xpath("//XCUIElementTypeButton[@name='Set']");
    private static final By PLACES_SEARCH      = By.xpath("//XCUIElementTypeSearchField[@name='Search a place']");
    private static final By PLACES_FIRST_MATCH = By.xpath("(//XCUIElementTypeCell)[1]");

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
        waitForVisible(PLACES_FIRST_MATCH).click();
    }

    @Override
    public void tapNext() {
        tap(NEXT_BUTTON);
    }
}
