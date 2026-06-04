# AlignAppiumFramework — Mobile Automation Framework Design

**Date:** 2026-06-04
**Branch:** feature/mobile-framework
**Stack:** Java + TestNG + Appium

---

## 1. Overview

Dual-platform (Android + iOS) native app automation framework using Appium. Single Maven multi-module repo. Platform-agnostic test layer — zero platform conditionals in test code. Full parallel execution at any granularity. Dual reporting via Allure + ExtentReports with optional video recording.

---

## 2. Project Structure

```
AlignAppiumFramework/
├── pom.xml                          # parent POM, dependency management
├── core/
│   ├── pom.xml
│   └── src/main/java/com/align/
│       ├── driver/
│       │   ├── DriverManager.java   # ThreadLocal<AppiumDriver>
│       │   ├── DriverFactory.java   # creates Android/iOS driver
│       │   └── ServerManager.java   # optional local Appium server lifecycle
│       ├── config/
│       │   ├── ConfigLoader.java    # reads properties + env vars + CLI flags
│       │   └── DeviceConfig.java    # POJO: platform, deviceName, udid, appPath
│       ├── pages/
│       │   └── BasePage.java        # shared wait, tap, scroll, gesture methods
│       ├── listeners/
│       │   ├── TestListener.java    # ITestListener → Allure + Extent + video
│       │   └── DriverListener.java  # screenshot on failure
│       └── utils/
│           ├── WaitUtils.java
│           ├── ScreenshotUtils.java
│           ├── VideoUtils.java
│           ├── GestureUtils.java
│           ├── KeyboardUtils.java
│           ├── AlertUtils.java
│           ├── PermissionUtils.java
│           ├── AppUtils.java
│           ├── DeviceUtils.java
│           ├── NetworkUtils.java
│           ├── ClipboardUtils.java
│           ├── FileUtils.java
│           ├── BiometricUtils.java
│           ├── DeepLinkUtils.java
│           ├── ContextUtils.java
│           ├── ElementUtils.java
│           └── NotificationUtils.java
├── android/
│   ├── pom.xml                      # depends on core
│   └── src/main/java/com/align/android/
│       ├── driver/
│       │   └── AndroidDriverFactory.java
│       ├── capabilities/
│       │   └── AndroidCapabilities.java
│       └── pages/                   # Android-specific page object implementations
├── ios/
│   ├── pom.xml                      # depends on core
│   └── src/main/java/com/align/ios/
│       ├── driver/
│       │   └── IOSDriverFactory.java
│       ├── capabilities/
│       │   └── IOSCapabilities.java
│       └── pages/                   # iOS-specific page object implementations
└── tests/
    ├── pom.xml                      # depends on android + ios
    └── src/test/
        ├── java/com/align/tests/    # test classes — platform-agnostic
        └── resources/
            ├── config/
            │   ├── config.properties
            │   ├── android-local.properties
            │   ├── ios-local.properties
            │   └── cloud.properties
            └── suites/
                ├── android-suite.xml
                ├── ios-suite.xml
                └── parallel-suite.xml
```

---

## 3. Driver Management & Parallel Execution

### ThreadLocal Driver

`DriverManager` stores one `AppiumDriver` per thread via `ThreadLocal<AppiumDriver>`. No shared state between threads.

**Lifecycle:**
1. TestNG `@BeforeMethod` → `DriverFactory.createDriver(DeviceConfig)` → `AppiumDriver`
2. `DriverManager.setDriver(driver)` — stored in ThreadLocal
3. Test runs — page objects call `DriverManager.getDriver()`
4. TestNG `@AfterMethod` → `driver.quit()` → `DriverManager.removeDriver()`

### Parallel Modes (TestNG XML config, no code change)

| Mode | Config |
|------|--------|
| Multiple tests on same device pool | `parallel="methods"` |
| Android + iOS simultaneously | `parallel="tests"` |
| Same test on multiple devices | `DataProvider` with device list |

### Cloud vs Local

`ConfigLoader` reads `EXECUTION_ENV` env var:
- `local` → connects to `http://127.0.0.1:4723`
- `cloud` → reads `CLOUD_URL`, `CLOUD_KEY` from env vars

