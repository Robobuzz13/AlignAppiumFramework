package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.PaymentPage;
import org.openqa.selenium.By;

public class AndroidPaymentPage extends BasePage implements PaymentPage {

    // RevenueCat paywall (PaywallActivity) has no resource-ids; match stable on-screen text.
    private static final By PAYWALL_MARKER = By.xpath(
            "//android.widget.TextView[contains(@text,'Astrologer') or @text='Restore' "
                    + "or @text='Yearly' or @text='Monthly']");

    private static final int PAYWALL_TIMEOUT = 20;

    @Override
    public boolean isPaymentPageDisplayed() {
        // RevenueCat fetches offerings, so the paywall can take a few seconds to render.
        try {
            waitForVisible(PAYWALL_MARKER, PAYWALL_TIMEOUT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
