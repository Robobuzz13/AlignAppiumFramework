# Onboarding Flow Test — Design

Date: 2026-06-04
App under test: `com.dailyinsights` (DailyInsights / Align), Android, real device SM-G975F

## Goal

Add a dual-platform page-object pair plus a TestNG test that validates the onboarding
hand-off: from a daily-insight screen with a **Next** button to a splash carousel screen
with a **Skip** button and multiple splash options.

Test assertion chain:
1. Insight screen shows a visible **Next** button.
2. Tapping **Next** navigates to the splash carousel.
3. Splash carousel shows a visible **Skip** button.
4. Splash carousel exposes more than one splash option (page-indicator dots).
5. Swiping through every splash screen reaches the signup screen.
6. Signup screen accepts a random email/password and the **Signup** button is tappable.
7. Name screen accepts a random name and the **Next** button advances to birth details.
8. Birth-details screen accepts a date, time, and location, and **Next** advances to the
   birth-chart summary.
9. Birth-chart summary shows **Edit Birth Information** and **Continue** advances the flow.
10. After chart generation a personalized insight screen appears (reuses the opening insight
    layout); **Next** advances to the Location Access permission screen.
11. Location Access screen is displayed; **Continue** triggers the Android system location
    permission dialog.
12. The system permission dialog is allowed via `PermissionUtils.allowPermission`, returning
    to the app (which then shows an app-level GPS-enable dialog).
13. The GPS-enable dialog is displayed; tapping **YES** opens the Android system Location
    settings.
14. The Location toggle is switched on and **Back** returns to the app (which lands back on
    the Location Access screen, now satisfiable).
15. With location enabled, **Continue** advances past the Location Access screen to the
    interests-selection screen.

## Locators (captured live via `adb uiautomator dump`)

### Screen A — Insight (`com.dailyinsights/.signup.ui.activity.DayNakshatraActivity`)

| Element        | Locator (resource-id)                | Type     |
|----------------|--------------------------------------|----------|
| Next button    | `com.dailyinsights:id/txtContinue`   | TextView (clickable, text="Next") |
| Insight text   | `com.dailyinsights:id/txtContent`    | TextView |
| Sub text       | `com.dailyinsights:id/txtSubText`    | TextView |
| Top icon       | `com.dailyinsights:id/imgTopIcon`    | ImageView |

### Screen B — Splash carousel (post-Next)

| Element            | Locator (resource-id)               | Type     |
|--------------------|-------------------------------------|----------|
| Skip button        | `com.dailyinsights:id/txtSkip`      | TextView (clickable, text="skip") |
| Carousel pager     | `com.dailyinsights:id/viewPager`    | ViewPager |
| Page indicator dot | `com.dailyinsights:id/worm_dot`     | ImageView (one per splash; 7 observed) |
| Title              | `com.dailyinsights:id/txtTitle`     | TextView |
| Description        | `com.dailyinsights:id/txtDes`       | TextView |

Swiping left (`GestureUtils.swipeLeft`) advances the carousel; 6 splash titles observed
(Birth Chart, Game Changer, Time It Right, Astro-Hacks, Real People). After the last
splash, the signup screen appears.

### Screen C — Signup (post-carousel, `StartupActivity`)

| Element        | Locator (resource-id)               | Type     |
|----------------|-------------------------------------|----------|
| Email field    | `com.dailyinsights:id/edtEmail`     | EditText |
| Password field | `com.dailyinsights:id/edtPassword`  | EditText |
| Signup button  | `com.dailyinsights:id/txtSingup`    | TextView (clickable) |

Note: the app misspells the clickable button id as `txtSingup`. The correctly-spelled
`txtSignup` is the non-clickable screen header — do not target it.

### Screen D — Name (post-signup, `StartupActivity`)

| Element     | Locator (resource-id)               | Type     |
|-------------|-------------------------------------|----------|
| Name field  | `com.dailyinsights:id/edtUserName`  | EditText |
| Next button | `com.dailyinsights:id/txtContinue`  | TextView (clickable; same id as the insight-screen Next) |

Entering a name and tapping Next advances to the birth-details screen, which greets the
user by name ("Good Evening 🌙 &lt;name&gt;") — confirming the value was accepted.

### Screen E — Birth details (post-name, `StartupActivity`)

| Element        | Locator (resource-id)                  | Type / behaviour |
|----------------|----------------------------------------|------------------|
| Birth date     | `com.dailyinsights:id/txtBirthDate`    | opens a DatePicker dialog |
| Birth time     | `com.dailyinsights:id/txtBirthTime`    | opens a time dialog |
| Birth location | `com.dailyinsights:id/txtBirthLocation`| opens Google Places autocomplete |
| Picker confirm | `com.dailyinsights:id/ButtonSet`       | shared Set button for the date and time dialogs |
| Places search  | `com.dailyinsights:id/places_autocomplete_search_bar` | EditText in the autocomplete screen |
| Places result  | `com.dailyinsights:id/places_autocomplete_prediction_primary_text` | first row selects the city |
| Next           | `com.dailyinsights:id/imgNext`         | advances to the birth-chart summary |

