# AlignAppiumFramework

Dual-platform (Android + iOS) native mobile test automation framework built on **Appium + Java 17 + TestNG**. One test layer drives both platforms — no platform conditionals in test code.

## Features

- **Cross-platform, single test layer** — interface-driven page objects; the same test runs on Android and iOS.
- **Full parallel execution** — ThreadLocal driver management. Run the same test on N devices, or Android + iOS simultaneously, configured purely in TestNG XML.
- **Local + cloud** — local emulators/simulators for dev, cloud farm (BrowserStack/Sauce) for CI; switched by one flag.
- **Dual reporting** — Allure + ExtentReports, single screenshot on failure shared to both.
- **Video recording** — optional, config-gated, auto-disabled on cloud (farms record themselves).
- **App binary flexibility** — local path, download URL, or cloud app ID per run.
- **16 utility classes** — gestures, keyboard, alerts, permissions, app/device/network control, clipboard, files, biometrics, deep links, WebView context, elements, notifications, screenshots, video.
- **CI/CD ready** — GitHub Actions (Android + iOS) and Jenkins pipelines included.

## Requirements

| Tool | Version |
|------|---------|
| JDK | 17+ (tested on 26) |
| Maven | 3.9+ |
| Appium | 2.x server + UiAutomator2 (Android) / XCUITest (iOS) drivers |
| Android | SDK + emulator/device |
| iOS | Xcode + simulator/device (macOS only) |

## Project Structure

```
AlignAppiumFramework/
├── core/      driver, config, BasePage, 16 utils, listeners, reporting
├── android/   AndroidCapabilities, AndroidDriverFactory, Android page objects
├── ios/       IOSCapabilities, IOSDriverFactory, iOS page objects
├── tests/     PageFactory, BaseTest, test classes, TestNG suites, config
├── .github/workflows/   android-tests.yml, ios-tests.yml
├── Jenkinsfile
└── docs/superpowers/    specs + implementation plan
```

Dependency direction: `android`/`ios` → `core`; `tests` → `android`+`ios`. `core` never depends on platform modules (uses reflection to route).

## Quick Start

```bash
# 1. Build
mvn install -DskipTests -pl core,android,ios,tests

# 2. Start Appium server (or set auto.start.server=true)
appium

# 3. Run Android tests locally
mvn test -pl tests \
  -Dplatform=android \
  -Denv=local \
  -Ddevice.name="Pixel_7_API_33" \
  -Dapp.path=/path/to/align.apk \
  -Dsuite=src/test/resources/suites/android-suite.xml
```

> Windows: Maven here lives at `D:\TestingTools\apache-maven-3.9.16\bin`. Add to PATH or prefix per session: `$env:PATH += ";D:\TestingTools\apache-maven-3.9.16\bin"`.

## Configuration

Resolution order (highest wins): **CLI `-D` flag → environment variable → properties file → default.**

Properties live in `tests/src/test/resources/config/`:

| File | Purpose |
|------|---------|
| `config.properties` | Shared defaults (platform, env, retry, video, timeouts) |
| `android-local.properties` | Android device + app |
| `ios-local.properties` | iOS device + app |
| `cloud.properties` | Cloud URL/key + app ID |

Common flags:

| Flag | Values | Meaning |
|------|--------|---------|
| `-Dplatform` | `android` / `ios` / `both` | Target platform |
| `-Denv` | `local` / `cloud` | Execution environment |
| `-Dapp.path` | path / URL / `bs://id` | App binary |
| `-Dapp.url` | URL | Download binary before run |
| `-Dsuite` | suite xml path | TestNG suite |
| `-Dvideo.recording.enabled` | `true` / `false` | Screen recording |
| `-Dvideo.save.on.pass` | `true` / `false` | Keep video on pass (default: fail only) |

## Parallel Execution

Configured in TestNG suite XML — no code changes:

| Mode | Suite |
|------|-------|
| Methods on a device pool | `android-suite.xml` / `ios-suite.xml` (`parallel="methods"`) |
| Android + iOS simultaneously | `parallel-suite.xml` (`parallel="tests"`, nested method parallelism) |

`DriverManager` holds each thread's driver in a `ThreadLocal`, so parallel runs never share state.

## Reports

| Report | Location |
|--------|----------|
| Allure results | `tests/target/allure-results/` → `mvn allure:report -pl tests` |
| Extent HTML | `tests/target/extent-reports/report.html` |
| Screenshots (failures) | `tests/target/screenshots/` |
| Videos | `tests/target/videos/` |
| Logs | `tests/target/logs/` |

## CI/CD

- **GitHub Actions** — `.github/workflows/android-tests.yml` (ubuntu) + `ios-tests.yml` (macos). `workflow_dispatch` inputs for platform, env, video, app path/URL. Cloud creds via `BROWSERSTACK_URL` / `BROWSERSTACK_KEY` secrets.
- **Jenkins** — parametrized `Jenkinsfile` (PLATFORM / ENV / PARALLEL / VIDEO_RECORDING / APP_PATH / APP_URL), publishes Allure + Extent, archives videos/screenshots/logs.

## Writing Tests

Tests target the `LoginPage`-style interface and never know the platform:

```java
public class LoginTest extends BaseTest {
    @Test
    public void validLogin() {
        LoginPage login = PageFactory.getLoginPage();   // returns Android or iOS impl
        login.enterUsername("user@align.com");
        login.enterPassword("Password123!");
        login.tapLogin();
    }
}
```

To add a screen: define the interface in `core`, implement `Android*`/`IOS*` in the platform modules, register a getter in `PageFactory`. See [CLAUDE.md](CLAUDE.md) for the full pattern and environment gotchas.

## Status

Framework compiles clean (core + android + ios + tests). Sample `LoginTest` included with **placeholder locators** — replace `AndroidLoginPage` / `IOSLoginPage` locators with real app IDs before running on-device.

## Docs

- [CLAUDE.md](CLAUDE.md) — build commands, architecture, conventions, environment gotchas
- [docs/superpowers/specs/](docs/superpowers/specs/) — design spec
- [docs/superpowers/plans/](docs/superpowers/plans/) — implementation plan
