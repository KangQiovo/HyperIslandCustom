package com.kangqi.hIc.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Collects comprehensive device and system information for log export.
 * Inspired by HyperCeiler's DeviceInfoBuilder.
 */
object DeviceInfoHelper {

    fun build(context: Context, moduleActive: Boolean = false): String {
        val sb = StringBuilder()
        val divider = "════════════════════════════════════════"

        sb.appendLine(divider)
        sb.appendLine("  HyperIsland Custom - Device Info")
        sb.appendLine(divider)
        sb.appendLine()

        // ── Module Info ──
        sb.appendLine("【模块信息】")
        sb.appendLine("  ApplicationId: ${getAppId()}")
        sb.appendLine("  VersionName: ${getVersionName()}")
        sb.appendLine("  VersionCode: ${getVersionCode()}")
        sb.appendLine("  ModuleActive: $moduleActive")
        sb.appendLine("  ExportTime: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
        sb.appendLine()

        // ── Device Info ──
        sb.appendLine("【设备信息】")
        sb.appendLine("  Brand: ${Build.BRAND}")
        sb.appendLine("  Model: ${Build.MODEL}")
        sb.appendLine("  Device: ${Build.DEVICE}")
        sb.appendLine("  Product: ${Build.PRODUCT}")
        sb.appendLine("  MarketName: ${getProp("ro.product.marketname")}")
        sb.appendLine("  SoC: ${getProp("ro.soc.model")}")
        sb.appendLine("  Board: ${Build.BOARD}")
        sb.appendLine("  Hardware: ${Build.HARDWARE}")
        sb.appendLine("  Fingerprint: ${Build.FINGERPRINT}")
        sb.appendLine("  DisplayId: ${Build.DISPLAY}")
        sb.appendLine()

        // ── System Info ──
        sb.appendLine("【系统信息】")
        sb.appendLine("  Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        sb.appendLine("  SecurityPatch: ${Build.VERSION.SECURITY_PATCH}")
        sb.appendLine("  Kernel: ${System.getProperty("os.version") ?: "Unknown"}")
        sb.appendLine("  BuildDate: ${getProp("ro.system.build.date")}")
        sb.appendLine("  Host: ${Build.HOST}")
        sb.appendLine("  Type: ${Build.TYPE}")
        sb.appendLine("  Tags: ${Build.TAGS}")
        sb.appendLine()

        // ── HyperOS Info ──
        val hyperOsVersion = getProp("ro.mi.os.version.code")
        val hyperOsIncremental = getProp("ro.mi.os.version.incremental")
        val miuiVersion = getProp("ro.miui.ui.version.name")
        if (hyperOsVersion.isNotEmpty() || miuiVersion.isNotEmpty()) {
            sb.appendLine("【HyperOS / MIUI】")
            if (hyperOsVersion.isNotEmpty()) sb.appendLine("  HyperOS Version: $hyperOsVersion")
            if (hyperOsIncremental.isNotEmpty()) sb.appendLine("  HyperOS Incremental: $hyperOsIncremental")
            if (miuiVersion.isNotEmpty()) sb.appendLine("  MIUI Version: $miuiVersion")
            sb.appendLine("  SystemVersion: ${getProp("ro.system.build.version.incremental")}")
            val isInternational = getProp("ro.product.mod_device").contains("global", ignoreCase = true)
            sb.appendLine("  InternationalBuild: $isInternational")
            sb.appendLine()
        }

        // ── Framework Info ──
        sb.appendLine("【框架信息】")
        val frameworkInfo = getFrameworkInfo(context)
        sb.appendLine("  RootManager: ${getRootManager()}")
        sb.appendLine("  RootVersion: ${getRootVersion()}")
        sb.appendLine("  XposedFramework: ${frameworkInfo.frameworkName}")
        sb.appendLine("  FrameworkVersion: ${frameworkInfo.frameworkVersion}")
        sb.appendLine("  ManagerApp: ${frameworkInfo.managerName}")
        sb.appendLine("  ManagerVersion: ${frameworkInfo.managerVersion}")
        sb.appendLine()

        // ── Battery Info ──
        sb.appendLine("【电池信息】")
        try {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, iFilter)
            val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            val temp = (batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10.0
            sb.appendLine("  Level: ${if (scale > 0) level * 100 / scale else level}%")
            sb.appendLine("  Health: ${batteryHealthString(health)}")
            sb.appendLine("  Temperature: ${temp}\u00B0C")
            val cycleFile = File("/sys/class/power_supply/battery/cycle_count")
            if (cycleFile.exists()) {
                sb.appendLine("  CycleCount: ${cycleFile.readText().trim()}")
            }
        } catch (_: Throwable) {
            sb.appendLine("  (unavailable)")
        }
        sb.appendLine()
        sb.appendLine(divider)

        return sb.toString()
    }

    // ── Framework detection ──

    data class FrameworkInfo(
        val frameworkName: String = "Unknown",
        val frameworkVersion: String = "Unknown",
        val managerName: String = "Unknown",
        val managerVersion: String = "Unknown"
    )

    @Suppress("DEPRECATION")
    fun getFrameworkInfo(context: Context): FrameworkInfo {
        val pm = context.packageManager
        var frameworkName = "Unknown"
        var frameworkVersion = "Unknown"
        var managerName = "Unknown"
        var managerVersion = "Unknown"

        // Check LSPosed Manager (rooted) — these are the actual framework managers
        val lsposedManagers = listOf(
            "org.lsposed.manager" to "LSPosed",
            "io.github.lsposed.manager" to "LSPosed"
        )
        for ((pkg, name) in lsposedManagers) {
            try {
                val pi = pm.getPackageInfo(pkg, 0)
                frameworkName = name
                frameworkVersion = "v${pi.versionName} (${pi.longVersionCode})"
                managerName = name + " Manager"
                managerVersion = "v${pi.versionName}"
                return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
            } catch (_: Throwable) {}
        }

        // Check LSPatch (rootless) — separate from LSPosed
        try {
            val pi = pm.getPackageInfo("org.lsposed.lspatch", 0)
            frameworkName = "LSPatch"
            frameworkVersion = "v${pi.versionName} (${pi.longVersionCode})"
            managerName = "LSPatch"
            managerVersion = "v${pi.versionName}"
            return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
        } catch (_: Throwable) {}

        // Fallback: try detecting via Xposed module metadata / system props
        val xposedProp = getProp("ro.xposed.version")
        if (xposedProp.isNotEmpty()) {
            frameworkName = "Xposed"
            frameworkVersion = xposedProp
        }

        return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
    }

    // ── Root detection ──

    fun getRootManager(): String {
        return try {
            when {
                File("/data/adb/magisk").exists() -> "Magisk"
                File("/data/adb/ksud").exists() -> "KernelSU"
                File("/data/adb/ap").exists() -> "APatch"
                else -> "Unknown"
            }
        } catch (_: Throwable) { "Unknown" }
    }

    private fun getRootVersion(): String {
        return try {
            val manager = getRootManager()
            when (manager) {
                "Magisk" -> {
                    val result = execCommand("su -v")
                    result.ifBlank { execCommand("magisk -v") }
                }
                "KernelSU" -> execCommand("ksud version").ifBlank {
                    getProp("ro.kernelsu.version")
                }
                "APatch" -> getProp("ro.apatch.version")
                else -> "Unknown"
            }
        } catch (_: Throwable) { "Unknown" }
    }

    // ── System properties ──

    fun getProp(key: String): String {
        // Try SystemProperties via reflection first
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java, String::class.java)
            (method.invoke(null, key, "") as? String) ?: ""
        } catch (_: Throwable) {
            // Fallback to getprop command
            try { execCommand("getprop $key").trim() } catch (_: Throwable) { "" }
        }
    }

    private fun execCommand(cmd: String): String {
        return try {
            val process = Runtime.getRuntime().exec(cmd.split(" ").toTypedArray())
            val result = BufferedReader(InputStreamReader(process.inputStream)).readText().trim()
            process.waitFor()
            result
        } catch (_: Throwable) { "" }
    }

    // ── Helpers ──

    private fun getAppId(): String = try { com.kangqi.hic.preview.BuildConfig.APPLICATION_ID } catch (_: Throwable) { "com.kangqi.hic.preview" }
    private fun getVersionName(): String = try { com.kangqi.hic.preview.BuildConfig.VERSION_NAME } catch (_: Throwable) { "1.0.0" }
    private fun getVersionCode(): Int = try { com.kangqi.hic.preview.BuildConfig.VERSION_CODE } catch (_: Throwable) { 1 }

    private fun batteryHealthString(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Unknown ($health)"
    }
}
