<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 スーパーアイランド カスタマイズ Xposed モジュール — プレビュー版 ✨**

🌐 [简体中文](README_zh-CN.md) | **日本語** | [English](README.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=Preview&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 HyperOS 3 スーパーアイランドを完全にコントロール — コンテンツ、色、アニメーションなどをカスタマイズ。

</div>

---

## 📋 目次

- [✨ 機能](#-機能)
- [🎨 トリプルテーマ対応](#-トリプルテーマ対応)
- [📦 動作要件](#-動作要件)
- [🔧 インストール](#-インストール)
- [🏗️ ソースからビルド](#️-ソースからビルド)
- [🛠️ 技術スタック](#️-技術スタック)
- [📝 更新履歴](#-更新履歴)
- [🙏 クレジット](#-クレジット)
- [📄 ライセンス](#-ライセンス)

---

## ✨ 機能

| 機能 | 説明 |
|------|------|
| 🏝️ **スーパーアイランド カスタマイズ** | コンテンツ、色、時間、プログレスバー、タイマー、ボタンを完全にカスタマイズ |
| 🎭 **20以上のシナリオプリセット** | ナビ、タクシー、フードデリバリー、音楽、決済、通話、タイマー、ダウンロードなど |
| 📋 **テンプレートシステム** | カスタムアイランド設定を再利用可能なテンプレートとして保存・適用 |
| 📱 **アプリホワイトリスト** | スーパーアイランドをトリガーできるアプリを制御 |
| 🔄 **パッケージスプーフィング** | 異なるアプリIDでアイランド表示をエミュレート |
| 💾 **設定バックアップ/復元** | JSON形式で全設定をエクスポート・インポート |
| 📊 **リアルタイムログビューア** | 検索・フィルター付きモジュールログ、LSPosedログ同期 |
| 🖌️ **トリプルテーマ** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **ダーク/ライトモード** | システム追従 / ライト / ダークモード対応 |
| 👆 **スワイプナビゲーション** | HorizontalPagerによるスムーズなタブ切り替え |

---

## 🎨 トリプルテーマ対応

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| ネイティブHyperOSデザイン | Google Material You + ダイナミックカラー | リアルタイム背景ぼかし＆ガラス効果 |

</div>

- 🌗 **ダーク / ライト / システム追従** — 3つの表示モード
- 🎛️ **独立ドックスタイル** — ドックバーのテーマを独立選択可能
- 👆 **スワイプタブ** — HorizontalPagerでスムーズなページ遷移
- 🫧 **ガラス透明度調整** — ガラスエフェクトの透過度をカスタマイズ

---

## 📦 動作要件

| 要件 | バージョン |
|------|-----------|
| 📱 Android | 11+（API 30+） |
| 🔷 HyperOS | 3.x（Xiaomi / Redmi / POCO） |
| ⚡ Xposedフレームワーク | LSPosed / LSPosed Fork |
| 🔓 Root権限 | KernelSU / Magisk / APatch |

---

## 🔧 インストール

1. ⚡ [LSPosed](https://github.com/LSPosed/LSPosed)をインストール
2. 📥 [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases) から最新APKをダウンロード
3. 📲 APKをインストール
4. ✅ LSPosedでモジュールを有効化 → **System UI** をスコープに追加
5. 🔄 再起動またはSystem UIを再起動

---

## 🏗️ ソースからビルド

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

📦 APK出力先：`app/build/outputs/apk/debug/app-debug.apk`

---

## 🛠️ 技術スタック

| カテゴリ | 技術 |
|---------|------|
| 💻 言語 | Kotlin |
| 🎨 UIフレームワーク | Jetpack Compose + Material Design 3 |
| 🎭 デザインシステム | Miuix (HyperOS) + Android Liquid Glass |
| ⚡ Hookフレームワーク | Xposed API 82（LSPosed互換） |
| 💾 ストレージ | DataStore Preferences + SharedPreferences |
| 🔨 ビルドシステム | Gradle Kotlin DSL, Compose BOM 2025.05.01 |

---

## 📝 更新履歴

### 🏷️ v1.0.0-preview — 初回リリース

- 🏝️ HyperOS 3 スーパーアイランド完全カスタマイズ
- 🎨 トリプルテーマシステム（Miuix / MD3 / Liquid Glass）
- 🌗 ダーク/ライトモード対応
- 👆 HorizontalPagerスワイプナビゲーション
- 📐 統一HyperPageレイアウトシステム
- 🎭 20以上のシナリオプリセット＋パッケージスプーフィング
- 📋 テンプレート保存/適用/削除
- 📱 アプリホワイトリスト管理
- 💾 設定インポート/エクスポート（JSON）
- 📊 リアルタイムログビューア＋LSPosedログ同期
- 🫧 フローティングLiquid Glassナビゲーションバー

---

## 🙏 クレジット

| プロジェクト | 作者 | 説明 |
|-------------|------|------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | HyperOSデザイン言語コンポーネント |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | 背景ぼかしエフェクトライブラリ |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | Xposedフレームワーク |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | 参考実装 |
| [SukiSU-Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra) | SukiSU Team | UIパターン参考 |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | レイアウトアーキテクチャ参考 |

---

## 📄 ライセンス

```
MIT License — Copyright (c) 2026 KangQi
```

全文は [LICENSE](LICENSE) をご覧ください。

---

<div align="center">

**⭐ このプロジェクトが役に立ったら Star をお願いします！⭐**

Made with ❤️ by [KangQi](https://github.com/KangQiovo)

</div>
