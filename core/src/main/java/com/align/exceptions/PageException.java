package com.align.exceptions;

public class PageException extends RuntimeException {
    private final String pageName;
    private final String locator;

    public PageException(String pageName, String locator, String message) {
        super(String.format("[%s] Element '%s': %s", pageName, locator, message));
        this.pageName = pageName;
        this.locator = locator;
    }

    public PageException(String pageName, String locator, String message, Throwable cause) {
        super(String.format("[%s] Element '%s': %s", pageName, locator, message), cause);
        this.pageName = pageName;
        this.locator = locator;
    }

    public String getPageName() { return pageName; }
    public String getLocator() { return locator; }
}
