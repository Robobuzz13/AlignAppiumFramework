# AlignAppiumFramework

Dual-platform (Android + iOS) native mobile test automation framework built on **Appium + Java 17 + TestNG**. One test layer drives both platforms — no platform conditionals in test code.

## Features

- **Cross-platform, single test layer** — interface-driven page objects; the same test runs on Android and iOS.
- **Full parallel execution** — ThreadLocal driver management. Run the same test on N devices, or Android + iOS simultaneously, configured purely in TestNG XML.
- **Local + cloud** — local emulators/simulators for dev, cloud farm (BrowserStack/Sauce) for CI; switched by one flag.
- **Dual reporting** — Allure + ExtentReports with a shared `@Step` breakdown; a failure screenshot goes to both, and per-step screenshots are an opt-in flag.
- **Thin tests + steps layer** — `@Test` methods orchestrate; per-screen actions/assertions live as `@Step` methods in a steps class.
- **Video recording** — optional, config-gated, auto-disabled on cloud (farms record themselves).
- **App binary flexibility** — local path, download URL, or cloud app ID per run.
- **16 utility classes** — gestures, keyboard, alerts, permissions, app/device/network control, clipboard, files, biometrics, deep links, WebView context, elements, notifications, screenshots, video.
- **CI/CD ready** — GitHub Actions (Android + iOS) and Jenkins pipelines included.

## Requirements

| Tool | Version |
|------|---------|
| JDK | 17+ |
| Maven | 3.9+ |
| Appium | 2.x+ server + UiAutomator2 (Android) / XCUITest (iOS) drivers |
| Android | SDK + emulator/device |
| iOS | Xcode + simulator/device (macOS only) |

> Newer JDKs: the `selenium.version` and `aspectjweaver.version` pom properties may need to stay
> compatible with the JDK and java-client in use — see [CLAUDE.md](CLAUDE.md) version notes.

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
# 1. Install the dependency modules (so PageFactory's reflection can load page objects at runtime)
mvn install -pl core,android,ios -DskipTests

# 2. Start an Appium server (or set auto.start.server=true)
appium

# 3a. Run against an already-installed Android app
mvn test -pl tests \
  -Dplatform=android -Denv=local \
  -Dapp.package=com.example.app \
  -Dapp.activity=com.example.app.MainActivity \
  -Dsuite=src/test/resources/suites/android-suite.xml

# 3b. ...or against an apk binary
mvn test -pl tests \
  -Dplatform=android -Denv=local \
  -Dapp.path=/path/to/app.apk \
  -Dsuite=src/test/resources/suites/android-suite.xml
```

> `mvn test -pl tests` alone won't resolve `core`/`android`/`ios` — run the install step first
> (or use `mvn install` at the root). If Maven lives in a custom location, add its `bin` to `PATH`.

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
| `-Dapp.package` | package id | Launch an already-installed app (with `-Dapp.activity`) |
| `-Dapp.activity` | launcher activity | Entry activity for an installed app |
| `-Dapp.url` | URL | Download binary before run |
| `-Dsuite` | suite xml path | TestNG suite |
| `-Dvideo.recording.enabled` | `true` / `false` | Screen recording |
| `-Dvideo.save.on.pass` | `true` / `false` | Keep video on pass (default: fail only) |
| `-Dscreenshot.each.step` | `true` / `false` | Attach a screenshot after every `@Step` (default false) |

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
| Allure results | `tests/allure-results/` → `mvn allure:report -pl tests`, view with `mvn allure:serve -pl tests` |
| Extent HTML | `tests/target/extent-reports/report.html` |
| Screenshots (failures) | `tests/target/screenshots/` |
| Videos | `tests/target/videos/` |
| Logs | `tests/target/logs/` |

Both Allure and Extent show the same `@Step` breakdown (steps are mirrored into Extent). With
`-Dscreenshot.each.step=true`, every step also carries a screenshot in both reports.

## CI/CD

- **GitHub Actions** — `.github/workflows/android-tests.yml` (ubuntu) + `ios-tests.yml` (macos). `workflow_dispatch` inputs for platform, env, video, app path/URL. Cloud creds via `BROWSERSTACK_URL` / `BROWSERSTACK_KEY` secrets.
- **Jenkins** — parametrized `Jenkinsfile` (PLATFORM / ENV / PARALLEL / VIDEO_RECORDING / APP_PATH / APP_URL), publishes Allure + Extent, archives videos/screenshots/logs.

## Writing Tests

Tests target platform-agnostic page interfaces and never know the platform. Keep `@Test` methods
thin — put per-screen actions/assertions in `@Step` methods in a steps class, and have the test
orchestrate them:

```java
// steps class (com.align.steps)
public class OnboardingSteps {
    @Step("Verify the login screen and sign in")
    public void signIn(String user, String pass) {
        LoginPage login = PageFactory.getLoginPage();   // returns Android or iOS impl
        Assert.assertTrue(login.isDisplayed());
        login.enterUsername(user);
        login.enterPassword(pass);
        login.tapLogin();
    }
}

// test class — orchestration only
public class LoginTest extends BaseTest {
    private final OnboardingSteps steps = new OnboardingSteps();

    @Test
    public void validLogin() {
        steps.signIn("user@example.com", "Password123!");
    }
}
```

To add a screen: define the interface in `core`, implement `Android*`/`IOS*` in the platform
modules, register a getter in `PageFactory`, and add a `@Step` that drives it. See
[CLAUDE.md](CLAUDE.md) for the full pattern and version-compatibility notes.

## Status

Framework compiles clean (core + android + ios + tests). Includes an end-to-end onboarding test
(`OnboardingFlowTest`, driven by `OnboardingSteps`) exercising ~14 screens — text entry, carousel
swipes, native pickers, place autocomplete, OS permission/GPS dialogs, and a final paywall check —
with Allure + Extent step reporting, optional per-step screenshots, and optional video. Replace the
page-object locators with your app's real IDs before running on-device.

## Docs

- [CLAUDE.md](CLAUDE.md) — build commands, architecture, conventions, environment gotchas
- [docs/superpowers/specs/](docs/superpowers/specs/) — design spec
- [docs/superpowers/plans/](docs/superpowers/plans/) — implementation plan
