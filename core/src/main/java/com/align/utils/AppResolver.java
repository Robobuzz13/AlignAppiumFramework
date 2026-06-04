package com.align.utils;

import com.align.exceptions.AppNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppResolver {
    private static final Logger log = LoggerFactory.getLogger(AppResolver.class);

    private AppResolver() {}

    public static String resolve(String appPath) {
        if (appPath == null || appPath.isBlank()) return "";

        if (isCloudAppId(appPath)) {
            log.debug("Cloud app ID detected, passing through: {}", appPath);
            return appPath;
        }

        String downloadUrl = System.getProperty("app.url", System.getenv("APP_URL"));
        if (downloadUrl != null && !downloadUrl.isBlank()) {
            return downloadFromUrl(downloadUrl, appPath);
        }

        File appFile = new File(appPath);
        if (!appFile.exists()) {
            throw new AppNotFoundException(appPath);
        }
        log.info("App resolved: {}", appFile.getAbsolutePath());
        return appFile.getAbsolutePath();
    }

    private static boolean isCloudAppId(String path) {
        return path.startsWith("bs://") || path.startsWith("sauce-storage:");
    }

    private static String downloadFromUrl(String url, String targetFileName) {
        try {
            Path targetDir = Paths.get("target", "apps");
            Files.createDirectories(targetDir);
            String fileName = targetFileName.isBlank()
                    ? url.substring(url.lastIndexOf('/') + 1)
                    : new File(targetFileName).getName();
            Path targetPath = targetDir.resolve(fileName);

            log.info("Downloading app from {} to {}", url, targetPath);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofFile(targetPath));
            log.info("App downloaded: {}", targetPath);
            return targetPath.toAbsolutePath().toString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppNotFoundException(url + " (download failed: interrupted)");
        } catch (IOException e) {
            throw new AppNotFoundException(url + " (download failed: " + e.getMessage() + ")");
        }
    }
}
