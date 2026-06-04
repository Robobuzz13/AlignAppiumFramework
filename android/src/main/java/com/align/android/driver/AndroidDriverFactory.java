package com.align.android.driver;

import com.align.config.DeviceConfig;
import com.align.android.capabilities.AndroidCapabilities;
import com.align.exceptions.DriverInitException;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(AndroidDriverFactory.class);

    public AndroidDriver createDriver(DeviceConfig config) {
        try {
            URL serverUrl = resolveServerUrl(config);
            log.info("Creating AndroidDriver — server: {}, device: {}, env: {}",
                    serverUrl, config.getDeviceName(), config.getExecutionEnv());
            return new AndroidDriver(serverUrl, AndroidCapabilities.build(config));
        } catch (MalformedURLException e) {
            throw new DriverInitException("Invalid Appium server URL: " + e.getMessage(), e);
        }
    }

    private URL resolveServerUrl(DeviceConfig config) throws MalformedURLException {
        if (config.isCloud()) {
            if (config.getCloudUrl().isEmpty()) {
                throw new DriverInitException("cloud.url is not configured for cloud execution");
            }
            return new URL(config.getCloudUrl());
        }
        return new URL("http://127.0.0.1:4723");
    }
}
