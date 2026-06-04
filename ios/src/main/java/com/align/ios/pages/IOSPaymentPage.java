package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.PaymentPage;
import org.openqa.selenium.By;

public class IOSPaymentPage extends BasePage implements PaymentPage {

    // TODO: inspect on real iOS device — placeholder paywall text markers
    private static final By PAYWALL_MARKER = By.xpath(
            "//XCUIElementTypeStaticText[contains(@name,'Astrologer') or @name='Restore' "
                    + "or @name='Yearly' or @name='Monthly']");

    @Override
    public boolean isPaymentPageDisplayed() {
        try {
            waitForVisible(PAYWALL_MARKER, 20);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
