package com.align.factory;

import com.align.config.ConfigLoader;
import com.align.pages.LoginPage;

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

    public static LoginPage getLoginPage() {
        return create("LoginPage");
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
