# CLAUDE.md

Guidance for Claude Code working in this repository.

## What This Is

AlignAppiumFramework — dual-platform (Android + iOS) native mobile test automation framework. Java 17 + TestNG + Appium. Maven multi-module. Single test layer runs on both platforms via interface-driven page objects; zero platform conditionals in test code.

## Build & Run

Maven is at `D:\TestingTools\apache-maven-3.9.16\bin` (not on system PATH). Each shell session:

```powershell
$env:PATH += ";D:\TestingTools\apache-maven-3.9.16\bin"
```

| Action | Command |
|--------|---------|
| Compile all modules | `mvn compile -pl core,android,ios,tests` |
| Compile test sources | `mvn test-compile -pl tests` |
| Run unit tests (core) | `mvn test -pl core` |
| Run Android suite (local) | `mvn test -pl tests -Dplatform=android -Denv=local -Dapp.path=/path/align.apk -Dsuite=src/test/resources/suites/android-suite.xml` |
| Run iOS suite (local) | `mvn test -pl tests -Dplatform=ios -Denv=local -Dapp.path=/path/align.app -Dsuite=src/test/resources/suites/ios-suite.xml` |
| Both parallel (cloud) | `mvn test -pl tests -Dplatform=both -Denv=cloud -Dapp.path=bs://id -Dsuite=src/test/resources/suites/parallel-suite.xml` |
| Allure report | `mvn allure:report -pl tests` |

On-device test execution needs: running Appium server (or `auto.start.server=true`), connected device/emulator, real app binary, and real locators (current ones are placeholders).

## Module Layout & Dependency Direction

```
core  ←  android  ←┐
  ↑                ├── tests
  └──  ios  ───────┘
```

- `core` — driver lifecycle, config, all 16 utils, BasePage, listeners, reporting. **Depends on nothing internal.**
- `android` / `ios` — platform capabilities, driver factories, page implementations. **Depend on core only.**
- `tests` — page interfaces' consumers (`PageFactory`), `BaseTest`, test classes, suites. **Depends on android + ios.**

`core` must NOT import `android`/`ios`. `DriverFactory` (core) routes to platform factories via **reflection** to avoid the circular dependency. `PageFactory` (tests) does the same for page objects.

## Key Conventions

- **Driver access:** never hold an `AppiumDriver` reference. Use `DriverManager.getDriver()` (ThreadLocal — parallel-safe). Always `removeDriver()` in teardown.
- **Page objects:** interface in `core/pages/`, `Android*`/`IOS*` impls in platform modules. Tests call `PageFactory.getX()` — never instantiate platform classes directly, never branch on platform.
- **Waits:** all waits go through `WaitUtils`. No `Thread.sleep()` (the one polling loop in `ContextUtils.waitForWebViewContext` is the documented exception).
- **Utils:** static methods taking `AppiumDriver` as first param. Platform-only methods guard with `instanceof` and throw `UnsupportedOperationException` (or log-warn for best-effort ops like keyboard).
- **Config priority:** CLI `-D` > env var (UPPER_SNAKE) > properties file > default. All reads through `ConfigLoader.getInstance()`.
- **Reporting:** `TestListener` captures one screenshot on failure, shares to both Allure + Extent. Video via `VideoUtils` (config-gated, auto-off on cloud).

## Environment Gotchas (Critical)

This machine runs **JDK 26** + **java-client 9.2.3** + **Selenium 4.44**. These caused real breakage — do not reintroduce:

1. **No Lombok.** JDK 26 removed `com.sun.tools.javac.code.TypeTag.UNKNOWN`; Lombok 1.18.x crashes the compiler. `DeviceConfig` uses a hand-written builder. Do not add Lombok back unless on a Lombok release that supports JDK 26.
2. **Cannot cast to `AndroidDriver`/`IOSDriver` for context/location.** Selenium 4.44 removed `ContextAware` and `html5.LocationContext`; those driver classes transitively reference them, so the cast fails at compile. Use capability **interfaces** instead: `InteractsWithApps`, `HasClipboard`, `PushesFiles`/`PullsFiles`, `HidesKeyboard`, `LocksDevice`, `SupportsRotation`, `HasNetworkConnection`, `HasNotifications`, `AuthenticatesByFinger`. For context switching use `driver.execute(MobileCommand.*)`.
3. **Screen-recording option packages** (`android.screenrecording`, `ios.screenrecording`) don't exist in 9.x — use no-arg `startRecordingScreen()`.
4. **Platform class-name prefix:** iOS classes are `IOS*` not `Ios*`. `PageFactory` maps `ios → IOS`.

## Adding a New Screen (Pattern)

1. Interface in `core/src/main/java/com/align/pages/XxxPage.java`
2. `android/.../pages/AndroidXxxPage.java` extends `BasePage` implements `XxxPage` (resource-id locators)
3. `ios/.../pages/IOSXxxPage.java` extends `BasePage` implements `XxxPage` (XCUITest locators)
4. Add `getXxxPage()` to `tests/.../factory/PageFactory.java`
5. Write test in `tests/.../tests/` extending `BaseTest`, call `PageFactory.getXxxPage()`

## Workflow

- Branch off `main` for changes; `main` is the integration branch.
- Conventional Commits (`feat(scope):`, `fix(scope):`, `chore:`, `ci:`, `docs:`).
- Specs in `docs/superpowers/specs/`, plans in `docs/superpowers/plans/`.
- Verify `mvn compile -pl core,android,ios,tests` is green before committing source changes.
