<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ HyperOS 3 슈퍼 아일랜드 커스터마이징 Xposed 모듈 — 프리뷰 빌드 ✨**

🌐 [简体中文](README.md) | [繁體中文](README_zh-TW.md) | [English](README_en.md) | [日本語](README_ja.md) | **한국어** | [ไทย](README_th.md) | [العربية](README_ar.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=Preview&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 HyperOS 3 슈퍼 아일랜드를 완전히 제어 — 콘텐츠, 색상, 애니메이션 등을 커스터마이징하세요.

</div>

---

## 📋 목차

- [✨ 기능](#-기능)
- [🎨 트리플 테마 지원](#-트리플-테마-지원)
- [📦 시스템 요구사항](#-시스템-요구사항)
- [🔧 설치 방법](#-설치-방법)
- [🏗️ 소스에서 빌드](#️-소스에서-빌드)
- [🧠 구현 원리](#-구현-원리)
- [📂 프로젝트 구조](#-프로젝트-구조)
- [🛠️ 기술 스택](#️-기술-스택)
- [📝 변경 이력](#-변경-이력)
- [🙏 크레딧](#-크레딧)
- [📄 라이선스](#-라이선스)
- [📬 연락처](#-연락처)

---

## ✨ 기능

| 기능 | 설명 |
|------|------|
| 🏝️ **슈퍼 아일랜드 커스터마이징** | 콘텐츠, 색상, 시간, 프로그레스바, 타이머, 버튼을 완전히 커스터마이징 |
| 🎭 **20개 이상 시나리오 프리셋** | 내비게이션, 택시, 배달, 음악, 결제, 통화, 타이머, 다운로드 등 |
| 📋 **템플릿 시스템** | 커스텀 아일랜드 설정을 재사용 가능한 템플릿으로 저장 및 적용 |
| 📱 **앱 화이트리스트** | 슈퍼 아일랜드를 트리거할 수 있는 앱 제어 |
| 🔄 **패키지 스푸핑** | 다른 앱 ID로 아일랜드 표시 에뮬레이트 |
| 💾 **설정 백업/복원** | JSON 형식으로 모든 설정 내보내기/가져오기 |
| 📊 **실시간 로그 뷰어** | 검색, 필터 기능이 있는 모듈 로그 모니터링 |
| 🖌️ **트리플 테마 시스템** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **다크/라이트 모드** | 시스템 따르기 / 라이트 / 다크 모드 적응 |
| 👆 **스와이프 내비게이션** | HorizontalPager로 부드러운 탭 전환 |

---

## 🎨 트리플 테마 지원

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| 네이티브 HyperOS 디자인 | Google Material You + 다이나믹 컬러 | 실시간 배경 블러 & 글래스 효과 |

</div>

- 🌗 **다크 / 라이트 / 시스템 따르기** — 세 가지 디스플레이 모드
- 🎛️ **독립 독 스타일** — 독바 테마 독립 선택 가능
- 👆 **스와이프 탭** — HorizontalPager로 부드러운 페이지 전환
- 🫧 **글래스 투명도 조절** — 글래스 효과 투명도 커스터마이징

---

## 📦 시스템 요구사항

| 요구사항 | 버전 |
|----------|------|
| 📱 Android | 11+ (API 30+) |
| 🔷 HyperOS | 3.x (Xiaomi / Redmi / POCO) |
| ⚡ Xposed 프레임워크 | LSPosed / LSPosed Fork |
| 🔓 Root 접근 | KernelSU / Magisk / APatch |

---

## 🔧 설치 방법

1. ⚡ [LSPosed](https://github.com/LSPosed/LSPosed) 설치 (또는 LSPosed Fork)
2. 📥 [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)에서 최신 APK 다운로드
3. 📲 APK 설치
4. ✅ LSPosed에서 모듈 활성화 → **System UI** 스코프 체크
5. 🔄 재부팅 또는 System UI 재시작

---

## 🏗️ 소스에서 빌드

```bash
git clone https://github.com/KangQiovo/HyperIslandCustom-Preview.git
cd HyperIslandCustom-Preview
./gradlew assembleDebug
```

📦 APK 출력: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🧠 구현 원리

### 🪝 Xposed Hook 인터셉트

모듈은 **두 개의 프로세스**에서 동시에 작동합니다:

```
┌─────────────────────┐        브로드캐스트         ┌─────────────────────┐
│   📱 앱 프로세스      │  ───────────────────────▶  │  🖥️ SystemUI 프로세스 │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ Intent 구성      │                            │  ├─ BroadcastReceiver│
│  ├─ Bitmap 로드      │                            │  ├─ JSON 구성        │
│  └─ 브로드캐스트 전송  │                            │  ├─ 패키지 스푸핑     │
│                     │                            │  └─ 알림 전송        │
└─────────────────────┘                            └─────────────────────┘
```

**핵심 흐름:**

1. **셀프 Hook** — `XC_MethodReplacement.returnConstant(true)`로 자체 `isModuleActive()`를 Hook하여 활성화 상태 보고.

2. **SystemUI 주입** — `SystemUIApplication.onCreate()`를 Hook하여 SystemUI 프로세스에 커스텀 `BroadcastReceiver` 등록.

3. **설정 → JSON 매핑** — `IslandManager.showIsland(config)` 호출 시 `IslandConfig`의 40개 이상 필드가 `Intent`에 패키징되어 브로드캐스트. SystemUI 수신기가 HyperOS 3 내부 API에 맞는 `miui.focus.param` JSON 구조 생성.

4. **패키지 스푸핑** — 올바른 내장 템플릿으로 아일랜드를 렌더링하기 위해 여러 메서드를 인터셉트.

5. **알림 발송** — `NotificationManager.notify()`를 통해 시스템 알림을 발송하고, HyperOS가 네이티브 템플릿 엔진으로 슈퍼 아일랜드를 렌더링.

---

### 🎨 트리플 테마 아키텍처

**CompositionLocal**을 사용하여 전체 UI 트리에서 프롭 드릴링 없이 접근:

| 속성 | 옵션 | 기본값 |
|------|------|--------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |
| `glassBarAlpha` | 0.0 ~ 1.0 | 0.4 |

15개 이상의 컨트롤이 현재 테마에 자동 적응: `HyperSwitch`, `HyperSlider`, `HyperButton`, `HyperTextField`, `HyperDropdown`, `HyperFilterChip` 등.

---

### 🫧 글래스 모피즘 렌더링

[Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) Backdrop API의 3레이어 글래스 렌더링:

| 레이어 | 효과 | 구현 |
|--------|------|------|
| 🪟 **배경** | 실시간 블러 + 바이브런시 | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **틴트** | 반투명 오버레이 | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **보더** | 그라데이션 엣지 하이라이트 | `Brush.verticalGradient(White@0.12 → White@0.04)` |

---

### 📐 통합 HyperPage 레이아웃

[OShin](https://github.com/suqi8/OShin)의 FunPage 패턴에서 영감:

```kotlin
HyperPage(title = "설정", onBack = { nav.pop() }) {
    // 콘텐츠 — 테마 적용 탑바, 상태바 인셋, 뒤로 버튼 자동 적용
}
```

---

## 📂 프로젝트 구조

```
app/src/main/java/com/kangqi/hIc/
├── 🏠 MainActivity.kt              # 싱글 액티비티, HorizontalPager 내비게이션
├── 📦 model/                        # 데이터 클래스
├── ⚙️ service/
│   └── IslandManager.kt            # 브로드캐스트 브릿지 (앱 → SystemUI)
├── 🎨 ui/
│   ├── components/                  # HyperPage, HyperComponents, GlassCard 등
│   ├── screens/                     # 6개 화면 Composable
│   └── theme/                       # ThemeManager, 컬러 토큰, 트리플 테마
├── 🔧 utils/                        # 설정 백업, 디바이스 정보 헬퍼
└── 🪝 xposed/
    └── MainHook.kt                  # Xposed Hook 엔트리
```

---

## 🛠️ 기술 스택

| 카테고리 | 기술 |
|----------|------|
| 💻 언어 | Kotlin |
| 🎨 UI 프레임워크 | Jetpack Compose + Material Design 3 |
| 🎭 디자인 시스템 | Miuix (HyperOS) + Android Liquid Glass |
| ⚡ Hook 프레임워크 | Xposed API 82 (LSPosed 호환) |
| 💾 스토리지 | DataStore Preferences + SharedPreferences |
| 🔨 빌드 시스템 | Gradle Kotlin DSL, Compose BOM 2025.05.01 |

---

## 📝 변경 이력

### 🏷️ Preview0.18 — 최초 릴리스

- 🏝️ HyperOS 3 슈퍼 아일랜드 전체 커스터마이징
- 🎨 트리플 테마 시스템 (Miuix / MD3 / Liquid Glass)
- 🌗 다크/라이트 모드 적응
- 👆 HorizontalPager 스와이프 내비게이션
- 📐 통합 HyperPage 레이아웃 시스템
- 🎭 20개 이상 시나리오 프리셋 + 패키지 스푸핑
- 📋 템플릿 저장/적용/삭제
- 📱 앱 화이트리스트 관리
- 💾 설정 가져오기/내보내기 (JSON)
- 📊 실시간 로그 뷰어 + LSPosed 로그 동기화
- 🫧 플로팅 Liquid Glass 내비게이션 바

---

## 🙏 크레딧

| 프로젝트 | 저자 | 설명 |
|----------|------|------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | HyperOS 디자인 언어 컴포넌트 |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | 배경 블러 효과 라이브러리 |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | Xposed 프레임워크 |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | 참조 구현 |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | 레이아웃 아키텍처 참조 |

---

## 📄 라이선스

```
MIT License — Copyright (c) 2026 KangQi
```

전체 텍스트는 [LICENSE](LICENSE)를 참조하세요.

---

<div align="center">

**⭐ 이 프로젝트가 유용하다면 Star를 눌러주세요! ⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
