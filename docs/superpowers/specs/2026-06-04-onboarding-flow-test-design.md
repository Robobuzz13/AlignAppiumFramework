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
- `nextLeadsToSplashWithSkipAndOptions()`:
  - assert `insightPage.isNextButtonVisible()`
  - `insightPage.tapNext()`
  - assert `splashPage.isSkipButtonVisible()`
  - assert `splashPage.getSplashOptionCount() > 1`

## Error handling

- Visibility checks go through `BasePage` / `WaitUtils` explicit waits (no `Thread.sleep`).
- `getSplashOptionCount()` returns 0 if no dots found; test asserts `> 1`, so a missing
  carousel fails loudly rather than NPE.

## Out of scope

- iOS execution (placeholder locators only).
- Asserting specific insight/splash text content (dynamic, lunar-date dependent).
- Swiping between splash screens (count assertion is enough for this flow).
