package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextUtils {
    private static final Logger log = LoggerFactory.getLogger(ContextUtils.class);
    private static final String NATIVE_APP = "NATIVE_APP";

    private ContextUtils() {}

    @SuppressWarnings("unchecked")
    private static Set<String> getContextHandles(AppiumDriver driver) {
        Object response = driver.execute(MobileCommand.GET_CONTEXT_HANDLES).getValue();
        if (response instanceof Collection) {
            return Set.copyOf((Collection<String>) response);
        }
        return Collections.emptySet();
    }

    private static void switchContext(AppiumDriver driver, String contextName) {
        driver.execute(MobileCommand.SWITCH_TO_CONTEXT, Map.of("name", contextName));
    }

    private static String currentContext(AppiumDriver driver) {
        Object response = driver.execute(MobileCommand.GET_CURRENT_CONTEXT_HANDLE).getValue();
        return response != null ? response.toString() : NATIVE_APP;
    }

    public static void switchToWebView(AppiumDriver driver) {
        waitForWebViewContext(driver, Duration.ofSeconds(10));
        Set<String> contexts = getContextHandles(driver);
        String webViewContext = contexts.stream()
                .filter(c -> c.startsWith("WEBVIEW"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WebView context found. Available: " + contexts));
        switchContext(driver, webViewContext);
        log.info("Switched to context: {}", webViewContext);
    }

    public static void switchToNative(AppiumDriver driver) {
        switchContext(driver, NATIVE_APP);
        log.info("Switched to NATIVE_APP context");
    }

    public static List<String> getAvailableContexts(AppiumDriver driver) {
        return List.copyOf(getContextHandles(driver));
    }

    public static String getCurrentContext(AppiumDriver driver) {
        return currentContext(driver);
    }

    public static void waitForWebViewContext(AppiumDriver driver, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            boolean found = getContextHandles(driver).stream()
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
