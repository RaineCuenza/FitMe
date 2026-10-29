# FitMe — Figma-inspired Android application

**FitMe: A Mobile-Based Weather Outfit Recommendation and Planning System**

This Android/Kotlin project recreates the **layout, navigation, colors and major interactions** of the uploaded **FitMe Mobile UI_UX Design.zip** Figma Make prototype. The Figma export was React/TypeScript, not Kotlin; these are newly authored Jetpack Compose screens connected to the native FitMe app's Room, DataStore and Open-Meteo logic.

## Included screens and journeys

1. Splash → three onboarding screens → style selection → city selection (first run only).
2. Home: FitMe wordmark, city selector, live Open-Meteo weather strip, daily look, recommendation explanation, browse and builder shortcuts.
3. Forecast: current conditions, humidity, precipitation, 7-day maximum/minimum temperatures, styling advice.
4. Explore: two-column grid of **48 offline clothing items**, name search, category chips, filters for color/style/occasion, details, Add to Outfit.
5. Builder: clothing slot selection, real composite preview, outfit name, occasion, optional favorite, Room persistence.
6. Saved outfits: Favorites + All saved looks, open details, edit, favorite/unfavorite, delete.
7. Planner: calendar-style date selection, select an existing outfit, save/update/remove a date plan; planned outfits list via Profile.
8. Profile: location, multiple style preferences, occasion, calendar, light/dark mode, introduction replay.

Screens are newly implemented with Figma's **cream (#F8F6F1), sage (#718876), forest (#344C3D), beige (#EFE8DD)** design tokens, large serif editorial headings, rounded content, and five-item bottom navigation.

### Visual differences from the Figma web prototype

- The Figma web export references remote Unsplash photographs and DM Serif Display/Manrope web fonts that were **not provided as bundled image/font files**. The Android app uses its **48 original local clothing illustrations** for its functional catalog and look preview, with an optional network-loaded hero photo from the Figma design. Offline preview remains available without that photograph.
- Android uses system serif/sans-serif font families, so text metrics may differ slightly from the browser's web fonts.
- The prototype used sample names/weather/dates. This Android version uses **real data** from the weather API and the user's saved outfits. Layout text adjusts accordingly.

## Open in Android Studio on macOS

1. **Back up** the working FitMe project first. Determine which folder Android Studio actually runs (`FitMe` versus `FitMe_Phase1`—your earlier screenshots showed different roots).
2. The ZIP contains files **at the project root** (`app/`, `build.gradle.kts`, `settings.gradle.kts`, etc.). Unzip into the exact directory Android Studio is opening.
3. **Keep the project's existing** `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, and `local.properties` created by Android Studio. These generated/binary files are not provided in this ZIP.
4. In Android Studio choose **File → Sync Project with Gradle Files**, then **Build → Make Project**, and **Run 'app'**.
5. Test onboarding, weather, city change, Explore (48 items), Builder → Save, Favorites, Outfit Details → Plan, and Profile dark mode.

### Merge via Terminal example

If your actual Android Studio project is `~/AndroidStudioProjects/FitMe`:

```bash
cd ~/AndroidStudioProjects
cp -R FitMe FitMe_backup
cd FitMe
unzip -o ~/Downloads/FitMe_Figma_AndroidStudio_Complete.zip
```

If your project is `FitMe_Phase1`, substitute that folder name in both the `cd` and backup commands. **Do not copy into a folder that is not the one open in Android Studio.**

To start a new project: create an Android Studio **Empty Activity (Jetpack Compose)** project with `com.fitme.app`, Kotlin DSL and Min SDK 26. Let Android Studio generate the Gradle wrapper first, then merge the source archive into it.

## Project components

- Kotlin 2.0.21, AGP 8.7.3, Gradle 8.9, JDK 17.
- Compose Material 3 + Navigation Compose.
- Room with preserved `Migration(1,2)`, local SQLite saved outfits and plans.
- DataStore for user settings and onboarding completion.
- Retrofit + OkHttp + Open-Meteo Weather and Geocoding API.
- Coil for the optional editorial image, original 48 PNG clothing illustrations for offline catalog.
- MVVM ViewModels and Repository Pattern.
- No accounts, uploading, paid APIs, Firebase or Tableau.

## Validation and what remains to test

**Checked here:** Kotlin syntax/parsing of all source files; seed count (48), unique IDs, PNG integrity and drawable naming; pure Kotlin search/filter + hot/rainy outfit recommendation tests; Android resources and ZIP integrity.

**Not checked here:** Android Gradle compilation, installation onto emulator/device, runtime layout and live Open-Meteo calls. The authoring container lacks the Android SDK, Gradle distribution and outbound access to Unsplash/Open-Meteo. **This is Android project source code, not a tested APK.**

If Android Studio reports errors, share the **first error line** from the Build Output and the exact local project folder. Avoid upgrading AGP/Kotlin dependencies until the first successful build.

**Design reference:** User-supplied `FitMe Mobile UI_UX Design.zip` (Figma Make export). All Figma-style layouts are implemented in `app/src/main/java/com/fitme/app/ui/wireframe/`.
