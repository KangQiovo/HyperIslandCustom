<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 超級島自訂 Xposed 模組 — 預覽版 ✨**

🌐 [简体中文](README.md) | **繁體中文** | [English](README_en.md) | [日本語](README_ja.md) | [한국어](README_ko.md) | [ไทย](README_th.md) | [العربية](README_ar.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=預覽版&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 完全掌控 HyperOS 3 超級島 — 自訂內容、顏色、動畫等一切。

</div>

---

## 📋 目錄

- [✨ 功能特性](#-功能特性)
- [🎨 三大主題支援](#-三大主題支援)
- [📦 系統需求](#-系統需求)
- [🔧 安裝方法](#-安裝方法)
- [🏗️ 從原始碼建置](#️-從原始碼建置)
- [🧠 實現原理](#-實現原理)
- [📂 專案結構](#-專案結構)
- [🛠️ 技術棧](#️-技術棧)
- [📝 更新日誌](#-更新日誌)
- [🙏 致謝](#-致謝)
- [📄 開源授權](#-開源授權)
- [📬 聯絡方式](#-聯絡方式)

---

## ✨ 功能特性

| 功能 | 描述 |
|------|------|
| 🏝️ **超級島自訂** | 完全自訂內容、顏色、時長、進度條、計時器和按鈕 |
| 🎭 **20+ 場景預設** | 導航、叫車、外送、音樂、支付、通話、計時器、下載等 |
| 📋 **範本系統** | 儲存和套用自訂超級島設定為可重複使用的範本 |
| 📱 **應用程式白名單** | 控制哪些應用程式可以觸發超級島 |
| 🔄 **套件名稱偽裝** | 模擬不同應用程式身份來顯示超級島 |
| 💾 **設定備份/還原** | 以 JSON 格式匯出和匯入所有設定 |
| 📊 **即時日誌檢視器** | 搜尋、篩選模組日誌，同步 LSPosed 日誌 |
| 🖌️ **三大主題系統** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **深色/淺色模式** | 跟隨系統 / 淺色 / 深色模式適配 |
| 👆 **滑動導覽** | HorizontalPager 實現流暢的標籤頁切換 |

---

## 🎨 三大主題支援

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| 原生 HyperOS 設計語言 | Google Material You + 動態取色 | 即時背景模糊 & 玻璃效果 |

</div>

- 🌗 **深色 / 淺色 / 跟隨系統** — 三種顯示模式
- 🎛️ **獨立 Dock 列樣式** — 底列主題可獨立選擇（Miuix / MD3 / Liquid Glass）
- 👆 **滑動切換標籤頁** — HorizontalPager 流暢的頁面導覽
- 🫧 **Glass 透明度調節** — 自訂玻璃效果透明度

---

## 📦 系統需求

| 需求 | 版本 |
|------|------|
| 📱 Android | 11+（API 30+） |
| 🔷 HyperOS | 3.x（小米 / Redmi / POCO） |
| ⚡ Xposed 框架 | LSPosed / LSPosed Fork |
| 🔓 Root 權限 | KernelSU / Magisk / APatch |

---

## 🔧 安裝方法

1. ⚡ 安裝 [LSPosed](https://github.com/LSPosed/LSPosed)（或 LSPosed Fork）
2. 📥 從 [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases) 下載最新 APK
3. 📲 安裝 APK
4. ✅ 在 LSPosed 中啟用模組 → 勾選 **系統介面 (System UI)** 作用域
5. 🔄 重新啟動或重新啟動系統介面

---

## 🏗️ 從原始碼建置

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

📦 APK 輸出路徑：`app/build/outputs/apk/debug/app-debug.apk`

---

## 🧠 實現原理

### 🪝 Xposed Hook 攔截機制

模組同時運行在**兩個程序**中：

```
┌─────────────────────┐         廣播通訊           ┌─────────────────────┐
│   📱 應用程式程序     │  ───────────────────────▶  │  🖥️ SystemUI 程序    │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ 建構Intent       │                            │  ├─ 廣播接收器       │
│  ├─ 載入Bitmap       │                            │  ├─ 建構JSON        │
│  └─ 發送廣播         │                            │  ├─ 套件名稱偽裝     │
│                     │                            │  └─ 發送通知        │
└─────────────────────┘                            └─────────────────────┘
```

**核心流程：**

1. **自 Hook** — 模組透過 `XC_MethodReplacement.returnConstant(true)` Hook 自身的 `isModuleActive()` 方法來回報啟用狀態。

2. **注入 SystemUI** — Hook `SystemUIApplication.onCreate()` 在 SystemUI 程序中註冊自訂 `BroadcastReceiver`，監聽 `ACTION_SHOW_ISLAND` / `ACTION_DISMISS_ISLAND`。

3. **設定 → JSON 對映** — 當 `IslandManager.showIsland(config)` 被呼叫時，`IslandConfig` 中的 40+ 欄位被封裝到 `Intent` 中並廣播。在 SystemUI 側，接收器建構符合 HyperOS 3 內部 API 的 `miui.focus.param` JSON 結構。

4. **套件名稱偽裝** — 為了讓超級島以正確的內建範本呈現（如導航使用高德地圖範本），模組攔截：
   - `StatusBarNotification.getPackageName()` → 回傳偽裝套件名稱
   - `StatusBarNotification.getUid()` → 回傳偽裝應用程式的真實 UID
   - `Notification.getSmallIcon()` → 載入偽裝應用程式的真實圖示

5. **通知發佈** — 攜帶建構好的 JSON 的系統通知透過 `NotificationManager.notify()` 發佈，HyperOS 使用其原生範本引擎呈現超級島。

---

### 🎨 三主題架構

主題系統使用 **CompositionLocal** 實現整個 UI 樹的零傳參存取：

**ThemeManager**（基於 DataStore 的狀態機）：
| 屬性 | 選項 | 預設值 |
|------|------|--------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |
| `glassBarAlpha` | 0.0 ~ 1.0 | 0.4 |

15+ 控制元件自動適配當前主題：`HyperSwitch`、`HyperSlider`、`HyperButton`、`HyperTextField`、`HyperDropdown`、`HyperFilterChip` 等。

---

### 🫧 玻璃擬態呈現

使用 [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) Backdrop API 的三層玻璃呈現：

| 層級 | 效果 | 實現方式 |
|------|------|----------|
| 🪟 **背景層** | 即時模糊 + 活力增強 | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **著色層** | 半透明覆蓋 | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **邊框層** | 漸層邊緣高光 | `Brush.verticalGradient(White@0.12 → White@0.04)` |

---

### 📐 統一 HyperPage 佈局系統

借鑑 [OShin](https://github.com/suqi8/OShin) 的 FunPage 模式，消除各頁面的重複樣板程式碼：

```kotlin
HyperPage(title = "設定", onBack = { nav.pop() }) {
    // 內容 — 自動獲得主題化頂列、狀態列間距、返回按鈕
}
```

---

### 👆 HorizontalPager 滑動導覽

滑動手勢與底部導覽的雙向同步架構。4 個主頁面透過 `HorizontalPager` 實現，2 個次要頁面（日誌、引用）透過 `AnimatedContent` 淡入淡出覆蓋顯示。

---

## 📂 專案結構

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # 單 Activity 入口，HorizontalPager 導覽
├── 📝 log/                          # 執行時日誌系統
├── 📦 model/                        # 資料類別
├── ⚙️ service/
│   └── IslandManager.kt            # 廣播橋接（應用程式 → SystemUI）
├── 🎨 ui/
│   ├── components/                  # HyperPage、HyperComponents、GlassCard 等
│   ├── screens/                     # 6 個頁面 Composable
│   └── theme/                       # ThemeManager、色彩令牌、三主題
├── 🔧 utils/                        # 設定備份、裝置資訊輔助
└── 🪝 xposed/
    └── MainHook.kt                  # Xposed Hook 入口
```

---

## 🛠️ 技術棧

| 類別 | 技術 |
|------|------|
| 💻 程式語言 | Kotlin |
| 🎨 UI 框架 | Jetpack Compose + Material Design 3 |
| 🎭 設計系統 | Miuix (HyperOS) + Android Liquid Glass |
| ⚡ Hook 框架 | Xposed API 82（相容 LSPosed） |
| 💾 資料儲存 | DataStore Preferences + SharedPreferences |
| 🔨 建置系統 | Gradle Kotlin DSL，Compose BOM 2025.05.01 |
| 🖼️ 玻璃效果 | Kyant0/AndroidLiquidGlass Backdrop API |
| 🎨 顏色選擇器 | skydoves/colorpicker-compose |

---

## 📝 更新日誌

### 🏷️ Preview0.18 — 首次發佈

- 🏝️ HyperOS 3 超級島完整自訂
- 🎨 三大主題系統（Miuix / MD3 / Liquid Glass）
- 🌗 深色/淺色模式適配
- 👆 HorizontalPager 滑動導覽
- 📐 統一 HyperPage 佈局系統
- 🎭 20+ 場景預設與套件名稱偽裝
- 📋 範本儲存/套用/刪除
- 📱 應用程式白名單管理
- 💾 設定匯入/匯出（JSON）
- 📊 即時日誌檢視器 + LSPosed 日誌同步
- 🫧 懸浮 Liquid Glass 導覽列

---

## 🙏 致謝

| 專案 | 作者 | 描述 |
|------|------|------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | HyperOS 設計語言元件 |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | 背景模糊效果庫 |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | Xposed 框架 |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | 參考實現 |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | 佈局架構參考 |

---

## 📄 開源授權

```
MIT License — Copyright (c) 2026 KangQi
```

查看 [LICENSE](LICENSE) 獲取完整授權文字。

---

<div align="center">

**⭐ 如果覺得有用，請給專案按個 Star！⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