The page object sets a **random past date** and a **random time**, then selects the first
location prediction:

- **Date** — the three NumberPicker spinners (`android:id/numberpicker_input`, order
  month/day/year) ignore typed text, so the page object scrolls each column down a random
  number of steps (year 20–45 back) via vertical swipes. Exact value is not targeted — any
  past date is acceptable — which avoids the off-by-one oscillation an exact-target loop hits
  when a fling moves more than one step. `ButtonSet` confirms.
- **Time** — `editTextHours` / `editTextMinutes` are plain EditText fields; the page object
  `sendKeys` a random 24-hour time (`clear()` then type) and confirms with `ButtonSet`.

Tapping Next advances to a birth-chart summary that echoes the entered date/time/location,
confirming all values were accepted.

Swipe mechanics on the NumberPicker were verified live via adb (a downward swipe spanning
wider than the EditText scrolls one or more steps); the Appium-driver swipe path
(`GestureUtils.swipe`) is not yet runtime-verified against an Appium session.

### Screen F — Birth-chart summary (post-birth-details, `StartupActivity`)

| Element            | Locator (resource-id)                  | Type     |
|--------------------|----------------------------------------|----------|
| Edit birth info    | `com.dailyinsights:id/txtEditBirthInfo`| TextView ("✏️ Edit Birth Information") |
| Continue           | `com.dailyinsights:id/imgNext`         | TextView (advances the flow) |

The summary echoes the entered birth date/time/location (`birthDate`, `txtBirthTime`,
`txtBirthLocation`). The page object asserts Edit Birth Information is displayed and taps
Continue, which advances to a chart-generation loading screen.

### Screen G — Personalized insight (post-chart-generation, `StartupActivity`)

After the chart loads, the app shows a personalized insight screen that reuses the **same
layout and resource-ids as the opening insight screen** (`imgTopIcon`, `txtContent`,
`txtSubText`, `txtContinue`). No new page object is needed — `InsightPage` is reused. The
test asserts the Next button is visible and taps it, which advances to the Location Access
permission screen.

### Screen H — Location Access (post-insight, `StartupActivity`)

| Element  | Locator (resource-id)              | Type     |
|----------|------------------------------------|----------|
| Title    | `com.dailyinsights:id/txtSignup`   | TextView (text "Location Access"; the id is reused, so confirm by text) |
| Continue | `com.dailyinsights:id/txtContinue` | TextView (clickable) |

The page object asserts the title text is "Location Access" and taps Continue. Continue
launches the **Android system location-permission dialog**
(`com.android.packageinstaller` `GrantPermissionsActivity`, with `permission_allow_button` /
`permission_deny_button`). That OS dialog is handled by `PermissionUtils.allowPermission`,
called from the test as a step (not modelled as an app page object, since it is an OS-level
dialog).

`PermissionUtils` originally only knew the Android 11+ dialog ids
(`com.android.permissioncontroller`). This device (Samsung S10) uses the legacy
`com.android.packageinstaller` ids, so those were added as a fallback to `allowPermission`
and `denyPermission`, making permission handling work across Android versions. Verified
live: a single Allow tap returned to the app, which then showed an app-level "Your GPS seems
to be disabled" dialog (`android:id/button1` YES / `button2` NO) — the next screen in the flow.

### Screen I — GPS-enable dialog (post-permission, app AlertDialog)

| Element | Locator (resource-id)   | Type   |
|---------|-------------------------|--------|
| Message | `android:id/message`    | TextView ("Your GPS seems to be disabled, do you want to enable it?") |
| YES     | `android:id/button1`    | Button |
| NO      | `android:id/button2`    | Button |

This is a standard Android `AlertDialog` (view-based, not a WebDriver alert), so `GpsDialogPage`
taps the buttons by id rather than using `AlertUtils.acceptAlert`. `isGpsDialogDisplayed`
confirms the dialog by checking the message text contains "GPS" (the `button1`/`message` ids are
generic to all AlertDialogs). Verified live: tapping YES opens the Android system Location
settings (`com.android.settings` `LocationSettingsActivity`, `switch_widget` "Location, Off").

### Screen J — System Location settings (cross-app, `com.android.settings`)

| Element          | Locator (resource-id)                      | Type   |
|------------------|--------------------------------------------|--------|
| Location toggle  | `com.android.settings:id/switch_widget`    | Switch ("Location, Off"/"Location, On") |

`LocationSettingsPage.enableLocation` reads the switch's `checked` attribute and taps it only
if off (idempotent). `returnToApp` presses the Android Back key via
`KeyboardUtils.pressBack` — Settings was launched from the app, so one Back returns to it.
Verified live: the toggle flipped Off→On (`checked` false→true) and Back returned to the app,
landing on the Location Access screen (now satisfiable, since location is enabled).

### Advancing past Location Access

