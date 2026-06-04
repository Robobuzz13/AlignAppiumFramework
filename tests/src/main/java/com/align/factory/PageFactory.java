package com.align.factory;

import com.align.config.ConfigLoader;
import com.align.pages.LoginPage;

public class PageFactory {
    private PageFactory() {}

    private static String getPlatform() {
        return ConfigLoader.getInstance().getProperty("platform", "android");
    }

    public static LoginPage getLoginPage() {
        return create("LoginPage");
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(String pageName) {
        String platform = getPlatform();
        String className = "com.align." + platform.toLowerCase() + ".pages."
                + platform.substring(0, 1).toUpperCase() + platform.substring(1).toLowerCase()
                + pageName;
        try {
            return (T) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page " + className
                    + " for platform: " + platform, e);
        }
    }
}
