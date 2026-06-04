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

## Error handling

- Visibility checks go through `BasePage` / `WaitUtils` explicit waits (no `Thread.sleep`).
- `getSplashOptionCount()` returns 0 if no dots found; test asserts `> 1`, so a missing
  carousel fails loudly rather than NPE.

## Out of scope

- iOS execution (placeholder locators only).
- Asserting specific insight/splash text content (dynamic, lunar-date dependent).
- Swiping between splash screens (count assertion is enough for this flow).
