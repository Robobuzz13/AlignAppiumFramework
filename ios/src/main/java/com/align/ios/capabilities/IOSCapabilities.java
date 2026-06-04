package com.align.ios.capabilities;

import com.align.config.DeviceConfig;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.util.HashMap;
import java.util.Map;

public class IOSCapabilities {
    private IOSCapabilities() {}

    public static XCUITestOptions build(DeviceConfig config) {
        XCUITestOptions options = new XCUITestOptions();

        if (!config.getDeviceName().isEmpty()) {
            options.setDeviceName(config.getDeviceName());
        }
        if (!config.getUdid().isEmpty()) {
            options.setUdid(config.getUdid());
        }
        if (!config.getAppPath().isEmpty()) {
            options.setApp(config.getAppPath());
        }
        if (!config.getPlatformVersion().isEmpty()) {
            options.setPlatformVersion(config.getPlatformVersion());
        }

        options.setNoReset(false);
        options.setAutoAcceptAlerts(false);
        options.setNewCommandTimeout(java.time.Duration.ofSeconds(300));
        options.setWdaLaunchTimeout(java.time.Duration.ofMillis(60000));

        if (config.isCloud()) {
            applyCloudCapabilities(options, config);
        }

        return options;
    }

    private static void applyCloudCapabilities(XCUITestOptions options, DeviceConfig config) {
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
