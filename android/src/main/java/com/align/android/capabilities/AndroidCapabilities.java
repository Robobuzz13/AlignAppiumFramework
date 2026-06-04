package com.align.android.capabilities;

import com.align.config.DeviceConfig;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.util.HashMap;
import java.util.Map;

public class AndroidCapabilities {
    private AndroidCapabilities() {}

    public static UiAutomator2Options build(DeviceConfig config) {
        UiAutomator2Options options = new UiAutomator2Options();

        if (!config.getDeviceName().isEmpty()) {
            options.setDeviceName(config.getDeviceName());
        }
        if (!config.getUdid().isEmpty()) {
            options.setUdid(config.getUdid());
        }
        if (!config.getAppPath().isEmpty()) {
            options.setApp(config.getAppPath());
        }
        // For an already-installed app, launch by package/activity instead of an apk path.
        if (!config.getAppPackage().isEmpty()) {
            options.setAppPackage(config.getAppPackage());
        }
        if (!config.getAppActivity().isEmpty()) {
            options.setAppActivity(config.getAppActivity());
        }
        if (!config.getPlatformVersion().isEmpty()) {
            options.setPlatformVersion(config.getPlatformVersion());
        }

        options.setNoReset(false);
        options.setAutoGrantPermissions(true);
        options.setNewCommandTimeout(java.time.Duration.ofSeconds(300));

        if (config.isCloud()) {
            applyCloudCapabilities(options, config);
        }

        return options;
    }

    private static void applyCloudCapabilities(UiAutomator2Options options, DeviceConfig config) {
        if (!config.getCloudKey().isEmpty() && config.getCloudKey().contains(":")) {
            String[] parts = config.getCloudKey().split(":", 2);
            Map<String, Object> bsOptions = new HashMap<>();
            bsOptions.put("userName", parts[0]);
            bsOptions.put("accessKey", parts[1]);
            bsOptions.put("projectName", "AlignApp");
            bsOptions.put("buildName", System.getenv().getOrDefault("BUILD_NAME", "local-build"));
            options.setCapability("bstack:options", bsOptions);
        }
    }
}
