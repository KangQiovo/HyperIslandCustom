<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 Super Island Customization Xposed Module — Preview Build ✨**

🌐 [简体中文](README_zh-CN.md) | [日本語](README_ja.md) | **English**

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
- [📸 Screenshots](#-screenshots)
- [📦 Requirements](#-requirements)
- [🔧 Installation](#-installation)
- [🏗️ Build from Source](#️-build-from-source)
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

## 📸 Screenshots

> 🖼️ Screenshots coming soon...

<!-- Uncomment when screenshots are added:
<div align="center">

| Home | Island Control | Settings |
|:---:|:---:|:---:|
| ![Home](screenshots/home.png) | ![Island](screenshots/island.png) | ![Settings](screenshots/settings.png) |

| Templates | Log Viewer | Credits |
|:---:|:---:|:---:|
| ![Templates](screenshots/templates.png) | ![Logs](screenshots/logs.png) | ![Credits](screenshots/credits.png) |

</div>
-->

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

## 📂 Project Structure

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # Single-activity, HorizontalPager navigation
├── 📝 log/                          # Runtime logging system
├── 📦 model/                        # Data classes (IslandConfig, scenarios, templates)
├── ⚙️ service/                      # IslandManager (broadcast to SystemUI)
├── 🎨 ui/
│   ├── components/                  # Reusable UI components
│   │   ├── HyperPage.kt            # Unified page template
│   │   ├── HyperComponents.kt      # Theme-aware component wrappers
│   │   ├── GlassCard.kt            # Glass morphism card containers
│   │   └── LiquidGlassNavBar.kt    # Floating glass navigation bar
│   ├── screens/                     # 6 screen composables
│   └── theme/                       # ThemeManager, Color tokens, Triple-theme
├── 🔧 utils/                        # Config backup, device info helpers
└── 🪝 xposed/                       # Xposed hook entry point
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

---

## 📝 Changelog

### 🏷️ v1.0.0-preview — Initial Release

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

Made with ❤️ by [KangQi](https://github.com/KangQiovo)

</div>
