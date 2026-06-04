package com.align.config;

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

    private DeviceConfig() {}

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

    public boolean isAndroid() { return "android".equalsIgnoreCase(platform); }
    public boolean isIOS()     { return "ios".equalsIgnoreCase(platform); }
    public boolean isCloud()   { return "cloud".equalsIgnoreCase(executionEnv); }

    public String getPlatform()        { return platform; }
    public String getDeviceName()      { return deviceName; }
    public String getUdid()            { return udid; }
    public String getAppPath()         { return appPath; }
    public String getPlatformVersion() { return platformVersion; }
    public String getExecutionEnv()    { return executionEnv; }
    public String getCloudUrl()        { return cloudUrl; }
    public String getCloudKey()        { return cloudKey; }
    public boolean isAutoStartServer() { return autoStartServer; }

    public void setAppPath(String v) { this.appPath = v; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DeviceConfig c = new DeviceConfig();

        public Builder platform(String v)        { c.platform = v;        return this; }
        public Builder deviceName(String v)      { c.deviceName = v;      return this; }
        public Builder udid(String v)            { c.udid = v;            return this; }
        public Builder appPath(String v)         { c.appPath = v;         return this; }
        public Builder platformVersion(String v) { c.platformVersion = v; return this; }
        public Builder executionEnv(String v)    { c.executionEnv = v;    return this; }
        public Builder cloudUrl(String v)        { c.cloudUrl = v;        return this; }
        public Builder cloudKey(String v)        { c.cloudKey = v;        return this; }
        public Builder autoStartServer(boolean v){ c.autoStartServer = v; return this; }

        public DeviceConfig build() { return c; }
    }
}