`ServerManager` auto-starts local Appium server via `AppiumServiceBuilder` when `AUTO_START_SERVER=true`.

### Config Resolution Order (highest wins)

1. Maven CLI `-D` flags
2. Environment variables
3. `config.properties` file
4. Hardcoded defaults

---

## 4. App Binary Configuration

`DeviceConfig` holds `appPath`, resolved at runtime by `AppResolver`.

**Config examples:**
```properties
# android-local.properties
app.path=/local/builds/align.apk

# ios-local.properties
app.path=/local/builds/align.app

# cloud.properties
app.path=bs://abc123def456        # BrowserStack app ID
# app.path=sauce-storage:align.ipa  # Sauce Labs
```

**`AppResolver` logic:**
- `APP_URL` set → download binary to `target/apps/` → use as `app.path`
- `app.path` starts with `bs://` or `sauce-storage:` → pass to capabilities (cloud installs)
- Local path → verify file exists → pass to `appPath` capability
- Missing file → throw `AppNotFoundException`, abort suite immediately

**Maven override:**
```bash
-Dapp.path=/builds/nightly/align-v2.1.apk
```

---

## 5. Page Object Model

### Interface-Driven Design

Each screen has:
- Interface in `core` defining the contract
- `AndroidXxxPage` implementation with Android locators
- `IOSXxxPage` implementation with iOS locators

```java
// core — contract
public interface LoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    HomePage tapLogin();
}

// android implementation
public class AndroidLoginPage extends BasePage implements LoginPage { ... }

// ios implementation
public class IOSLoginPage extends BasePage implements LoginPage { ... }
```

### PageFactory

Single entry point for tests. Reads platform from `DriverManager` context:
```java
LoginPage login = PageFactory.getLoginPage();
```

No `if(android)` / `if(ios)` in any test class.

### BasePage

Shared interactions all page objects inherit:
- `waitForElement(By)`, `waitForClickable(By)`, `waitForInvisible(By)`
- `tap(By)`, `sendKeys(By, String)`, `getText(By)`
- `swipeUp()`, `swipeDown()`, `scrollToElement(By)`
- `isElementVisible(By)` → boolean
- Page objects call `BasePage` methods only — never `driver` directly

### Locator Strategy

- Android: `By.id`, `By.xpath`, `UiAutomator2` strategies
- iOS: `By.iOSNsPredicateString`, `By.iOSClassChain`, XCUITest strategies
- Locators defined as constants at top of each page class
- No `PageFactory.initElements()` — causes stale element issues on dynamic mobile UIs; explicit `driver.findElement()` wrapped in wait methods instead

---

## 6. Reporting

### Dual Reporter Architecture

Two TestNG listeners registered in `testng.xml`. Fire on same events, independently.

**`TestListener`** implements `ITestListener` + `ISuiteListener`:

| Event | Action |
|-------|--------|
| `onTestStart` | Log device info, platform, test name |
| `onTestFailure` | Screenshot → attach Allure + Extent; stop+save video if enabled |
| `onTestSuccess` | Update Extent node; stop+discard video (unless `video.save.on.pass=true`) |
| `onTestSkipped` | Log skip reason |
| `onFinish` | Flush `ExtentReports` |

### Allure

- `allure-testng` dependency
- `@Step`, `@Attachment`, `@Description` annotations in page objects + tests
- Screenshots attached as `@Attachment(type="image/png")`
- `allure-results/` generated per run
- Jenkins/GH Actions runs `allure generate` post-test
- History trend preserved by archiving `allure-results/` between CI runs

### ExtentReports

- `ExtentReports` singleton in `ExtentManager`
- `ThreadLocal<ExtentTest>` for thread-safe test nodes
- Report: `target/extent-reports/report.html`
- Each node logs: platform, device, OS version, app version, steps, screenshot on fail

### Screenshot on Failure

Single capture, shared to both reporters:
```
onTestFailure
  → ScreenshotUtils.capture(DriverManager.getDriver())
  → byte[] → Allure.addAttachment()
  → ExtentTest.addScreenCaptureFromBase64String()
```

### Video Recording

**Config:**
```properties
video.recording.enabled=true
video.save.on.pass=false     # save only on failure by default
```

