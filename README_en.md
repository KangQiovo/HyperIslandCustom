<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 Super Island Customization Xposed Module — Preview Build ✨**

🌐 [简体中文](README.md) | [繁體中文](README_zh-TW.md) | **English** | [日本語](README_ja.md) | [한국어](README_ko.md) | [ไทย](README_th.md) | [العربية](README_ar.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=Preview&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 Take full control of the HyperOS 3 Super Island — customize content, colors, animations, and more.

</div>

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🎨 Triple-Theme Support](#-triple-theme-support)
- [📦 Requirements](#-requirements)
- [🔧 Installation](#-installation)
- [🏗️ Build from Source](#️-build-from-source)
- [🧠 Implementation Principles](#-implementation-principles)
- [📂 Project Structure](#-project-structure)
- [🛠️ Tech Stack](#️-tech-stack)
- [📝 Changelog](#-changelog)
- [🙏 Credits](#-credits)
- [📄 License](#-license)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🏝️ **Super Island Customization** | Fully customize content, colors, timing, progress bars, timers, and buttons |
| 🎭 **20+ Scenario Presets** | Navigation, Taxi, Food delivery, Music, Payment, Call, Timer, Download... |
| 📋 **Template System** | Save and apply custom island configurations as reusable templates |
| 📱 **App Whitelist** | Control which apps can trigger the Super Island |
| 🔄 **Package Spoofing** | Emulate different app identities for island display |
| 💾 **Config Backup/Restore** | Export and import all settings as JSON |
| 📊 **Real-time Log Viewer** | Monitor module logs with search, filter, and LSPosed log sync |
| 🖌️ **Triple-Theme System** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **Dark/Light Mode** | Follow System / Light / Dark mode adaptation |
| 👆 **Swipe Navigation** | HorizontalPager for smooth tab switching |

---

## 🎨 Triple-Theme Support

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| Native HyperOS design | Google Material You + dynamic color | Real-time backdrop blur & glass effects |

</div>

- 🌗 **Dark / Light / Follow System** — Three display modes
- 🎛️ **Independent Dock Style** — Mix and match dock bar theme (Miuix / MD3 / Liquid Glass)
- 👆 **Swipeable Tabs** — Smooth HorizontalPager navigation between pages
- 🫧 **Glass Opacity Control** — Adjust glass effect transparency

---

## 📦 Requirements

| Requirement | Version |
|-------------|---------|
| 📱 Android | 11+ (API 30+) |
| 🔷 HyperOS | 3.x (Xiaomi / Redmi / POCO) |
| ⚡ Xposed Framework | LSPosed / LSPosed Fork |
| 🔓 Root Access | KernelSU / Magisk / APatch |

---

## 🔧 Installation

1. ⚡ Install [LSPosed](https://github.com/LSPosed/LSPosed) (or LSPosed Fork)
2. 📥 Download the latest APK from [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)
3. 📲 Install the APK
4. ✅ Enable the module in LSPosed → check **System UI** in scope
5. 🔄 Reboot or restart System UI

---

## 🏗️ Build from Source

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

📦 APK output: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🧠 Implementation Principles

### 🪝 Xposed Hook & Interception

The module operates across **two processes** simultaneously:

```
┌─────────────────────┐         Broadcast          ┌─────────────────────┐
│   📱 App Process     │  ───────────────────────▶  │  🖥️ SystemUI Process │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ buildIntent()   │                            │  ├─ BroadcastReceiver│
│  ├─ loadBitmap()    │                            │  ├─ buildJSON()     │
│  └─ sendBroadcast() │                            │  ├─ spoofPackage()  │
│                     │                            │  └─ nm.notify()     │
└─────────────────────┘                            └─────────────────────┘
```

**Step-by-step flow:**

1. **Self-Hook** — Hooks its own `isModuleActive()` via `XC_MethodReplacement.returnConstant(true)` to report activation status.

2. **SystemUI Injection** — Hooks `SystemUIApplication.onCreate()` to register a custom `BroadcastReceiver` listening for `ACTION_SHOW_ISLAND` / `ACTION_DISMISS_ISLAND`.

3. **Config → JSON Mapping** — When `IslandManager.showIsland(config)` is called, 40+ fields from `IslandConfig` are packed into an `Intent` and broadcast. The SystemUI receiver builds a `miui.focus.param` JSON matching HyperOS 3's internal API:

```json
{
  "param_v2": {
    "templateType": 14,
    "business": "navigation",
    "baseInfo": { "title": "...", "content": "..." },
    "param_island": {
      "bigIslandArea": {
        "imageTextInfoLeft": { ... },
        "progressArea": { ... },
        "buttonArea": { ... }
      },
      "smallIslandArea": { "type": 1 }
    }
  }
}
```

4. **Package Spoofing** — Intercepts multiple methods to disguise notification source:
   - `StatusBarNotification.getPackageName()` → returns spoofed package
   - `StatusBarNotification.getUid()` → returns spoofed app's real UID
   - `Notification.getSmallIcon()` → loads the spoofed app's actual icon

5. **Notification Posting** — A system notification with the crafted JSON is posted via `NotificationManager.notify()`, and HyperOS renders the Super Island using its native template engine.

---

### 🎨 Triple-Theme Architecture

The theme system uses **CompositionLocal** for zero-prop-drilling access across the entire UI tree:

```
HyperIslandTheme(themeManager, backdrop)
  ├─ Determine isDark (respects darkMode override)
  ├─ Set transparent status/nav bars
  ├─ Build colorScheme
  │  ├─ MD3 → dynamicLightColorScheme / dynamicDarkColorScheme
  │  └─ Others → static fallback palette
  ├─ Wrap with MiuixTheme (for Miuix components)
  ├─ Wrap with MaterialTheme (for MD3 components)
  └─ Provide LocalIsDark, LocalBackdrop, LocalThemeManager
```

**ThemeManager** (DataStore-backed state machine):
| Property | Options | Default |
|----------|---------|---------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |
| `glassBarAlpha` | 0.0 ~ 1.0 | 0.4 |

**Theme-Agnostic Components** (`HyperComponents.kt`):

Every UI control adapts to the active theme automatically:

```kotlin
@Composable
fun HyperSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    when (themeManager.appTheme) {
        MIUIX        → MiuixSwitch(...)
        LIQUID_GLASS → LiquidGlassToggle(...)
        else         → Switch(...)                  // Material3
    }
}
```

15+ controls follow this pattern: `HyperSwitch`, `HyperSlider`, `HyperButton`, `HyperTextField`, `HyperDropdown`, `HyperFilterChip`, etc.

---

### 🫧 Glass Morphism Rendering

Three-layer glass rendering using [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) Backdrop API:

| Layer | Effect | Implementation |
|-------|--------|----------------|
| 🪟 **Backdrop** | Real-time blur + vibrancy | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **Tint** | Semi-transparent overlay | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **Border** | Gradient edge highlight | `Brush.verticalGradient(White@0.12 → White@0.04)` |

**Interactive Highlight** (Android 13+ RuntimeShader / AGSL):
```glsl
half4 main(float2 coord) {
    float dist = distance(coord, position);
    float intensity = smoothstep(radius, radius * 0.5, dist);
    return color * intensity;    // Circular light following touch
}
```

---

### 🎢 Physics-Based Animation

**DampedDragAnimation** — Liquid deformation system for the glass navbar slider:

| Stream | Stiffness | Damping | Purpose |
|--------|-----------|---------|---------|
| Value | 1.0 | 1000 | Tab position |
| Velocity | 0.5 | 300 | Drag speed |
| Press | 1.0 | 1000 | Touch pressure |
| ScaleXY | 0.6/0.7 | 250 | Squash/stretch |

On fast drag release, velocity affects shape deformation — creating a **liquid squash & stretch** effect matching real-world physics.

---

### 📐 Unified HyperPage Layout

Inspired by [OShin](https://github.com/suqi8/OShin)'s FunPage pattern:

```kotlin
HyperPage(title = "Settings", onBack = { nav.pop() }) {
    // Content — automatically gets themed top bar, status bar insets, back button
}
```

| Condition | Behavior |
|-----------|----------|
| `onBack != null` | Secondary page → themed top bar with back button |
| `onBack == null` | Main page → scaffold handles title |
| Theme = MIUIX | Native `MiuixTopAppBar` |
| Theme = MD3 | Material3 `TopAppBar` |
| Theme = LIQUID_GLASS | Custom `Row` with glass styling |

---

### 👆 HorizontalPager Navigation

Dual-sync architecture between swipe gesture and bottom navigation:

```kotlin
// Sync 1: Swipe → updates selected route
snapshotFlow { pagerState.currentPage }.collectLatest { page ->
    selectedRoute = navItems[page].route
}

// Sync 2: Nav tap → animates pager
pagerState.animateScrollToPage(targetPage)
```

4 main pages via `HorizontalPager`, 2 secondary pages via `AnimatedContent` overlay with fade transitions.

---

## 📂 Project Structure

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # Single-activity, HorizontalPager navigation
├── 📝 log/                          # Runtime logging system
├── 📦 model/                        # Data classes (IslandConfig, scenarios, templates)
├── ⚙️ service/
│   └── IslandManager.kt            # Broadcast bridge (App → SystemUI)
├── 🎨 ui/
│   ├── components/                  # HyperPage, HyperComponents, GlassCard, etc.
│   ├── screens/                     # 6 screen composables
│   └── theme/                       # ThemeManager, Color tokens, Triple-theme
├── 🔧 utils/                        # Config backup, device info helpers
└── 🪝 xposed/
    └── MainHook.kt                  # Xposed hook entry (self-hook + SystemUI hook)
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| 💻 Language | Kotlin |
| 🎨 UI Framework | Jetpack Compose + Material Design 3 |
| 🎭 Design Systems | Miuix (HyperOS) + Android Liquid Glass |
| ⚡ Hook Framework | Xposed API 82 (LSPosed compatible) |
| 💾 Storage | DataStore Preferences + SharedPreferences |
| 🔨 Build System | Gradle Kotlin DSL, Compose BOM 2025.05.01 |
| 🖼️ Glass Effects | Kyant0/AndroidLiquidGlass Backdrop API |
| 🎨 Color Picker | skydoves/colorpicker-compose |

---

## 📝 Changelog

### 🏷️ Preview0.18 — Initial Release

- 🏝️ HyperOS 3 Super Island full customization
- 🎨 Triple-theme system (Miuix / MD3 / Liquid Glass)
- 🌗 Dark/Light mode adaptation
- 👆 HorizontalPager swipe navigation
- 📐 Unified HyperPage layout system
- 🎭 20+ scenario presets with package spoofing
- 📋 Template save/apply/delete
- 📱 App whitelist management
- 💾 Config import/export (JSON)
- 📊 Real-time log viewer with LSPosed log sync
- 🫧 Floating Liquid Glass navigation bar
- 🪝 Two-process Xposed hook architecture
- 🎢 Physics-based glass navbar animations
- ✨ AGSL RuntimeShader interactive highlights

---

## 🙏 Credits

| Project | Author | Description |
|---------|--------|-------------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | HyperOS design language components |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | Backdrop blur effects library |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | Xposed framework |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | Reference implementation |
| [SukiSU-Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra) | SukiSU Team | UI pattern reference |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | Layout architecture reference |
| [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) | skydoves | Color picker |

---

## 📄 License

```
MIT License — Copyright (c) 2026 KangQi
```

See [LICENSE](LICENSE) for full text.

---

<div align="center">

**⭐ Star this project if you find it useful! ⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
