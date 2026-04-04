<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 超级岛自定义 Xposed 模块 — 预览版 ✨**

🌐 **简体中文** | [日本語](README_ja.md) | [English](README.md)

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

## 📂 项目结构

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # 单 Activity 入口，HorizontalPager 导航
├── 📝 log/                          # 运行时日志系统
├── 📦 model/                        # 数据类（IslandConfig、场景、模板）
├── ⚙️ service/                      # IslandManager（广播到 SystemUI）
├── 🎨 ui/
│   ├── components/                  # 可复用 UI 组件
│   │   ├── HyperPage.kt            # 统一页面模板
│   │   ├── HyperComponents.kt      # 主题感知组件包装器
│   │   ├── GlassCard.kt            # 玻璃态卡片容器
│   │   └── LiquidGlassNavBar.kt    # 悬浮玻璃导航栏
│   ├── screens/                     # 6 个页面 Composable
│   └── theme/                       # ThemeManager、颜色令牌、三主题系统
├── 🔧 utils/                        # 配置备份、设备信息工具
└── 🪝 xposed/                       # Xposed Hook 入口
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

---

## 📝 更新日志

### 🏷️ v1.0.0-preview — 首次发布

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

Made with ❤️ by [KangQi](https://github.com/KangQiovo)

</div>