**Flow:**
```
onTestStart  → if enabled → VideoUtils.startRecording(driver)
onTestFinish → VideoUtils.stopAndSave/Discard(driver, testName)
             → if (failed OR video.save.on.pass=true)
                 → save to target/videos/<testName>-<timestamp>.mp4
                 → Allure.addAttachment("video", "video/mp4", bytes)
                 → ExtentTest link to video file
             → else → discard
```

**Platform options:**
- Android: `AndroidStartScreenRecordingOptions` — configurable bitrate, time limit (max 30 min)
- iOS: `IOSStartScreenRecordingOptions` — configurable video quality, FPS

**`VideoUtils` API:**
- `startRecording(AppiumDriver)` — platform-detected
- `stopAndSave(AppiumDriver, String testName)` → `Path`
- `stopAndDiscard(AppiumDriver)`

**CI behavior:** Auto-disabled when `EXECUTION_ENV=cloud` — cloud farms provide own video recordings.

---

## 7. Utilities

All utils: static methods, take `AppiumDriver` as param or pull from `DriverManager`. No inheritance. Independently testable.

### WaitUtils
- `waitForVisible(By, int seconds)`
- `waitForClickable(By, int seconds)`
- `waitForInvisible(By, int seconds)`
- `fluentWait(By, Duration timeout, Duration pollingInterval)`
- No `Thread.sleep()` anywhere in framework

### GestureUtils
- `swipeUp()`, `swipeDown()`, `swipeLeft()`, `swipeRight()` — percentage-based, screen-size-aware
- `scrollToElement(By)`, `scrollToText(String)` — max 5 swipes before fail
- `longPress(By)`, `doubleTap(By)`, `tap(int x, int y)`
- `pinchZoomIn(By)`, `pinchZoomOut(By)`
- `dragAndDrop(By source, By target)`
- `multiTouchAction(List<TouchPoint>)` — W3C Actions API

### KeyboardUtils
- `hideKeyboard()`, `isKeyboardShown()` → boolean
- `pressKey(AndroidKey)` / `pressKey(IOSKey)`
- `pressEnter()`, `pressBack()` (Android), `pressHome()`
- `typeWithKeyboard(By, String)` — tap field + type + hide keyboard

### AlertUtils
- `acceptAlert()`, `dismissAlert()`, `getAlertText()` → String
- `isAlertPresent()` → boolean (no exception on absent)
- `handlePermissionAlert(PermissionAction)` — iOS/Android system permission dialogs

### PermissionUtils
- `allowPermission()`, `denyPermission()`
- `grantPermission(String packageName, String permission)` — Android ADB-level
- `handleIOSPermission(PermissionAction)`
- `resetPermissions()` — Android only

### AppUtils
- `installApp(String appPath)`
- `removeApp(String bundleIdOrPackageName)`
- `isAppInstalled(String id)` → boolean
- `launchApp()`, `closeApp()`, `resetApp()`
- `runAppInBackground(Duration)`
- `activateApp(String id)`, `terminateApp(String id)`
- `getAppState(String id)` → `ApplicationState`

### DeviceUtils
- `lockDevice()`, `unlockDevice()`, `isDeviceLocked()` → boolean
- `rotatePortrait()`, `rotateLandscape()`, `getDeviceOrientation()`
- `shake()` — iOS only
- `getDeviceTime()`, `getDeviceInfo()` → Map
- `getBatteryInfo()` → level + state

### NetworkUtils
- `toggleWifi(boolean)`, `toggleMobileData(boolean)` — Android only
- `toggleAirplaneMode(boolean)` — Android only
- `setNetworkSpeed(NetworkSpeed)` — emulator only
- `getNetworkConnection()` → `ConnectionType`

### ClipboardUtils
- `setClipboard(String text)`, `getClipboard()` → String, `clearClipboard()`

### FileUtils
- `pushFileToDevice(String localPath, String devicePath)`
- `pullFileFromDevice(String devicePath)` → byte[]
- `pushMediaFile(String localPath)` — adds to device media library

### BiometricUtils
- `simulateFingerprintSuccess()`, `simulateFingerprintFailure()` — Android emulator
- `enrollBiometric()` — emulator setup
- `simulateFaceIdSuccess()`, `simulateFaceIdFailure()` — iOS simulator

