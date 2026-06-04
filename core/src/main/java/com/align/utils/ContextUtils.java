package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ContextUtils {
    private static final Logger log = LoggerFactory.getLogger(ContextUtils.class);
    private static final String NATIVE_APP = "NATIVE_APP";

    private ContextUtils() {}

    public static void switchToWebView(AppiumDriver driver) {
        waitForWebViewContext(driver, Duration.ofSeconds(10));
        Set<String> contexts = driver.getContextHandles();
        String webViewContext = contexts.stream()
                .filter(c -> c.startsWith("WEBVIEW"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WebView context found. Available: " + contexts));
        driver.context(webViewContext);
        log.info("Switched to context: {}", webViewContext);
    }

    public static void switchToNative(AppiumDriver driver) {
        driver.context(NATIVE_APP);
        log.info("Switched to NATIVE_APP context");
    }

    public static List<String> getAvailableContexts(AppiumDriver driver) {
        return List.copyOf(driver.getContextHandles());
    }

    public static String getCurrentContext(AppiumDriver driver) {
        return driver.getContext();
    }

    public static void waitForWebViewContext(AppiumDriver driver, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            boolean found = driver.getContextHandles().stream()
                    .anyMatch(c -> c.startsWith("WEBVIEW"));
            if (found) return;
            try { Thread.sleep(500); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        throw new RuntimeException("WebView context not available after " + timeout.getSeconds() + "s");
    }
}
