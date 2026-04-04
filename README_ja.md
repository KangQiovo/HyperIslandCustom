<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 スーパーアイランド カスタマイズ Xposed モジュール — プレビュー版 ✨**

🌐 [简体中文](README.md) | [繁體中文](README_zh-TW.md) | [English](README_en.md) | **日本語** | [한국어](README_ko.md) | [ไทย](README_th.md) | [العربية](README_ar.md)

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
- [🧠 実装原理](#-実装原理)
- [📂 プロジェクト構成](#-プロジェクト構成)
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

## 🧠 実装原理

### 🪝 Xposed Hookインターセプト

モジュールは**2つのプロセス**で同時に動作します：

```
┌─────────────────────┐       ブロードキャスト       ┌─────────────────────┐
│   📱 アプリプロセス    │  ───────────────────────▶  │  🖥️ SystemUIプロセス  │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ Intent構築       │                            │  ├─ BroadcastReceiver│
│  ├─ Bitmap読込       │                            │  ├─ JSON構築         │
│  └─ ブロードキャスト   │                            │  ├─ パッケージ偽装    │
│                     │                            │  └─ 通知送信         │
└─────────────────────┘                            └─────────────────────┘
```

**コアフロー：**

1. **セルフHook** — `XC_MethodReplacement.returnConstant(true)` で自身の `isModuleActive()` をHookし、有効化状態を報告。

2. **SystemUI注入** — `SystemUIApplication.onCreate()` をHookして、SystemUIプロセスにカスタム `BroadcastReceiver` を登録。`ACTION_SHOW_ISLAND` / `ACTION_DISMISS_ISLAND` を監視。

3. **設定 → JSONマッピング** — `IslandManager.showIsland(config)` が呼ばれると、`IslandConfig` の40以上のフィールドが `Intent` にパッケージされブロードキャスト。SystemUI側では受信器が HyperOS 3 内部APIに準拠した `miui.focus.param` JSON構造を構築：

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

4. **パッケージスプーフィング** — 正しい内蔵テンプレートでアイランドを描画させるため（例：ナビはGaodeMapテンプレート）、以下のメソッドをインターセプト：
   - `StatusBarNotification.getPackageName()` → 偽装パッケージ名を返却
   - `StatusBarNotification.getUid()` → 偽装アプリの実UIDを返却
   - `Notification.getSmallIcon()` → 偽装アプリの実アイコンを読み込み

5. **通知送信** — 構築したJSONを含むシステム通知が `NotificationManager.notify()` で送信され、HyperOSのネイティブテンプレートエンジンがスーパーアイランドを描画。

---

### 🎨 トリプルテーマアーキテクチャ

テーマシステムは **CompositionLocal** によりUI全体でプロップドリリング不要のアクセスを実現：

```
HyperIslandTheme(themeManager, backdrop)
  ├─ isDark 判定（darkMode 設定を尊重）
  ├─ 透明なステータスバー/ナビバー設定
  ├─ colorScheme 構築
  │  ├─ MD3 → dynamicLightColorScheme / dynamicDarkColorScheme
  │  └─ その他 → 静的フォールバックパレット
  ├─ MiuixTheme でラップ（Miuixコンポーネント用）
  ├─ MaterialTheme でラップ（MD3コンポーネント用）
  └─ LocalIsDark, LocalBackdrop, LocalThemeManager を提供
```

**ThemeManager**（DataStoreベースの状態マシン）：
| プロパティ | 選択肢 | デフォルト |
|-----------|--------|-----------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |
| `glassBarAlpha` | 0.0 ~ 1.0 | 0.4 |

**テーマ適応コンポーネント**（`HyperComponents.kt`）：

各UIコントロールが現在のテーマに自動適応：

```kotlin
@Composable
fun HyperSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    when (themeManager.appTheme) {
        MIUIX        → MiuixSwitch(...)         // ネイティブHyperOSスイッチ
        LIQUID_GLASS → LiquidGlassToggle(...)   // カスタムガラストグル
        else         → Switch(...)               // Material3
    }
}
```

15以上のコントロールがこのパターンに従います：`HyperSwitch`、`HyperSlider`、`HyperButton`、`HyperTextField`、`HyperDropdown`、`HyperFilterChip` など。

---

### 🫧 ガラスモーフィズムレンダリング

[Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) Backdrop APIによる三層ガラスレンダリング：

| レイヤー | エフェクト | 実装 |
|---------|----------|------|
| 🪟 **バックドロップ** | リアルタイムぼかし + バイブランシー | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **ティント** | 半透明オーバーレイ | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **ボーダー** | グラデーションエッジハイライト | `Brush.verticalGradient(White@0.12 → White@0.04)` |

**LiquidGlassNavBar** — フローティングボトムナビの3サブレイヤー：
1. 可視タブ行：backdrop ぼかし + レンズ効果
2. 不可視ティントレイヤー：アクセントカラーのSrcAtopブレンドモード
3. ガラススライダーインジケーター：速度ベースの変形アニメーション

**インタラクティブハイライト**（Android 13+ RuntimeShader / AGSL）：
```glsl
half4 main(float2 coord) {
    float dist = distance(coord, position);
    float intensity = smoothstep(radius, radius * 0.5, dist);
    return color * intensity;    // タッチに追従する円形ライト
}
```

---

### 🎢 物理ベースアニメーション

**DampedDragAnimation** — ガラスナビバースライダーの液体変形システム：

| アニメーション | 剛性 | 減衰 | 用途 |
|--------------|------|------|------|
| Value | 1.0 | 1000 | タブ位置 |
| Velocity | 0.5 | 300 | ドラッグ速度 |
| Press | 1.0 | 1000 | タッチ圧力 |
| ScaleXY | 0.6/0.7 | 250 | スカッシュ/ストレッチ |

高速ドラッグリリース時、速度が形状変形に影響：
```
scaleX /= 1 - velocity×0.75    → 水平方向にストレッチ
scaleY *= 1 - velocity×0.25    → 垂直方向にスカッシュ
```
実際の物理法則に基づいた**液体スカッシュ＆ストレッチ**エフェクトを実現。

---

### 📐 統一HyperPageレイアウト

[OShin](https://github.com/suqi8/OShin) の FunPage パターンに着想を得て、各画面のボイラープレートを排除：

```kotlin
HyperPage(title = "設定", onBack = { nav.pop() }) {
    // コンテンツ — テーマ対応トップバー、ステータスバー余白、戻るボタンが自動適用
}
```

| 条件 | 動作 |
|------|------|
| `onBack != null` | サブページ → 戻るボタン付きテーマ対応トップバー + ステータスバーパディング |
| `onBack == null` | メインページ → Scaffoldがタイトルを処理 |
| テーマ = MIUIX | ネイティブ `MiuixTopAppBar` |
| テーマ = MD3 | Material3 `TopAppBar` |
| テーマ = LIQUID_GLASS | カスタム `Row` + ガラススタイル |

---

### 👆 HorizontalPagerナビゲーション

スワイプジェスチャーとボトムナビの双方向同期アーキテクチャ：

```kotlin
// 同期1：スワイプ → ルート状態を更新
snapshotFlow { pagerState.currentPage }.collectLatest { page ->
    selectedRoute = navItems[page].route
}

// 同期2：ナビタップ → ページアニメーション
pagerState.animateScrollToPage(targetPage)
```

4つのメインページを `HorizontalPager` で実装、2つのサブページ（ログ、クレジット）を `AnimatedContent` のフェードトランジションで表示。

---

## 📂 プロジェクト構成

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # シングルActivity、HorizontalPagerナビゲーション
├── 📝 log/                          # ランタイムログシステム
├── 📦 model/                        # データクラス
│   ├── IslandConfig.kt             # 95フィールドの設定モデル（HyperOS API対応）
│   ├── IslandScenario.kt           # 20以上のプリセットシナリオ
│   ├── IslandTemplate.kt           # 保存可能なテンプレートデータクラス
│   └── SpoofPackages.kt            # 既知パッケージ名マッピング
├── ⚙️ service/
│   └── IslandManager.kt            # ブロードキャストブリッジ（アプリ → SystemUI）
├── 🎨 ui/
│   ├── components/
│   │   ├── HyperPage.kt            # 統一ページテンプレート
│   │   ├── HyperComponents.kt      # 15以上のテーマ適応UIコントロール
│   │   ├── GlassCard.kt            # ガラスモーフィズムカード
│   │   ├── LiquidGlassNavBar.kt    # フローティングガラスナビバー
│   │   ├── DampedDragAnimation.kt  # 物理ベース変形アニメーション
│   │   └── InteractiveHighlight.kt # AGSLシェーダーライトエフェクト
│   ├── screens/                     # 6画面のComposable
│   └── theme/
│       ├── ThemeManager.kt          # DataStoreベースのテーマ状態マシン
│       ├── Theme.kt                 # Composeテーマラッパー（3テーマ）
│       └── Color.kt                 # カラートークン + GlassTokens
├── 🔧 utils/
│   ├── ConfigBackupHelper.kt       # JSONインポート/エクスポート
│   └── DeviceInfoHelper.kt         # システム情報収集
└── 🪝 xposed/
    └── MainHook.kt                  # Xposed Hookエントリ（セルフHook + SystemUI Hook）
```

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
| 🖼️ ガラス効果 | Kyant0/AndroidLiquidGlass Backdrop API |
| 🎨 カラーピッカー | skydoves/colorpicker-compose |

---

## 📝 更新履歴

### 🏷️ Preview0.18 — 初回リリース

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
- 🪝 デュアルプロセスXposed Hookアーキテクチャ
- 🎢 物理ベースガラスナビバーアニメーション
- ✨ AGSL RuntimeShaderインタラクティブハイライト

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
| [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) | skydoves | カラーピッカー |

---

## 📄 ライセンス

```
MIT License — Copyright (c) 2026 KangQi
```

全文は [LICENSE](LICENSE) をご覧ください。

---

<div align="center">

**⭐ このプロジェクトが役に立ったら Star をお願いします！⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
