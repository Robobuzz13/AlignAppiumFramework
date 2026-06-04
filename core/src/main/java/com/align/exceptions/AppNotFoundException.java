package com.align.exceptions;

public class AppNotFoundException extends RuntimeException {
    public AppNotFoundException(String appPath) {
        super("App binary not found at path: " + appPath + ". Verify app.path config or -Dapp.path flag.");
    }

    public AppNotFoundException(String appPath, Throwable cause) {
        super("App binary not found at path: " + appPath + ". Verify app.path config or -Dapp.path flag.", cause);
    }
}
