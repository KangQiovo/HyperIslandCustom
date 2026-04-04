<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 超级岛自定义 Xposed 模块 — 预览版 ✨**

🌐 **简体中文** | [繁體中文](README_zh-TW.md) | [English](README_en.md) | [日本語](README_ja.md) | [한국어](README_ko.md) | [ไทย](README_th.md) | [العربية](README_ar.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=预览版&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 完全掌控 HyperOS 3 超级岛 — 自定义内容、颜色、动画等一切。

</div>

---

## 📋 目录

- [✨ 功能特性](#-功能特性)
- [🎨 三大主题支持](#-三大主题支持)
- [📸 截图预览](#-截图预览)
- [📦 系统要求](#-系统要求)
- [🔧 安装方法](#-安装方法)
- [🏗️ 从源码构建](#️-从源码构建)
- [🧠 实现原理](#-实现原理)
- [📂 项目结构](#-项目结构)
- [🛠️ 技术栈](#️-技术栈)
- [📝 更新日志](#-更新日志)
- [🙏 致谢](#-致谢)
- [📄 开源协议](#-开源协议)

---

## ✨ 功能特性

| 功能 | 描述 |
|------|------|
| 🏝️ **超级岛自定义** | 完全自定义内容、颜色、时长、进度条、计时器和按钮 |
| 🎭 **20+ 场景预设** | 导航、打车、外卖、音乐、支付、通话、计时器、下载等 |
| 📋 **模板系统** | 保存和应用自定义超级岛配置为可复用模板 |
| 📱 **应用白名单** | 控制哪些应用可以触发超级岛 |
| 🔄 **包名伪装** | 模拟不同应用身份来显示超级岛 |
| 💾 **配置备份/恢复** | 以 JSON 格式导出和导入所有设置 |
| 📊 **实时日志查看器** | 搜索、过滤模块日志，同步 LSPosed 日志 |
| 🖌️ **三大主题系统** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **深色/浅色模式** | 跟随系统 / 浅色 / 深色模式适配 |
| 👆 **滑动导航** | HorizontalPager 实现流畅的标签页切换 |

---

## 🎨 三大主题支持

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| 原生 HyperOS 设计语言 | Google Material You + 动态取色 | 实时背景模糊 & 玻璃效果 |

</div>

- 🌗 **深色 / 浅色 / 跟随系统** — 三种显示模式
- 🎛️ **独立 Dock 栏样式** — 底栏主题可独立选择（Miuix / MD3 / Liquid Glass）
- 👆 **滑动切换标签页** — HorizontalPager 流畅的页面导航
- 🫧 **Glass 透明度调节** — 自定义玻璃效果透明度

---

## 📸 截图预览

> 🖼️ 截图即将发布...

<!-- 添加截图后取消注释：
<div align="center">

| 主页 | 超级岛控制 | 设置 |
|:---:|:---:|:---:|
| ![主页](screenshots/home.png) | ![超级岛](screenshots/island.png) | ![设置](screenshots/settings.png) |

| 模板 | 日志查看器 | 引用项目 |
|:---:|:---:|:---:|
| ![模板](screenshots/templates.png) | ![日志](screenshots/logs.png) | ![引用](screenshots/credits.png) |

</div>
-->

---

## 📦 系统要求

| 要求 | 版本 |
|------|------|
| 📱 Android | 11+（API 30+） |
| 🔷 HyperOS | 3.x（小米 / Redmi / POCO） |
| ⚡ Xposed 框架 | LSPosed / LSPosed Fork |
| 🔓 Root 权限 | KernelSU / Magisk / APatch |

---

## 🔧 安装方法

1. ⚡ 安装 [LSPosed](https://github.com/LSPosed/LSPosed)（或 LSPosed Fork）
2. 📥 从 [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases) 下载最新 APK
3. 📲 安装 APK
4. ✅ 在 LSPosed 中启用模块 → 勾选 **系统界面 (System UI)** 作用域
5. 🔄 重启或重启系统界面

---

## 🏗️ 从源码构建

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

📦 APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

---

## 🧠 实现原理

### 🪝 Xposed Hook 拦截机制

模块同时运行在**两个进程**中：

```
┌─────────────────────┐         广播通信           ┌─────────────────────┐
│   📱 应用进程         │  ───────────────────────▶  │  🖥️ SystemUI 进程    │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ 构建Intent       │                            │  ├─ 广播接收器       │
│  ├─ 加载Bitmap       │                            │  ├─ 构建JSON        │
│  └─ 发送广播         │                            │  ├─ 包名伪装        │
│                     │                            │  └─ 发送通知        │
└─────────────────────┘                            └─────────────────────┘
```

**核心流程：**

1. **自Hook** — 模块通过 `XC_MethodReplacement.returnConstant(true)` Hook 自身的 `isModuleActive()` 方法来上报激活状态。

2. **注入 SystemUI** — Hook `SystemUIApplication.onCreate()` 在 SystemUI 进程中注册自定义 `BroadcastReceiver`，监听 `ACTION_SHOW_ISLAND` / `ACTION_DISMISS_ISLAND`。

3. **配置 → JSON 映射** — 当 `IslandManager.showIsland(config)` 被调用时，`IslandConfig` 中的 40+ 字段被打包到 `Intent` 中并广播。在 SystemUI 侧，接收器构建符合 HyperOS 3 内部 API 的 `miui.focus.param` JSON 结构：

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

4. **包名伪装** — 为了让超级岛以正确的内置模板渲染（如导航使用高德地图模板），模块拦截以下方法：
   - `StatusBarNotification.getPackageName()` → 返回伪装包名
   - `StatusBarNotification.getUid()` → 返回伪装应用的真实 UID
   - `Notification.getSmallIcon()` → 加载伪装应用的真实图标

5. **通知发布** — 携带构建好的 JSON 的系统通知通过 `NotificationManager.notify()` 发布，HyperOS 使用其原生模板引擎渲染超级岛。

---

### 🎨 三主题架构

主题系统使用 **CompositionLocal** 实现整个 UI 树的零传参访问：

```
HyperIslandTheme(themeManager, backdrop)
  ├─ 判断 isDark（尊重 darkMode 覆盖设置）
  ├─ 设置透明状态栏/导航栏
  ├─ 构建 colorScheme
  │  ├─ MD3 → dynamicLightColorScheme / dynamicDarkColorScheme
  │  └─ 其他 → 静态备用配色方案
  ├─ 包裹 MiuixTheme（用于 Miuix 组件）
  ├─ 包裹 MaterialTheme（用于 MD3 组件）
  └─ 提供 LocalIsDark, LocalBackdrop, LocalThemeManager
```

**ThemeManager**（基于 DataStore 的状态机）：
| 属性 | 选项 | 默认值 |
|------|------|--------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |
| `glassBarAlpha` | 0.0 ~ 1.0 | 0.4 |

**主题自适应组件**（`HyperComponents.kt`）：

每个 UI 控件自动适配当前主题：

```kotlin
@Composable
fun HyperSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    when (themeManager.appTheme) {
        MIUIX        → MiuixSwitch(...)         // 原生 HyperOS 开关
        LIQUID_GLASS → LiquidGlassToggle(...)   // 自定义玻璃开关
        else         → Switch(...)               // Material3 开关
    }
}
```

15+ 控件均遵循此模式：`HyperSwitch`、`HyperSlider`、`HyperButton`、`HyperTextField`、`HyperDropdown`、`HyperFilterChip` 等。

---

### 🫧 玻璃拟态渲染

使用 [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) Backdrop API 的三层玻璃渲染：

| 层级 | 效果 | 实现方式 |
|------|------|----------|
| 🪟 **背景层** | 实时模糊 + 活力增强 | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **着色层** | 半透明覆盖 | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **边框层** | 渐变边缘高光 | `Brush.verticalGradient(White@0.12 → White@0.04)` |

**LiquidGlassNavBar** — 悬浮底部导航栏的 3 个子层：
1. 可见标签行：backdrop 模糊 + 镜片效果
2. 不可见着色层：accent 颜色的 SrcAtop 混合模式（产生色彩渗透效果）
3. 玻璃滑块指示器：基于速度的变形动画

**交互高光**（Android 13+ RuntimeShader / AGSL）：
```glsl
half4 main(float2 coord) {
    float dist = distance(coord, position);
    float intensity = smoothstep(radius, radius * 0.5, dist);
    return color * intensity;    // 跟随触摸的圆形光效
}
```

---

### 🎢 基于物理的动画系统

**DampedDragAnimation** — 玻璃导航栏滑块的液态变形系统：

| 动画流 | 刚度 | 阻尼 | 用途 |
|--------|------|------|------|
| Value | 1.0 | 1000 | 标签位置 |
| Velocity | 0.5 | 300 | 拖动速度 |
| Press | 1.0 | 1000 | 触摸压力 |
| ScaleXY | 0.6/0.7 | 250 | 挤压/拉伸 |

快速拖动释放时，速度影响形状变形：
```
scaleX /= 1 - velocity×0.75    → 水平方向拉伸
scaleY *= 1 - velocity×0.25    → 垂直方向压缩
```
呈现出符合真实物理效果的**液态挤压拉伸**动画。

---

### 📐 统一 HyperPage 布局系统

借鉴 [OShin](https://github.com/suqi8/OShin) 的 FunPage 模式，消除各页面的重复样板代码：

```kotlin
HyperPage(title = "设置", onBack = { nav.pop() }) {
    // 内容 — 自动获得主题化顶栏、状态栏间距、返回按钮
}
```

| 条件 | 行为 |
|------|------|
| `onBack != null` | 二级页面 → 带返回按钮的主题化顶栏 + 状态栏内边距 |
| `onBack == null` | 主页面 → 由 Scaffold 处理标题 |
| 主题 = MIUIX | 原生 `MiuixTopAppBar` |
| 主题 = MD3 | Material3 `TopAppBar` |
| 主题 = LIQUID_GLASS | 自定义 `Row` + 玻璃样式 |

---

### 👆 HorizontalPager 滑动导航

滑动手势与底部导航的双向同步架构：

```kotlin
// 同步1：滑动 → 更新选中路由
snapshotFlow { pagerState.currentPage }.collectLatest { page ->
    selectedRoute = navItems[page].route
}

// 同步2：点击导航 → 触发翻页动画
pagerState.animateScrollToPage(targetPage)
```

4 个主页面通过 `HorizontalPager` 实现，2 个二级页面（日志、引用）通过 `AnimatedContent` 淡入淡出覆盖显示。

---

## 📂 项目结构

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # 单 Activity 入口，HorizontalPager 导航
├── 📝 log/                          # 运行时日志系统
├── 📦 model/                        # 数据类
│   ├── IslandConfig.kt             # 95 字段配置模型，对应 HyperOS API
│   ├── IslandScenario.kt           # 20+ 预设场景（导航、打车、音乐等）
│   ├── IslandTemplate.kt           # 可保存的模板数据类
│   └── SpoofPackages.kt            # 已知包名映射
├── ⚙️ service/
│   └── IslandManager.kt            # 广播桥接（应用 → SystemUI）
├── 🎨 ui/
│   ├── components/
│   │   ├── HyperPage.kt            # 统一页面模板
│   │   ├── HyperComponents.kt      # 15+ 主题自适应 UI 控件
│   │   ├── GlassCard.kt            # 玻璃拟态卡片容器
│   │   ├── LiquidGlassNavBar.kt    # 悬浮玻璃导航栏
│   │   ├── DampedDragAnimation.kt  # 基于物理的变形动画
│   │   └── InteractiveHighlight.kt # AGSL 着色器光效
│   ├── screens/                     # 6 个页面 Composable
│   └── theme/
│       ├── ThemeManager.kt          # 基于 DataStore 的主题状态机
│       ├── Theme.kt                 # Compose 主题包装器（3 主题）
│       └── Color.kt                 # 颜色令牌 + GlassTokens
├── 🔧 utils/
│   ├── ConfigBackupHelper.kt       # JSON 导入/导出
│   └── DeviceInfoHelper.kt         # 系统信息采集
└── 🪝 xposed/
    └── MainHook.kt                  # Xposed Hook 入口（自Hook + SystemUI Hook）
```

---

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 💻 编程语言 | Kotlin |
| 🎨 UI 框架 | Jetpack Compose + Material Design 3 |
| 🎭 设计系统 | Miuix (HyperOS) + Android Liquid Glass |
| ⚡ Hook 框架 | Xposed API 82（兼容 LSPosed） |
| 💾 数据存储 | DataStore Preferences + SharedPreferences |
| 🔨 构建系统 | Gradle Kotlin DSL，Compose BOM 2025.05.01 |
| 🖼️ 玻璃效果 | Kyant0/AndroidLiquidGlass Backdrop API |
| 🎨 颜色选择器 | skydoves/colorpicker-compose |

---

## 📝 更新日志

### 🏷️ Preview0.18 — 首次发布

- 🏝️ HyperOS 3 超级岛完整自定义
- 🎨 三大主题系统（Miuix / MD3 / Liquid Glass）
- 🌗 深色/浅色模式适配
- 👆 HorizontalPager 滑动导航
- 📐 统一 HyperPage 布局系统
- 🎭 20+ 场景预设与包名伪装
- 📋 模板保存/应用/删除
- 📱 应用白名单管理
- 💾 配置导入/导出（JSON）
- 📊 实时日志查看器 + LSPosed 日志同步
- 🫧 悬浮 Liquid Glass 导航栏
- 🪝 双进程 Xposed Hook 架构
- 🎢 基于物理的玻璃导航栏动画
- ✨ AGSL RuntimeShader 交互高光

---

## 🙏 致谢

| 项目 | 作者 | 描述 |
|------|------|------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | HyperOS 设计语言组件 |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | 背景模糊效果库 |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | Xposed 框架 |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | 参考实现 |
| [SukiSU-Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra) | SukiSU Team | UI 模式参考 |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | 布局架构参考 |
| [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) | skydoves | 颜色选择器 |

---

## 📄 开源协议

```
MIT License — Copyright (c) 2026 KangQi
```

查看 [LICENSE](LICENSE) 获取完整协议文本。

---

<div align="center">

**⭐ 如果觉得有用，请给项目点个 Star！⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
