package com.align.tests;

import com.align.config.ConfigLoader;
import com.align.config.DeviceConfig;
import com.align.driver.DriverFactory;
import com.align.driver.DriverManager;
import com.align.driver.ServerManager;
import com.align.utils.AppResolver;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        DeviceConfig config = DeviceConfig.fromConfig();
        if (config.isAutoStartServer() && !config.isCloud()) {
            ServerManager.startServer();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("Setting up test: {}.{}", getClass().getSimpleName(), method.getName());
        DeviceConfig config = buildDeviceConfig();
        AppiumDriver driver = DriverFactory.createDriver(config);
        DriverManager.setDriver(driver);
        log.info("Driver ready — platform: {}, device: {}", config.getPlatform(), config.getDeviceName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        log.info("Tearing down test: {}", method.getName());
        if (DriverManager.hasDriver()) {
            try {
                DriverManager.getDriver().quit();
            } catch (Exception e) {
                log.warn("Driver quit failed: {}", e.getMessage());
            } finally {
                DriverManager.removeDriver();
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (ServerManager.isRunning()) {
            ServerManager.stopServer();
        }
    }

    private DeviceConfig buildDeviceConfig() {
        ConfigLoader cfg = ConfigLoader.getInstance();
        String appPath = cfg.getProperty("app.path", "");
        if (!appPath.isEmpty()) {
            appPath = AppResolver.resolve(appPath);
        }
        return DeviceConfig.builder()
                .platform(cfg.getProperty("platform", "android"))
                .deviceName(cfg.getProperty("device.name", ""))
                .udid(cfg.getProperty("device.udid", ""))
                .appPath(appPath)
                .platformVersion(cfg.getProperty("platform.version", ""))
                .executionEnv(cfg.getProperty("env", "local"))
                .cloudUrl(cfg.getProperty("cloud.url", ""))
                .cloudKey(cfg.getProperty("cloud.key", ""))
                .autoStartServer(cfg.getBooleanProperty("auto.start.server", false))
                .build();
    }
}