Once location is enabled, the existing `LocationAccessPage.tapContinue()` advances past the
screen (no permission re-prompt) to the interests-selection screen ("What brings you to the
align27 app today?"). The transition needs a brief moment while the app acquires a location
fix; the test relies on `isLocationAccessDisplayed()`'s built-in `isElementVisible` wait to
absorb that delay (asserting the screen is *gone* after Continue). Verified live: Continue
advanced to the interests screen (`rvInsightsList` with options such as "Explore My Birth
Chart", "Understand My Future", and a `txtSubmit` Continue button).

## Components (Approach A — one page object per screen)

### Interfaces (`core/src/main/java/com/align/pages/`)

```java
public interface InsightPage {
    boolean isNextButtonVisible();
    void tapNext();
    String getInsightText();
}

public interface SplashCarouselPage {
    boolean isSkipButtonVisible();
    int getSplashOptionCount();   // counts worm_dot elements
    String getTitle();
    void tapSkip();
    void swipeToNextSplash();     // GestureUtils.swipeLeft
}

public interface SignupPage {
    boolean isSignupScreenVisible();
    void enterEmail(String email);
    void enterPassword(String password);
    void tapSignup();
}

public interface NamePage {
    boolean isNameScreenVisible();
    void enterName(String name);
    void tapNext();
}

public interface BirthDetailsPage {
    boolean isBirthDetailsScreenVisible();
    void setBirthDate();                 // random past date (swipes the spinners)
    void setBirthTime();                 // random time (types hours/minutes)
    void setBirthLocation(String city);  // searches and selects the first match
    void tapNext();
}

public interface BirthChartSummaryPage {
    boolean isEditBirthInfoDisplayed();
    void tapContinue();
}

public interface LocationAccessPage {
    boolean isLocationAccessDisplayed();
    void tapContinue();
}

public interface GpsDialogPage {
    boolean isGpsDialogDisplayed();
    void tapYes();
    void tapNo();
}

public interface LocationSettingsPage {
    boolean isLocationSettingsDisplayed();
    void enableLocation();   // idempotent — toggles on only if off
    void returnToApp();      // Android Back
}
```

### Android impls (`android/.../pages/`) — real locators

- `AndroidInsightPage extends BasePage implements InsightPage`
- `AndroidSplashCarouselPage extends BasePage implements SplashCarouselPage`
  - `getSplashOptionCount()` uses `driver.findElements(By.id(...worm_dot))` and returns size.

### iOS impls (`ios/.../pages/`) — placeholder

- `IOSInsightPage`, `IOSSplashCarouselPage` with placeholder
  accessibility-id locators and `// TODO: inspect on real iOS device`.
  Compiles and satisfies the `PageFactory` reflection contract; not expected to run
  until iOS locators are captured.

### PageFactory (`tests/.../factory/PageFactory.java`)

Add:
```java
public static InsightPage getInsightPage()             { return create("InsightPage"); }
public static SplashCarouselPage getSplashCarouselPage(){ return create("SplashCarouselPage"); }
```

### Test (`tests/src/test/java/com/align/tests/OnboardingFlowTest.java`)

`OnboardingFlowTest extends BaseTest`:
- `onboardingThroughSignup()`:
  - assert `insightPage.isNextButtonVisible()`
  - `insightPage.tapNext()`
  - assert `splashPage.isSkipButtonVisible()`
  - assert `splashPage.getSplashOptionCount() > 1`
  - swipe `swipeToNextSplash()` in a guarded loop (max 10) until `signupPage.isSignupScreenVisible()`
  - assert `signupPage.isSignupScreenVisible()`
  - enter random email (`qa_<uuid8>@example.com`) + password (`Pass<uuid6>!`), `tapSignup()`
  - assert `namePage.isNameScreenVisible()`
  - enter random name (`QA <uuid5>`), `tapNext()`
  - assert `birthPage.isBirthDetailsScreenVisible()`
  - `setBirthDate()`, `setBirthTime()`, `setBirthLocation("London")`, `tapNext()`
  - assert `summaryPage.isEditBirthInfoDisplayed()`
  - `summaryPage.tapContinue()`
  - assert `chartInsight.isNextButtonVisible()` (reused `InsightPage`), `tapNext()`
  - assert `locationPage.isLocationAccessDisplayed()`, `tapContinue()`
  - `PermissionUtils.allowPermission(driver)` to dismiss the system permission dialog
  - assert `gpsDialog.isGpsDialogDisplayed()`, `tapYes()`
  - assert `locationSettings.isLocationSettingsDisplayed()`, `enableLocation()`, `returnToApp()`
  - assert back on Location Access, `tapContinue()`, assert Location Access dismissed

## Error handling

- Visibility checks go through `BasePage` / `WaitUtils` explicit waits (no `Thread.sleep`).
- `getSplashOptionCount()` returns 0 if no dots found; test asserts `> 1`, so a missing
  carousel fails loudly rather than NPE.

## Out of scope

- iOS execution (placeholder locators only).
- Asserting specific insight/splash text content (dynamic, lunar-date dependent).
- Swiping between splash screens (count assertion is enough for this flow).
