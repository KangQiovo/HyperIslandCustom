<div align="center">

# 🏝️ HyperIsland Custom

**✨ وحدة Xposed لتخصيص الجزيرة الخارقة في HyperOS 3 — النسخة الرسمية ✨**

🌐 [简体中文](README.md) | [繁體中文](README_zh-TW.md) | [English](README_en.md) | [日本語](README_ja.md) | [한국어](README_ko.md) | [ไทย](README_th.md) | **العربية**

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-93%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom?style=for-the-badge&label=Preview&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom/releases)

<br/>

> 🚀 تحكم كامل في الجزيرة الخارقة لنظام HyperOS 3 — تخصيص المحتوى والألوان والرسوم المتحركة والمزيد.

</div>

---

## 📋 جدول المحتويات

- [✨ الميزات](#-الميزات)
- [🎨 دعم الثيمات الثلاثة](#-دعم-الثيمات-الثلاثة)
- [📦 المتطلبات](#-المتطلبات)
- [🔧 التثبيت](#-التثبيت)
- [🧠 مبادئ التنفيذ](#-مبادئ-التنفيذ)
- [📝 سجل التغييرات](#-سجل-التغييرات)
- [🙏 الشكر والتقدير](#-الشكر-والتقدير)
- [📄 الترخيص](#-الترخيص)
- [📬 التواصل](#-التواصل)

---

## ✨ الميزات

| الميزة | الوصف |
|--------|-------|
| 🏝️ **تخصيص الجزيرة الخارقة** | تخصيص كامل للمحتوى والألوان والمدة وأشرطة التقدم والمؤقتات والأزرار |
| 🎭 **أكثر من 20 إعداد مسبق** | الملاحة، سيارة أجرة، توصيل الطعام، الموسيقى، الدفع، المكالمات، المؤقت، التنزيل... |
| 📋 **نظام القوالب** | حفظ وتطبيق إعدادات الجزيرة المخصصة كقوالب قابلة لإعادة الاستخدام |
| 📱 **القائمة البيضاء** | التحكم في التطبيقات التي يمكنها تشغيل الجزيرة الخارقة |
| 🔄 **انتحال الحزمة** | محاكاة هويات تطبيقات مختلفة لعرض الجزيرة |
| 💾 **نسخ احتياطي/استعادة** | تصدير واستيراد جميع الإعدادات بصيغة JSON |
| 📊 **عارض السجلات الفوري** | مراقبة سجلات الوحدة مع البحث والتصفية ومزامنة LSPosed |
| 🖌️ **نظام 3 ثيمات** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **الوضع الداكن/الفاتح** | متابعة النظام / فاتح / داكن |
| 👆 **التنقل بالسحب** | HorizontalPager للتبديل السلس بين علامات التبويب |

---

## 🎨 دعم الثيمات الثلاثة

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| تصميم HyperOS الأصلي | Google Material You + ألوان ديناميكية | تمويه خلفية فوري وتأثيرات زجاجية |

</div>

- 🌗 **داكن / فاتح / متابعة النظام** — ثلاثة أوضاع عرض
- 🎛️ **نمط Dock مستقل** — اختيار ثيم شريط التنقل بشكل مستقل
- 👆 **علامات تبويب قابلة للسحب** — تنقل سلس بين الصفحات
- 🫧 **التحكم في شفافية الزجاج** — ضبط شفافية تأثير الزجاج

---

## 📦 المتطلبات

| المتطلب | الإصدار |
|---------|---------|
| 📱 Android | +11 (API +30) |
| 🔷 HyperOS | 3.x (Xiaomi / Redmi / POCO) |
| ⚡ إطار Xposed | LSPosed / LSPosed Fork |
| 🔓 صلاحيات Root | KernelSU / Magisk / APatch |

---

## 🔧 التثبيت

1. ⚡ تثبيت [LSPosed](https://github.com/LSPosed/LSPosed) (أو LSPosed Fork)
2. 📥 تنزيل أحدث APK من [**Releases**](https://github.com/KangQiovo/HyperIslandCustom/releases)
3. 📲 تثبيت APK
4. ✅ تفعيل الوحدة في LSPosed ← تحديد **System UI** في النطاق
5. 🔄 إعادة التشغيل أو إعادة تشغيل System UI

---

## 🧠 مبادئ التنفيذ

### 🪝 اعتراض Xposed Hook

تعمل الوحدة عبر **عمليتين** في وقت واحد:

```
┌─────────────────────┐          بث                ┌─────────────────────┐
│   📱 عملية التطبيق   │  ───────────────────────▶  │  🖥️ عملية SystemUI   │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ بناء Intent     │                            │  ├─ مستقبل البث     │
│  ├─ تحميل Bitmap    │                            │  ├─ بناء JSON       │
│  └─ إرسال البث      │                            │  ├─ انتحال الحزمة   │
│                     │                            │  └─ إرسال الإشعار   │
└─────────────────────┘                            └─────────────────────┘
```

**التدفق الأساسي:**

1. **Hook ذاتي** — Hook الطريقة `isModuleActive()` الخاصة للإبلاغ عن حالة التفعيل
2. **حقن SystemUI** — Hook `SystemUIApplication.onCreate()` لتسجيل `BroadcastReceiver` مخصص
3. **تعيين Config → JSON** — تحويل أكثر من 40 حقلاً من `IslandConfig` إلى بنية `miui.focus.param` JSON المتوافقة مع API الداخلي لـ HyperOS 3
4. **انتحال الحزمة** — اعتراض `getPackageName()` و `getUid()` و `getSmallIcon()` لعرض القالب الصحيح
5. **إرسال الإشعار** — إرسال إشعار النظام عبر `NotificationManager.notify()` ويقوم HyperOS بعرض الجزيرة الخارقة

---

### 🎨 هندسة الثيمات الثلاثة

يستخدم نظام الثيمات **CompositionLocal** للوصول بدون تمرير الخصائص عبر شجرة UI:

| الخاصية | الخيارات | الافتراضي |
|---------|---------|-----------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |

أكثر من 15 عنصر تحكم يتكيف مع الثيم الحالي تلقائياً.

---

### 🫧 عرض Glass Morphism

عرض زجاجي ثلاثي الطبقات باستخدام [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass):

| الطبقة | التأثير | التنفيذ |
|--------|--------|---------|
| 🪟 **الخلفية** | تمويه فوري + حيوية | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **الصبغة** | طبقة شبه شفافة | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **الحدود** | تمييز حافة متدرج | `Brush.verticalGradient(...)` |

---

## 📝 سجل التغييرات

### 🏷️ 1.0.0 — الإصدار الأول

- 🏝️ تخصيص كامل للجزيرة الخارقة في HyperOS 3
- 🎨 نظام 3 ثيمات (Miuix / MD3 / Liquid Glass)
- 🌗 دعم الوضع الداكن/الفاتح
- 👆 تنقل بالسحب عبر HorizontalPager
- 📐 نظام تخطيط HyperPage الموحد
- 🎭 أكثر من 20 إعداد مسبق + انتحال الحزمة
- 📋 حفظ/تطبيق/حذف القوالب
- 📱 إدارة القائمة البيضاء
- 💾 استيراد/تصدير الإعدادات (JSON)
- 📊 عارض سجلات فوري + مزامنة LSPosed

---

## 🙏 الشكر والتقدير

| المشروع | المؤلف | الوصف |
|---------|--------|-------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | مكونات لغة تصميم HyperOS |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | مكتبة تأثيرات تمويه الخلفية |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | إطار Xposed |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | تنفيذ مرجعي |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | مرجع هندسة التخطيط |

---

## 📄 الترخيص

```
MIT License — Copyright (c) 2026 KangQi
```

انظر [LICENSE](LICENSE) للنص الكامل.

---

<div align="center">

**⭐ !إذا وجدت هذا المشروع مفيداً، يرجى إعطاء نجمة ⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
