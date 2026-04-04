# HyperIsland Custom Preview

> HyperOS 3 Super Island customization Xposed module — Preview build

[![Android](https://img.shields.io/badge/Android-15+-green?logo=android)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30+-blue)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100+-orange)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&label=Preview)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

---

## Features

- **Super Island Customization** - Fully customize HyperOS 3 Super Island content, colors, timing, progress bars, timers, and buttons
- **20+ Scenario Presets** - Navigation, Taxi, Food delivery, Music, Payment, Call, Timer, Download, and more
- **Template System** - Save and apply custom island configurations as reusable templates
- **App Whitelist** - Control which apps can trigger the Super Island
- **Package Spoofing** - Emulate different app identities for island display
- **Config Backup/Restore** - Export and import all settings as JSON

## Triple-Theme Support

| Miuix (HyperOS) | Material Design 3 | Android Liquid Glass |
|:---:|:---:|:---:|
| Native HyperOS design language | Google Material You with dynamic color | Real-time backdrop blur & glass effects |

- Dark / Light / Follow System mode
- Independent dock bar style selection (Miuix / MD3 / Liquid Glass)
- Swipeable tab navigation (HorizontalPager)

## Screenshots

> TODO: Add screenshots here

<!-- Uncomment and add screenshot paths:
| Home | Island Control | Settings |
|:---:|:---:|:---:|
| ![Home](screenshots/home.png) | ![Island](screenshots/island.png) | ![Settings](screenshots/settings.png) |
-->

## Requirements

- Android 11+ (API 30+)
- HyperOS 3 (Xiaomi devices)
- LSPosed or compatible Xposed framework
- Root access (KernelSU / Magisk / APatch)

## Installation

1. Install [LSPosed](https://github.com/LSPosed/LSPosed) (or LSPosed Fork)
2. Download the latest APK from [Releases](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)
3. Install the APK
4. Enable the module in LSPosed, check **System UI** in scope
5. Reboot or restart System UI

## Build from Source

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/java/com/kangqi/hIc/
├── MainActivity.kt              # Single-activity entry, HorizontalPager navigation
├── log/                          # Runtime logging system
├── model/                        # Data classes (IslandConfig, scenarios, templates)
├── service/                      # IslandManager (broadcast to SystemUI)
├── ui/
│   ├── components/               # Reusable UI components
│   │   ├── HyperPage.kt         # Unified page template (OShin-inspired)
│   │   ├── HyperComponents.kt   # Theme-aware component wrappers
│   │   ├── GlassCard.kt         # Glass morphism card containers
│   │   └── LiquidGlassNavBar.kt # Floating glass navigation bar
│   ├── screens/                  # 6 screen composables
│   └── theme/                    # ThemeManager, Color tokens, Triple-theme system
├── utils/                        # Config backup, device info helpers
└── xposed/                       # Xposed hook entry point
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Design Systems**: Miuix (HyperOS) + Android Liquid Glass (Kyant0/AndroidLiquidGlass)
- **Framework**: Xposed API 82 (LSPosed compatible)
- **Storage**: DataStore Preferences + SharedPreferences
- **Build**: Gradle Kotlin DSL, Compose BOM 2025.05.01

## Changelog

### v1.0.0-preview (Initial Release)

- HyperOS 3 Super Island full customization
- Triple-theme system (Miuix / MD3 / Liquid Glass)
- Dark/Light mode adaptation
- HorizontalPager swipe navigation
- Unified HyperPage layout system
- 20+ scenario presets with package spoofing
- Template save/apply/delete
- App whitelist management
- Config import/export (JSON)
- Real-time log viewer with LSPosed log sync
- Floating Liquid Glass navigation bar

## Credits

- [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) - HyperOS design language components
- [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) - Backdrop blur effects
- [LSPosed](https://github.com/LSPosed/LSPosed) - Xposed framework
- [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) - Reference implementation
- [SukiSU-Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra) - UI pattern reference
- [OShin](https://github.com/suqi8/OShin) - Layout architecture reference
- [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) - Color picker

## License

```
MIT License

Copyright (c) 2026 KangQi

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
