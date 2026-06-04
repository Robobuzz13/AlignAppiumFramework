# CLAUDE.md

Guidance for Claude Code working in this repository.

## What This Is

AlignAppiumFramework — dual-platform (Android + iOS) native mobile test automation framework. Java 17 + TestNG + Appium. Maven multi-module. Single test layer runs on both platforms via interface-driven page objects; zero platform conditionals in test code.

## Build & Run

Requires Maven 3.9+ and a JDK 17+ on `PATH`. (If Maven is installed to a custom location,
add its `bin` to `PATH` for the session.)

| Action | Command |
|--------|---------|
| Compile all modules | `mvn compile -pl core,android,ios,tests` |
| Compile test sources | `mvn test-compile -pl tests` |
| Allure report | `mvn allure:report -pl tests` (then `mvn allure:serve -pl tests` to view) |

**Running a suite — install dependency modules first, then run `tests`.** `mvn test -pl tests`
alone does not build/resolve `core`/`android`/`ios`, so `PageFactory`'s reflection can't load
the page objects at runtime. Install once, then run:

```
mvn install -pl core,android,ios -DskipTests
mvn test -pl tests -Dplatform=android -Denv=local <app flags> -Dsuite=src/test/resources/suites/android-suite.xml
```

**App flags** — supply the app one of two ways:
- Installed app: `-Dapp.package=<pkg> -Dapp.activity=<launcher.activity>`
- App binary: `-Dapp.path=/path/app.apk` (or `.app`, download URL, or `bs://id` for cloud)

| Scenario | App flags + suite |
|--------|---------|
| Android, installed app | `-Dapp.package=com.example.app -Dapp.activity=com.example.app.MainActivity -Dsuite=…/android-suite.xml` |
| Android, apk | `-Dapp.path=/path/app.apk -Dsuite=…/android-suite.xml` |
| iOS, local | `-Dplatform=ios -Dapp.path=/path/app.app -Dsuite=…/ios-suite.xml` |
| Both, cloud | `-Dplatform=both -Denv=cloud -Dapp.path=bs://id -Dsuite=…/parallel-suite.xml` |

On-device execution needs: a running Appium server (or `auto.start.server=true`), a connected
device/emulator, the app installed or a binary, and real locators in the page objects.

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
- **Reporting:** `TestListener` captures one screenshot on failure, shared to both Allure + Extent. `@Step` methods (Allure) are mirrored into Extent by `ExtentStepListener` (an Allure `StepLifecycleListener` registered via `META-INF/services`), so both reports show the step breakdown. Optional per-step screenshots: `-Dscreenshot.each.step=true`. Video via `VideoUtils` (config-gated, auto-off on cloud; keep on pass with `-Dvideo.save.on.pass=true`).
- **Steps layer:** keep `@Test` methods thin — put per-screen actions/assertions in `@Step` methods in a separate steps class (e.g. `com.align.steps.*`); the test only orchestrates the sequence.

## Version Compatibility Notes (Critical)

Dependency versions are pinned in the root `pom.xml`. These combinations caused real breakage —
do not reintroduce them when bumping versions:

1. **Selenium ↔ java-client.** java-client 9.2.3's `SupportsContextSwitching` extends
   `org.openqa.selenium.ContextAware`, which later Selenium 4.x releases removed. If Maven
   resolves a Selenium version without that class, `AndroidDriver`/`IOSDriver` fail to load at
   runtime (`NoClassDefFoundError: ContextAware`). The pom pins `selenium.version` to a release
   that still ships it (java-client's declared floor). Keep Selenium and java-client compatible
   when upgrading either.
2. **No Lombok on newer JDKs.** Recent JDKs removed `com.sun.tools.javac.code.TypeTag.UNKNOWN`;
   Lombok 1.18.x crashes the compiler. `DeviceConfig` uses a hand-written builder. Only add Lombok
   back on a Lombok release that supports the JDK in use.
3. **aspectjweaver must match the running JDK.** Allure's `@Step` weaving runs the aspectjweaver
   javaagent; an older weaver cannot parse a newer JDK's bytecode (`Unsupported class file major
   version N`) and silently disables step weaving. `aspectjweaver.version` is a pom property —
   bump it to the latest when running on a newer JDK.
4. **Don't cast to `AndroidDriver`/`IOSDriver` for context/location** in core utils. Some Selenium
   versions remove `ContextAware`/`html5.LocationContext`, breaking the cast at compile. Use
   capability **interfaces**: `InteractsWithApps`, `HasClipboard`, `PushesFiles`/`PullsFiles`,
   `HidesKeyboard`, `LocksDevice`, `SupportsRotation`, `HasNetworkConnection`, `HasNotifications`,
   `AuthenticatesByFinger`. For context switching use `driver.execute(MobileCommand.*)`.
5. **Screen-recording option packages** (`android.screenrecording`, `ios.screenrecording`) don't
   exist in java-client 9.x — use no-arg `startRecordingScreen()`.
6. **Platform class-name prefix:** iOS classes are `IOS*` not `Ios*`. `PageFactory` maps `ios → IOS`.

### Soft-keyboard covering buttons (Android)

After typing into a field, the soft keyboard can cover a button below it and make it "not
clickable". Hide the keyboard before tapping such a button (`KeyboardUtils.hideKeyboard(getDriver())`).

## Adding a New Screen (Pattern)

1. Interface in `core/src/main/java/com/align/pages/XxxPage.java`
2. `android/.../pages/AndroidXxxPage.java` extends `BasePage` implements `XxxPage` (resource-id locators)
3. `ios/.../pages/IOSXxxPage.java` extends `BasePage` implements `XxxPage` (XCUITest locators)
4. Add `getXxxPage()` to `tests/.../factory/PageFactory.java`
5. Add a `@Step` method that drives the screen in the steps class; the `@Test` calls it

**Reusable screens:** when several screens share one widget (e.g. a single-select list used for
multiple questions), model them with one generic page object and identify the specific instance
by a stable text/title rather than creating a page object per screen.

**OS-level dialogs** (runtime permission, "enable GPS", system settings) vary by device and OS
version — handle them best-effort (act if present, skip if not) so the test stays portable.

## Workflow

- Branch off `main` for changes; `main` is the integration branch.
- Conventional Commits (`feat(scope):`, `fix(scope):`, `chore:`, `ci:`, `docs:`).
- Specs in `docs/superpowers/specs/`, plans in `docs/superpowers/plans/`.
- Verify `mvn compile -pl core,android,ios,tests` is green before committing source changes.
