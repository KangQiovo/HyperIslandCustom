<div align="center">

# 🏝️ HyperIsland Custom Preview

**✨ โมดูล Xposed ปรับแต่ง Super Island ของ HyperOS 3 — รุ่นตัวอย่าง ✨**

🌐 [简体中文](README.md) | [繁體中文](README_zh-TW.md) | [English](README_en.md) | [日本語](README_ja.md) | [한국어](README_ko.md) | **ไทย** | [العربية](README_ar.md)

[![Android](https://img.shields.io/badge/Android-11%2B-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-30%2B-2196F3?style=for-the-badge)](https://developer.android.com/about/versions/11)
[![Xposed](https://img.shields.io/badge/Xposed-100%2B-F4511E?style=for-the-badge)](https://github.com/LSPosed/LSPosed)
[![License](https://img.shields.io/badge/License-MIT-FDD835?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/github/v/release/KangQiovo/HyperIslandCustom-Preview?include_prereleases&style=for-the-badge&label=Preview&color=AB47BC)](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)

<br/>

> 🚀 ควบคุม Super Island ของ HyperOS 3 อย่างเต็มรูปแบบ — ปรับแต่งเนื้อหา สี แอนิเมชัน และอื่นๆ

</div>

---

## 📋 สารบัญ

- [✨ คุณสมบัติ](#-คุณสมบัติ)
- [🎨 รองรับ 3 ธีม](#-รองรับ-3-ธีม)
- [📦 ความต้องการของระบบ](#-ความต้องการของระบบ)
- [🔧 การติดตั้ง](#-การติดตั้ง)
- [🧠 หลักการทำงาน](#-หลักการทำงาน)
- [📝 บันทึกการเปลี่ยนแปลง](#-บันทึกการเปลี่ยนแปลง)
- [🙏 เครดิต](#-เครดิต)
- [📄 สัญญาอนุญาต](#-สัญญาอนุญาต)
- [📬 ติดต่อ](#-ติดต่อ)

---

## ✨ คุณสมบัติ

| คุณสมบัติ | คำอธิบาย |
|-----------|----------|
| 🏝️ **ปรับแต่ง Super Island** | ปรับแต่งเนื้อหา สี เวลา แถบความคืบหน้า ตัวจับเวลา และปุ่มได้อย่างเต็มที่ |
| 🎭 **20+ พรีเซ็ตสถานการณ์** | นำทาง แท็กซี่ ส่งอาหาร เพลง การชำระเงิน โทรศัพท์ ตัวจับเวลา ดาวน์โหลด ฯลฯ |
| 📋 **ระบบเทมเพลต** | บันทึกและใช้งานค่าปรับแต่งเป็นเทมเพลตที่นำกลับมาใช้ได้ |
| 📱 **รายการอนุญาตแอป** | ควบคุมว่าแอปใดสามารถเรียกใช้ Super Island |
| 🔄 **การปลอมแปลงแพ็กเกจ** | จำลองตัวตนของแอปอื่นสำหรับการแสดง Island |
| 💾 **สำรอง/กู้คืนค่า** | ส่งออกและนำเข้าการตั้งค่าทั้งหมดเป็น JSON |
| 📊 **ดูบันทึกแบบเรียลไทม์** | ตรวจสอบบันทึกโมดูลพร้อมค้นหา กรอง และซิงค์บันทึก LSPosed |
| 🖌️ **ระบบ 3 ธีม** | Miuix (HyperOS) / Material Design 3 / Android Liquid Glass |
| 🌗 **โหมดมืด/สว่าง** | ตามระบบ / สว่าง / มืด |
| 👆 **การนำทางแบบปัด** | HorizontalPager สำหรับการสลับแท็บอย่างลื่นไหล |

---

## 🎨 รองรับ 3 ธีม

<div align="center">

| 🟦 Miuix (HyperOS) | 🟪 Material Design 3 | 🔲 Android Liquid Glass |
|:---:|:---:|:---:|
| ดีไซน์ HyperOS ดั้งเดิม | Google Material You + สีไดนามิก | เบลอพื้นหลังเรียลไทม์ & เอฟเฟกต์แก้ว |

</div>

---

## 📦 ความต้องการของระบบ

| ความต้องการ | เวอร์ชัน |
|-------------|---------|
| 📱 Android | 11+ (API 30+) |
| 🔷 HyperOS | 3.x (Xiaomi / Redmi / POCO) |
| ⚡ Xposed Framework | LSPosed / LSPosed Fork |
| 🔓 สิทธิ์ Root | KernelSU / Magisk / APatch |

---

## 🔧 การติดตั้ง

1. ⚡ ติดตั้ง [LSPosed](https://github.com/LSPosed/LSPosed) (หรือ LSPosed Fork)
2. 📥 ดาวน์โหลด APK ล่าสุดจาก [**Releases**](https://github.com/KangQiovo/HyperIslandCustom-Preview/releases)
3. 📲 ติดตั้ง APK
4. ✅ เปิดใช้งานโมดูลใน LSPosed → เลือก **System UI** ใน scope
5. 🔄 รีบูตหรือรีสตาร์ท System UI

---

## 🧠 หลักการทำงาน

### 🪝 Xposed Hook & การดักจับ

โมดูลทำงานข้าม **2 โปรเซส** พร้อมกัน:

```
┌─────────────────────┐      บรอดแคสต์             ┌─────────────────────┐
│   📱 โปรเซสแอป       │  ───────────────────────▶  │  🖥️ โปรเซส SystemUI  │
│                     │   ACTION_SHOW_ISLAND       │                     │
│  IslandManager      │                            │  MainHook           │
│  ├─ สร้าง Intent     │                            │  ├─ ตัวรับบรอดแคสต์  │
│  ├─ โหลด Bitmap     │                            │  ├─ สร้าง JSON       │
│  └─ ส่งบรอดแคสต์     │                            │  ├─ ปลอมแปลงแพ็กเกจ │
│                     │                            │  └─ ส่งการแจ้งเตือน  │
└─────────────────────┘                            └─────────────────────┘
```

**ขั้นตอนหลัก:**

1. **Self-Hook** — Hook เมธอด `isModuleActive()` ของตัวเองเพื่อรายงานสถานะการเปิดใช้งาน
2. **ฉีด SystemUI** — Hook `SystemUIApplication.onCreate()` เพื่อลงทะเบียน `BroadcastReceiver` ใน SystemUI
3. **แมป Config → JSON** — แปลง 40+ ฟิลด์จาก `IslandConfig` เป็น `miui.focus.param` JSON ที่ตรงกับ API ภายในของ HyperOS 3
4. **ปลอมแปลงแพ็กเกจ** — ดักจับ `getPackageName()`, `getUid()`, `getSmallIcon()` เพื่อให้ Island แสดงเทมเพลตที่ถูกต้อง
5. **ส่งการแจ้งเตือน** — ส่งการแจ้งเตือนระบบผ่าน `NotificationManager.notify()` และ HyperOS จะเรนเดอร์ Super Island

---

### 🎨 สถาปัตยกรรม 3 ธีม

ระบบธีมใช้ **CompositionLocal** สำหรับการเข้าถึงทั่วทั้ง UI tree:

| คุณสมบัติ | ตัวเลือก | ค่าเริ่มต้น |
|-----------|---------|------------|
| `appTheme` | MIUIX / MD3 / LIQUID_GLASS | MIUIX |
| `dockStyle` | MIUIX / MD3 / LIQUID_GLASS | LIQUID_GLASS |
| `darkMode` | FOLLOW_SYSTEM / LIGHT / DARK | FOLLOW_SYSTEM |

คอนโทรล 15+ ตัวปรับตัวตามธีมอัตโนมัติ: `HyperSwitch`, `HyperSlider`, `HyperButton` เป็นต้น

---

### 🫧 การเรนเดอร์ Glass Morphism

เรนเดอร์แก้ว 3 ชั้นโดยใช้ [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass):

| ชั้น | เอฟเฟกต์ | การใช้งาน |
|------|---------|-----------|
| 🪟 **พื้นหลัง** | เบลอเรียลไทม์ + vibrancy | `drawBackdrop { blur(12f.dp); vibrancy() }` |
| 🎨 **สีเคลือบ** | โอเวอร์เลย์กึ่งโปร่งใส | `drawRect(Color.White.copy(alpha=0.08f))` |
| ✨ **ขอบ** | ไฮไลท์ขอบแบบไล่สี | `Brush.verticalGradient(...)` |

---

## 📝 บันทึกการเปลี่ยนแปลง

### 🏷️ Preview0.18 — เวอร์ชันแรก

- 🏝️ ปรับแต่ง Super Island ของ HyperOS 3 อย่างเต็มรูปแบบ
- 🎨 ระบบ 3 ธีม (Miuix / MD3 / Liquid Glass)
- 🌗 รองรับโหมดมืด/สว่าง
- 👆 การนำทางแบบปัดด้วย HorizontalPager
- 📐 ระบบเลย์เอาต์ HyperPage แบบรวม
- 🎭 20+ พรีเซ็ตสถานการณ์ + การปลอมแปลงแพ็กเกจ
- 📋 บันทึก/ใช้งาน/ลบเทมเพลต
- 📱 จัดการรายการอนุญาตแอป
- 💾 นำเข้า/ส่งออกค่า (JSON)
- 📊 ดูบันทึกเรียลไทม์ + ซิงค์ LSPosed

---

## 🙏 เครดิต

| โปรเจกต์ | ผู้สร้าง | คำอธิบาย |
|----------|---------|----------|
| [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) | YuKongA | คอมโพเนนต์ภาษาดีไซน์ HyperOS |
| [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Kyant0 | ไลบรารีเอฟเฟกต์เบลอพื้นหลัง |
| [LSPosed](https://github.com/LSPosed/LSPosed) | LSPosed Team | เฟรมเวิร์ก Xposed |
| [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler) | ReChronoRain | การอ้างอิงการใช้งาน |
| [OShin](https://github.com/suqi8/OShin) | suqi8 | การอ้างอิงสถาปัตยกรรมเลย์เอาต์ |

---

## 📄 สัญญาอนุญาต

```
MIT License — Copyright (c) 2026 KangQi
```

ดูข้อความเต็มที่ [LICENSE](LICENSE)

---

<div align="center">

**⭐ ถ้าคุณคิดว่าโปรเจกต์นี้มีประโยชน์ กรุณากด Star! ⭐**

Made with ❤️ by [KangQi](http://www.coolapk.com/u/21241695)

</div>
