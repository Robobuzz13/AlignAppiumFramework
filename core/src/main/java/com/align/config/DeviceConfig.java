package com.align.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceConfig {
    private String platform;
    private String deviceName;
    private String udid;
    private String appPath;
    private String platformVersion;
    private String executionEnv;
    private String cloudUrl;
    private String cloudKey;
    private boolean autoStartServer;

    public static DeviceConfig fromConfig() {
        ConfigLoader cfg = ConfigLoader.getInstance();
        return DeviceConfig.builder()
                .platform(cfg.getProperty("platform", "android"))
                .deviceName(cfg.getProperty("device.name", ""))
                .udid(cfg.getProperty("device.udid", ""))
                .appPath(cfg.getProperty("app.path", ""))
                .platformVersion(cfg.getProperty("platform.version", ""))
                .executionEnv(cfg.getProperty("env", "local"))
                .cloudUrl(cfg.getProperty("cloud.url", ""))
                .cloudKey(cfg.getProperty("cloud.key", ""))
                .autoStartServer(cfg.getBooleanProperty("auto.start.server", false))
                .build();
    }

    public boolean isAndroid() {
        return "android".equalsIgnoreCase(platform);
    }

    public boolean isIOS() {
        return "ios".equalsIgnoreCase(platform);
    }

    public boolean isCloud() {
        return "cloud".equalsIgnoreCase(executionEnv);
    }
}
