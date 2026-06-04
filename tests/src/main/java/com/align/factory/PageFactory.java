package com.align.factory;

import com.align.config.ConfigLoader;
import com.align.pages.BirthChartSummaryPage;
import com.align.pages.BirthDetailsPage;
import com.align.pages.GpsDialogPage;
import com.align.pages.InsightPage;
import com.align.pages.LocationAccessPage;
import com.align.pages.LocationSettingsPage;
import com.align.pages.NamePage;
import com.align.pages.OptionListPage;
import com.align.pages.PaymentPage;
import com.align.pages.SignupPage;
import com.align.pages.SplashCarouselPage;

import java.util.Map;

public class PageFactory {
    private PageFactory() {}

    // Maps platform key → class name prefix (handles abbreviations like IOS vs Ios)
    private static final Map<String, String> PREFIX = Map.of(
            "android", "Android",
            "ios", "IOS"
    );

    private static String getPlatform() {
        return ConfigLoader.getInstance().getProperty("platform", "android").toLowerCase();
    }

    public static InsightPage getInsightPage() {
        return create("InsightPage");
    }

    public static SplashCarouselPage getSplashCarouselPage() {
        return create("SplashCarouselPage");
    }

    public static SignupPage getSignupPage() {
        return create("SignupPage");
    }

    public static NamePage getNamePage() {
        return create("NamePage");
    }

    public static BirthDetailsPage getBirthDetailsPage() {
        return create("BirthDetailsPage");
    }

    public static BirthChartSummaryPage getBirthChartSummaryPage() {
        return create("BirthChartSummaryPage");
    }

    public static LocationAccessPage getLocationAccessPage() {
        return create("LocationAccessPage");
    }

    public static GpsDialogPage getGpsDialogPage() {
        return create("GpsDialogPage");
    }

    public static LocationSettingsPage getLocationSettingsPage() {
        return create("LocationSettingsPage");
    }

    public static OptionListPage getOptionListPage() {
        return create("OptionListPage");
    }

    public static PaymentPage getPaymentPage() {
        return create("PaymentPage");
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(String pageName) {
        String platform = getPlatform();
        String prefix = PREFIX.getOrDefault(platform,
                platform.substring(0, 1).toUpperCase() + platform.substring(1));
        String className = "com.align." + platform + ".pages." + prefix + pageName;
        try {
            return (T) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page " + className
                    + " for platform: " + platform, e);
        }
    }
}
