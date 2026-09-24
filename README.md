# Loop'in — Private Subscription Tracker for India 🇮🇳

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-brightgreen.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.00-blue.svg)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Room-SQLite%20(100%25%20Offline)-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Privacy](https://img.shields.io/badge/Internet%20Permission-None%20(0KB%20Cloud)-success.svg)](#zero-internet-100-offline-privacy-guarantee)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

Loop'in is a modern, native Android application designed to eliminate **subscription sprawl** in India across OTT streaming services (Netflix, JioHotstar, Spotify), developer & AI tools (Claude, Gemini), professional software (Figma, LinkedIn Premium), and lifestyle services.

Built from the ground up with **Jetpack Compose**, **Clean Architecture**, and an **uncompromising privacy-first stance**: the app operates **100% offline with zero internet permissions**.

---

## Table of Contents

- [The Problem: Subscription Sprawl in India](#the-problem-subscription-sprawl-in-india)
- [Zero Internet: 100% Offline Privacy Guarantee](#zero-internet-100-offline-privacy-guarantee)
- [The RBI Pre-Debit Mandate Concept](#the-rbi-pre-debit-mandate-concept)
- [Design System & UI Experience](#design-system--ui-experience)
- [Screen Tour & Features](#screen-tour--features)
- [Technical Architecture](#technical-architecture)
- [Directory Structure](#directory-structure)
- [Getting Started & Setup](#getting-started--setup)
  - [Prerequisites](#prerequisites)
  - [Open in Android Studio](#open-in-android-studio)
  - [Run on Android Emulator](#run-on-android-emulator)
  - [Run on a Physical Android Device](#run-on-a-physical-android-device)
  - [Run Unit Tests](#run-unit-tests)
  - [Build Debug APK](#build-debug-apk)
- [Design Decisions & Trade-offs](#design-decisions--trade-offs)

---

## The Problem: Subscription Sprawl in India

With the rise of UPI AutoPay and e-mandates on credit/debit cards, Indians subscribe to an ever-growing array of digital tools and entertainment platforms:
- **OTT & Media**: Netflix, JioHotstar, Spotify, YouTube Premium.
- **Generative AI & Tech**: Claude Pro, Google Gemini Advanced, ChatGPT Plus.
- **Design & Productivity**: Figma, LinkedIn Premium, Notion, GitHub Copilot.
- **Cloud & Utilities**: Google One, Apple iCloud, Microsoft 365.

Managing renewal dates, recurring charges, and pricing tiers across disparate banking apps and emails leads to forgotten renewals, accidental auto-debits, and zero visibility into total recurring burn rate.

---

## Zero Internet: 100% Offline Privacy Guarantee

Loop'in was architected with a fundamental principle: **your financial commitments are nobody else's business.**

Most finance apps require:
- Complete access to your personal SMS inbox.
- Bank Account Aggregator (AA) linking with Net Banking credentials.
- Continuous upload of transaction data to cloud servers.

Loop'in takes the exact opposite approach:
```xml
<!-- In AndroidManifest.xml: NO INTERNET PERMISSION EXISTS -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Zero network permissions. No android.permission.INTERNET. -->
</manifest>
```

- **0 Network Requests**: The app literally cannot make an HTTP request; the operating system prevents network access at the kernel level.
- **100% On-Device SQLite**: All subscriptions, renewals, categories, and payment cycle metadata live inside an encrypted, local Room database.
- **Zero Third-Party Telemetry**: No analytics SDKs, no trackers, no remote crash loggers.

---

## The RBI Pre-Debit Mandate Concept

Under the **Reserve Bank of India (RBI) Recurring Payment Framework (e-mandates)**:
1. Every issuer bank and payment aggregator (NPCI, UPI AutoPay, Visa/Mastercard/RuPay) is required by law to send a **pre-debit notification** to the consumer at least **24 to 48 hours** before any recurring transaction is charged.
2. The notification must contain the merchant name, amount to be debited, date of debit, and unique mandate reference number.

### How Loop'in Leverages This
Instead of invasively reading personal conversations or asking for bank login credentials, Loop'in is architected to utilize **local on-device notification parsing**:
- It listens purely for system notifications dispatched by authorized banking and UPI apps (e.g., HDFC, ICICI, SBI, Google Pay, PhonePe, Paytm).
- It extracts the merchant name, amount in INR, and renewal date locally using on-device regex and natural language heuristics.
- It automatically updates the user's renewal calendar without a single byte of data leaving the phone.

Users can also manage subscriptions via the fast, intuitive **Manual Entry** modal with built-in plan tiers and pricing presets.

---

## Design System & UI Experience

The Loop'in Design System is built for modern Android with high tactile feedback, fluid animations, and strict WCAG AA contrast compliance.

### Brand Palette
| Color Name | Hex Code | Preview | Purpose |
|---|---|---|---|
| **Brand Orange** | `#E95810` | ![#E95810](https://via.placeholder.com/15/E95810/E95810.png) | Primary brand accent, active tabs, brand headers, links |
| **Deep Charcoal** | `#16110F` | ![#16110F](https://via.placeholder.com/15/16110F/16110F.png) | Primary typography, headers, dark charcoal card containers |
| **Primary Button Gradient** | `#3D3C3B` to `#16110F` | — | Primary action buttons with linear gradient and `#FFFFFF` text |
| **Pure White** | `#FFFFFF` | ![#FFFFFF](https://via.placeholder.com/15/FFFFFF/FFFFFF.png) | Surface container, content sheets, button text, crisp contrast |
| **Neutral Gray** | `#767676` | ![#767676](https://via.placeholder.com/15/767676/767676.png) | Subtitles, inactive tabs, secondary descriptions |
| **Accent Green** | `#2E7D32` | ![#2E7D32](https://via.placeholder.com/15/2E7D32/2E7D32.png) | Active status pills, safe renewal badges |
| **Warning Orange**| `#F57C00` | ![#F57C00](https://via.placeholder.com/15/F57C00/F57C00.png) | Plan count indicator diamonds, upcoming renewal warnings |
| **Danger Red** | `#EF4444` | ![#EF4444](https://via.placeholder.com/15/EF4444/EF4444.png) | Form close button, delete subscription action |

### Typography & Physics
- **Font**: Bundled **Inter Tight** typography (`res/font/inter_tight.ttf`) in Regular, Medium, SemiBold, and Bold weights.
- **Indian Numbering System**: Custom domain formatter supporting Indian Lakhs and Crores (e.g., `₹ 2,912.07` monthly, `₹ 34,944.84` annual projection).
- **Spring Animations**: Spring-based physics (`Spring.DampingRatioMediumBouncy`) on overlay pop-ups and anticipatory scale-down transitions.
- **Directional Navigation**: Lateral sliding animations for top-level navigation bar switching; bottom-to-top modal slide for subscription details; global "move-out" transitions on back navigation that smoothly reveal the previous screen underneath.

---

## Screen Tour & Features

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│                 │       │                 │       │                 │
│   Home Screen   │ ────> │  Discover Apps  │ ────> │ Subscription    │
│  (Hero & Spend) │       │ (Plans & Icons) │       │  Detail Modal   │
│                 │       │                 │       │                 │
└────────┬────────┘       └─────────────────┘       └─────────────────┘
         │
         │ (Tap "+" Tab)
         ▼
┌─────────────────┐       ┌─────────────────┐
│ Add Overlay     │ ────> │ New Subscription│
│ (Bouncy Spring) │       │  (Manual Form)  │
└─────────────────┘       └─────────────────┘
```

1. **Home Screen (`HomeScreen.kt`)**:
   - Striped blue hero section with parallax-locked background.
   - Monthly recurring burn card and annual projection.
   - Expandable "Coming Up" renewals drawer with urgent renewal counts.
   - Quick list of recent subscriptions.
2. **Discover / Explore (`DiscoverScreen.kt`)**:
   - Featured brand artwork cards (Netflix, Spotify, Gemini).
   - Popular Indian apps list (JioHotstar, Claude AI, Figma, LinkedIn).
   - Vector diamond plan indicators (`PlanIndicatorBadge`).
   - Instant search filtering by service name and category.
3. **Add Subscription Overlay (`AddSubscriptionOverlay.kt`)**:
   - Physics-based spring scale entrance with backdrop dimming.
   - **Auto Detect Subscriptions**: RBI notification auto-detection engine.
   - **Manual Entry**: Interactive form entry with anticipatory bounce exit.
4. **New Subscription Screen (`NewSubscriptionScreen.kt`)**:
   - Subscription name input with inline auto-suggestions.
   - Category picker (Entertainment, Productivity, Music, Cloud Storage, Lifestyle).
   - Billing cycle selector (Weekly, Monthly, Quarterly, Yearly).
   - Plan tier selection (Basic, Standard, Premium) with real-time INR price updating.
5. **My Subs (`SubscriptionsListScreen.kt`)**:
   - Complete library of active and inactive subscriptions.
   - Filter chips: `All`, `Upcoming`, `Expired`, `Free Trial`.
   - 56dp squircle brand icons with renewal dates and billing cadence.
6. **Profile & Settings (`ProfileScreen.kt`)**:
   - User profile card with avatar and subscription count.
   - Account settings and invite actions.
   - Developer tools: Seed sample Indian subscriptions or clear local Room SQLite database with instant UI updates.
7. **Notifications Timeline (`NotificationsScreen.kt`)**:
   - 7-day renewal countdown timeline with urgency badges (`Due in 3 days`, `Due in 4 days`).
8. **Subscription Details (`SubscriptionDetailScreen.kt`)**:
   - Bottom-to-top modal presentation.
   - Breakdown of billing cycle, amount, next due date, and detection signal.
   - Pause tracking and remove subscription actions.

---

## Technical Architecture

Loop'in adheres to **Modern Android Development (MAD)** standards and follows Google's recommended **Clean Architecture** patterns:

```
app/
 └── src/
      ├── main/
      │    ├── java/com/loopin/app/
      │    │    ├── data/               <-- Data Layer: Room DB, Entities, DAOs, Repositories
      │    │    │    ├── db/
      │    │    │    │    ├── entity/
      │    │    │    │    ├── dao/
      │    │    │    │    └── LoopInDatabase.kt
      │    │    │    └── repository/
      │    │    ├── domain/             <-- Domain Layer: Pure business logic & models
      │    │    │    ├── model/
      │    │    │    └── logic/
      │    │    ├── ui/                 <-- UI Layer: Jetpack Compose screens & ViewModels
      │    │    │    ├── components/
      │    │    │    ├── home/
      │    │    │    ├── discover/
      │    │    │    ├── subscription/
      │    │    │    ├── settings/
      │    │    │    ├── notifications/
      │    │    │    ├── navigation/
      │    │    │    └── theme/
      │    │    └── di/                 <-- Dependency Injection (AppContainer)
      │    └── res/                     <-- Vector assets, brand drawables, fonts
      └── test/                         <-- Unit tests (Robolectric / JUnit4 / Coroutines)
```

### Key Libraries & Stack
- **UI Toolkit**: Jetpack Compose (BOM `2024.09.00`) + Material 3
- **Architecture**: MVVM with `StateFlow` and `viewModelScope`
- **Database**: Room `2.6.1` with SQLite and KSP code generation
- **Coroutines**: Kotlinx Coroutines `1.9.0` with `runTest` and `StandardTestDispatcher`
- **Navigation**: Navigation Compose `2.8.0` with custom animated transitions
- **Build System**: Gradle 8.9 + Android Gradle Plugin 8.7.3 + Kotlin 2.0.21

---

## Directory Structure

```
loop-in/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/loopin/app/
│       │   │   ├── di/
│       │   │   │   └── AppContainer.kt
│       │   │   ├── data/
│       │   │   │   ├── db/
│       │   │   │   │   ├── Converters.kt
│       │   │   │   │   ├── LoopInDatabase.kt
│       │   │   │   │   ├── dao/SubscriptionDao.kt
│       │   │   │   │   └── entity/SubscriptionEntity.kt
│       │   │   │   └── repository/
│       │   │   │       ├── SubscriptionRepository.kt
│       │   │   │       ├── SubscriptionRepositoryImpl.kt
│       │   │   │       └── SampleDataProvider.kt
│       │   │   ├── domain/
│       │   │   │   ├── logic/
│       │   │   │   │   ├── AlertSelector.kt
│       │   │   │   │   ├── IndianCurrencyFormatter.kt
│       │   │   │   │   ├── MonthlySpendCalculator.kt
│       │   │   │   │   ├── NextDueDateCalculator.kt
│       │   │   │   │   └── RecentSubscriptionsSelector.kt
│       │   │   │   └── model/
│       │   │   │       ├── BillingCycle.kt
│       │   │   │       ├── RenewalAlert.kt
│       │   │   │       ├── Subscription.kt
│       │   │   │       ├── SubscriptionSource.kt
│       │   │   │       └── SubscriptionStatus.kt
│       │   │   └── ui/
│       │   │       ├── components/
│       │   │       │   ├── AddSubscriptionOverlay.kt
│       │   │       │   ├── CurrencyText.kt
│       │   │       │   ├── LoopInBottomBar.kt
│       │   │       │   ├── MonogramTile.kt
│       │   │       │   ├── SourcePill.kt
│       │   │       │   └── StatusBadge.kt
│       │   │       ├── discover/DiscoverScreen.kt
│       │   │       ├── home/
│       │   │       │   ├── ComingUpCard.kt
│       │   │       │   ├── EmptyHomeState.kt
│       │   │       │   ├── HeaderSection.kt
│       │   │       │   ├── HomeScreen.kt
│       │   │       │   ├── HomeUiState.kt
│       │   │       │   ├── HomeViewModel.kt
│       │   │       │   ├── RecentSubscriptionsSection.kt
│       │   │       │   └── SpendHeroCard.kt
│       │   │       ├── navigation/
│       │   │       │   ├── LoopInNavHost.kt
│       │   │       │   └── NavRoutes.kt
│       │   │       ├── notifications/NotificationsScreen.kt
│       │   │       ├── settings/ProfileScreen.kt
│       │   │       ├── subscription/
│       │   │       │   ├── NewSubscriptionScreen.kt
│       │   │       │   ├── SubscriptionDetailScreen.kt
│       │   │       │   └── SubscriptionsListScreen.kt
│       │   │       └── theme/
│       │   │           ├── Color.kt
│       │   │           ├── Shape.kt
│       │   │           ├── Spacing.kt
│       │   │           ├── Theme.kt
│       │   │           └── Type.kt
│       │   └── res/
│       │       ├── drawable/ (card_netflix.png, card_spotify.png, icon_hotstar.png, etc.)
│       │       ├── font/inter_tight.ttf
│       │       └── values/
│       └── test/java/com/loopin/app/
│           ├── domain/
│           │   ├── AlertSelectorTest.kt
│           │   ├── IndianCurrencyFormatterTest.kt
│           │   ├── MonthlySpendCalculatorTest.kt
│           │   ├── NextDueDateCalculatorTest.kt
│           │   └── RecentSubscriptionsSelectorTest.kt
│           └── ui/home/HomeViewModelTest.kt
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Getting Started & Setup

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2.1+) or Koala.
- **Java Development Kit (JDK)**: JDK 21 (bundled with Android Studio as JetBrains Runtime `jbr-21`).
- **Android SDK**: Android 15 SDK (`platforms/android-35`), Build-Tools `35.0.0`.
- **Target Device**: Any Android device or emulator running **Android 8.0 (API level 26) or higher**.

### Open in Android Studio
1. Launch Android Studio.
2. Select **Open** and choose the `loop-in` directory:
   ```bash
   /path/to/loop-in
   ```
3. Android Studio will automatically invoke Gradle sync using the Gradle Wrapper (`gradle-8.9`).
4. Wait for Gradle Sync and KSP indexing to finish.

### Run on Android Emulator
1. Open the **Device Manager** in Android Studio (`Tools > Device Manager`).
2. Create or select an AVD running **API 34 or 35** (e.g., Pixel 8 Pro).
3. Click the green **Run** button (`Shift + F10`) or choose `Run 'app'`.

### Run on a Physical Android Device
1. On your Android phone, enable **Developer Options** (tap `Build Number` 7 times in `Settings > About Phone`).
2. Turn on **USB Debugging**.
3. Connect your device via USB cable and authorize your computer.
4. Select your device from the deployment target dropdown in Android Studio and press **Run**.

### Run Unit Tests
All business logic, currency formatting, spend calculators, and ViewModels are covered by unit tests.
To run the full test suite from your terminal:

```bash
# Set JAVA_HOME to JDK 21
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Execute all debug unit tests
./gradlew testDebugUnitTest
```

The HTML test report is automatically generated at:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Build Debug APK
To assemble a standalone debug APK for sideloading or sharing:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

To install directly onto a connected device via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Design Decisions & Trade-offs

1. **Local-Only vs. Cloud Sync**:
   - *Decision*: Zero network permissions (`android.permission.INTERNET` omitted entirely).
   - *Trade-off*: Multi-device sync is not supported without manual database export/import, but user privacy is 100% physically guaranteed.
2. **Deterministic Pre-Debit Signals vs. SMS Scraping**:
   - *Decision*: Passive notification listening + manual entry presets.
   - *Trade-off*: Avoids requesting Google Play's heavily restricted `READ_SMS` permission, adhering to Play Store policy and user trust.
3. **Jetpack Compose Single-Activity Architecture**:
   - *Decision*: Entire application rendered in Compose with `NavHost`.
   - *Trade-off*: Eliminates Fragment lifecycle overhead; enables fluid shared physics transitions and directional navigation.
4. **Minor Units for Financial Calculations**:
   - *Decision*: All amounts are stored as `Long` in minor currency units (paise: ₹1 = 100 paise).
   - *Trade-off*: Completely prevents floating-point rounding errors common with `Float` or `Double`.

---

## License

```
MIT License

Copyright (c) 2026 Anurag Verma

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
