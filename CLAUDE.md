# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Poop-A-Day is a dual-platform native mobile app for tracking bathroom visits. iOS is built with SwiftUI, Android with Jetpack Compose. Monetized via Google AdMob (banner + interstitial). All changes must be applied to both platforms.

## Build & Run

### iOS
```bash
open "iOS/Poop-A-Day/Poop-A-Day.xcodeproj"
# Cmd+R in Xcode to build and run
# Development Team: N8P74R8894
# Bundle ID: com.Sozolabs.Poop-A-Day
```

### Android
```bash
cd Android && ./gradlew assembleDebug     # Build debug APK
cd Android && ./gradlew bundleRelease     # Build release AAB
cd Android && ./gradlew clean             # Clean build cache
# Or open Android/ folder in Android Studio and press Run
# Package: com.sozolabs.poop_a_day
```

## Architecture

### Three-tab navigation (Tracker / Log / Stats)
- **Tracker**: Animated toilet button that logs events with a multi-phase poop-falling animation (fall → shrink into toilet → impact shake/splash → interstitial ad)
- **Log**: Reverse-chronological history of all events
- **Stats**: Aggregated counts for today/week/month/year

### Data Layer
Both platforms persist logs as JSON files (`poop_logs.json`) in local storage. No remote backend.
- **iOS**: `DataStore` (ObservableObject) with `JSONEncoder`/`JSONDecoder` to Documents directory
- **Android**: `PoopRepository` with GSON + coroutines (`Dispatchers.IO`) to app files directory

### Key Architectural Decisions
- No global state manager — reactive data flows from repository → screen state → UI
- Animation timing is coordinated via delayed dispatches (DispatchQueue on iOS, coroutine delay on Android)
- Theme preference stored locally (`@AppStorage` on iOS, `rememberSaveable` on Android)
- Localized to English and Spanish (iOS: `Localizable.xcstrings`, Android: `values/strings.xml` + `values-es/strings.xml`)

## Platform Parity

Changes to one platform must be mirrored to the other. Key paired files:

| Concept | iOS | Android |
|---------|-----|---------|
| Tracker animation | `TrackerView.swift` | `ui/TrackerScreen.kt` |
| Data persistence | `DataStore.swift` | `data/PoopRepository.kt` |
| Stats calculation | `StatsView.swift` | `ui/StatsScreen.kt` |
| Tile background | `TileBackground.swift` | `ui/TileBackground.kt` |
| Ad config | `AdConfig.swift` | `ads/AdConfig.kt` |
| Strings (ES) | `Localizable.xcstrings` | `res/values-es/strings.xml` |

## AdMob Configuration

Production IDs are already configured. iOS IDs in `AdConfig.swift` + `Info.plist`. Android IDs in `ads/AdConfig.kt` + `AndroidManifest.xml`.

| | iOS | Android |
|--|-----|---------|
| App ID | `ca-app-pub-5686344303496334~5462412605` | `ca-app-pub-5686344303496334~3083661274` |
| Banner | `ca-app-pub-5686344303496334/9649069624` | `ca-app-pub-5686344303496334/8144416261` |
| Interstitial | `ca-app-pub-5686344303496334/1076311584` | `ca-app-pub-5686344303496334/3501362622` |

## Animation Pipeline (TrackerScreen)

Both platforms implement identical 3-phase animation:
1. **Fall** (0.7s iOS / 0.5s Android): Poop drops from off-screen with random rotation
2. **Shrink** (0.2s): Scale down + fade as poop "enters" toilet
3. **Impact**: Toilet squish (spring), horizontal shake (8-step sequence), blue splash droplets (8 random angles via sin/cos), counter pop animation, haptic feedback (iOS only), then interstitial ad

## Localization

iOS uses SwiftUI's `LocalizedStringKey` (automatic for `Text("literal")`). For custom components, use `LocalizedStringKey` type instead of `String` to enable lookup.

Android uses standard `stringResource(R.string.key)` pattern.