### DeepLinkUtils
- `openDeepLink(String url)` — platform-aware (`mobile: openUrl` on iOS, intent on Android)
- `openUniversalLink(String url)`

### ContextUtils (WebView support)
- `switchToWebView()`, `switchToNative()`
- `getAvailableContexts()` → List\<String\>
- `getCurrentContext()` → String
- `waitForWebViewContext(Duration)`

### ElementUtils
- `getAttribute(By, String attr)` → String
- `getText(By)`, `isEnabled(By)` → boolean, `isSelected(By)` → boolean
- `highlightElement(By)` — debug visual aid
- `getElementLocation(By)` → Point
- `getElementSize(By)` → Dimension
- `getElementCount(By)` → int

### NotificationUtils
- `openNotificationCenter()` — Android only
- `clearNotifications()` — Android only
- `getNotificationText()` → List\<String\>

---

## 8. Error Handling

### Retry Mechanism
```properties
retry.count=1   # configurable, default 1
```
TestNG `IRetryAnalyzer` — retries only on `WebDriverException` (network blip, Appium timeout). Never retries on assertion failures. Retry count logged in report.

### Custom Exceptions

| Exception | Thrown by | Behavior |
|-----------|-----------|----------|
| `PageException` | Page objects | Context: element locator + page name + screenshot path |
| `DriverInitException` | `DriverManager` | Fail test immediately, clear message, not NPE |
| `AppNotFoundException` | `AppResolver` | Abort entire suite, not test-by-test |

### Logging
- SLF4J + Log4j2
- Output: `target/logs/<platform>-<timestamp>.log`
- DEBUG level locally, INFO in CI
- Config: `log4j2.xml` in `core/resources/`

---

## 9. CI/CD Integration

### GitHub Actions

Files: `.github/workflows/android-tests.yml` + `ios-tests.yml`

**Triggers:** push to main, PR, `workflow_dispatch`

**`workflow_dispatch` inputs:**
- `platform`: `[android, ios, both]`
- `execution_env`: `[local, cloud]`
- `parallel`: `[true, false]`
- `video_recording`: `[true, false]`
- `app_path`: string — path, URL, or cloud app ID
- `app_url`: string — download URL for binary (optional)

Matrix strategy runs Android + iOS in parallel when `both` selected. Secrets hold `CLOUD_URL`, `CLOUD_KEY`. Allure results published to GitHub Pages.

### Jenkins

**`Jenkinsfile`** (declarative pipeline):

**Parameters:**
- `PLATFORM` — choice: android / ios / both
- `ENV` — choice: local / cloud
- `PARALLEL` — boolean
- `VIDEO_RECORDING` — boolean
- `APP_PATH` — string
- `APP_URL` — string

**Stages:** Checkout → Build → Test → Allure Report → Extent Report (HTML plugin) → Archive artifacts

### Maven Command Pattern

```bash
mvn test -pl tests \
  -Dplatform=android \
  -Denv=cloud \
  -Dsuite=parallel-suite.xml \
  -Dvideo.recording.enabled=true \
  -Dapp.path=bs://abc123def456
```

---

## 10. Key Dependencies (Parent POM)

| Dependency | Version | Purpose |
|------------|---------|---------|
| `io.appium:java-client` | 9.x | Appium driver |
| `org.testng:testng` | 7.x | Test runner |
| `io.qameta.allure:allure-testng` | 2.x | Allure integration |
| `com.aventstack:extentreports` | 5.x | Extent HTML reports |
| `org.slf4j:slf4j-api` | 2.x | Logging facade |
| `org.apache.logging.log4j:log4j-slf4j2-impl` | 2.x | Log implementation |
| `org.projectlombok:lombok` | latest | Reduce boilerplate |

---

## 11. Success Criteria

- Tests run on Android and iOS from single `mvn test` command with platform flag
- Full parallel: same test on N devices simultaneously, no flakiness from shared state
- Zero platform conditionals in test layer
- On failure: screenshot + video (if enabled) attached to both Allure and Extent reports
- CI: GitHub Actions + Jenkins pipelines accept app binary path per run
- Local Appium server auto-starts if configured; cloud env switches automatically
