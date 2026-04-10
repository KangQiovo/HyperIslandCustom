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
        val rootInfo = getRootInfo(context)
        sb.appendLine("  RootManager: ${rootInfo.name}")
        sb.appendLine("  RootVersion: ${rootInfo.version}")
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

    /**
     * Xposed framework API version from XposedBridge at runtime.
     * Set by the Xposed hook entry point when the module is loaded.
     * This is the actual framework API level (e.g. 93, 100), NOT the manager app version.
     */
    @Volatile
    var xposedApiVersion: Int = -1

    /**
     * Framework name detected by the Xposed hook at load time.
     * Set to "LSPosed" or "LSPatch" by MainHook.hookSelf() based on classloader inspection.
     * This is the most reliable signal — it tells us which framework actually loaded the module,
     * as opposed to which framework apps happen to be installed on the device.
     */
    @Volatile
    var hookDetectedFramework: String = ""

    /**
     * Normalize a PackageInfo version into "v<name> (<code>)" format without duplication.
     * Strips ALL pre-existing leading "v" characters and any "(...)" block from versionName,
     * so that callers always get a single canonical format regardless of how the APK was built.
     *
     * Root managers like SukiSU/KernelSU sometimes embed versionCode in versionName
     * (e.g. "v1.9.2(7467)" or even "vv1.9.2(7467)"), which would otherwise double-format.
     */
    private fun formatPkgVersion(versionName: String?, versionCode: Long): String {
        if (versionName.isNullOrBlank()) return "v? ($versionCode)"
        var clean = versionName.trim()
        // Strip ALL leading "v" characters (e.g. "vv1.9.2" → "1.9.2")
        while (clean.startsWith("v", ignoreCase = true)) {
            clean = clean.substring(1).trimStart()
        }
        // Strip everything from the first "(" onward (e.g. "1.9.2(7467)" → "1.9.2")
        val parenIdx = clean.indexOf('(')
        if (parenIdx >= 0) {
            clean = clean.substring(0, parenIdx).trim()
        }
        if (clean.isBlank()) clean = "?"
        return "v$clean ($versionCode)"
    }

    /**
     * Normalize a versionName (without a separate versionCode) — strips ALL leading "v"s
     * and any trailing "(...)" block, then re-prepends a single "v".
     */
    private fun formatPkgVersionName(versionName: String?): String {
        if (versionName.isNullOrBlank()) return "Unknown"
        var clean = versionName.trim()
        while (clean.startsWith("v", ignoreCase = true)) {
            clean = clean.substring(1).trimStart()
        }
        val parenIdx = clean.indexOf('(')
        if (parenIdx >= 0) {
            clean = clean.substring(0, parenIdx).trim()
        }
        if (clean.isBlank()) return "Unknown"
        return "v$clean"
    }

    @Suppress("DEPRECATION")
    fun getFrameworkInfo(context: Context): FrameworkInfo {
        val pm = context.packageManager
        var frameworkName = "Unknown"
        var frameworkVersion = "Unknown"
        var managerName = "Unknown"
        var managerVersion = "Unknown"

        // 1. Detect Xposed framework API version (set by hook entry or system prop)
        val apiVersion = if (xposedApiVersion > 0) xposedApiVersion else {
            val prop = getProp("ro.xposed.version")
            if (prop.isNotEmpty()) prop.toIntOrNull() ?: -1 else -1
        }

        // 2. Use hook-detected framework as the primary signal.
        //    The hook checks for LSPatch-specific classes at load time, so this tells us
        //    which framework ACTUALLY loaded the module — not just which apps are installed.
        val hookFramework = hookDetectedFramework

        if (hookFramework == "LSPosed") {
            // Module is loaded by LSPosed — try to get version details
            frameworkName = "LSPosed"
            frameworkVersion = if (apiVersion > 0) "API $apiVersion" else "Detected"

            // 2a. Try root-based detection for detailed version info
            val lspdDetected = detectLsposedViaRoot()
            if (lspdDetected != null) {
                if (frameworkVersion == "Detected") frameworkVersion = lspdDetected.second
                // Try to find the manager app for display version
                val lsposedManagerPackages = listOf(
                    "org.lsposed.manager",
                    "io.github.lsposed.manager"
                )
                for (pkg in lsposedManagerPackages) {
                    try {
                        val pi = pm.getPackageInfo(pkg, 0)
                        managerName = "${lspdDetected.first} Manager"
                        managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                        return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
                    } catch (_: Throwable) {}
                }
                managerName = lspdDetected.first
                managerVersion = lspdDetected.second
                return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
            }

            // 2b. Fallback: try LSPosed Manager app for version info
            val lsposedManagerPackages = listOf(
                "org.lsposed.manager",
                "io.github.lsposed.manager"
            )
            for (pkg in lsposedManagerPackages) {
                try {
                    val pi = pm.getPackageInfo(pkg, 0)
                    managerName = "LSPosed Manager"
                    managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                    return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
                } catch (_: Throwable) {}
            }

            // 2c. No manager found — return with what we have (API version from hook)
            return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
        }

        if (hookFramework == "LSPatch") {
            // Module is actually loaded by LSPatch
            frameworkName = "LSPatch"
            frameworkVersion = if (apiVersion > 0) "API $apiVersion" else "Detected"
            val lspatchPackages = listOf(
                "org.lsposed.lspatch",
                "io.github.lsposed.lspatch"
            )
            for (pkg in lspatchPackages) {
                try {
                    val pi = pm.getPackageInfo(pkg, 0)
                    managerName = "LSPatch Manager"
                    managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                    return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
                } catch (_: Throwable) {}
            }
            return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
        }

        // 3. Hook didn't report framework (module not active) — fall back to package scanning
        //    Try LSPosed via root first
        val lspdDetected = detectLsposedViaRoot()
        if (lspdDetected != null) {
            frameworkName = lspdDetected.first
            frameworkVersion = if (apiVersion > 0) "API $apiVersion" else lspdDetected.second
            val lsposedManagerPackages = listOf(
                "org.lsposed.manager",
                "io.github.lsposed.manager"
            )
            for (pkg in lsposedManagerPackages) {
                try {
                    val pi = pm.getPackageInfo(pkg, 0)
                    managerName = "${lspdDetected.first} Manager"
                    managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                    return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
                } catch (_: Throwable) {}
            }
            managerName = lspdDetected.first
            managerVersion = lspdDetected.second
            return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
        }

        // 4. Try LSPosed Manager app
        val lsposedManagerPackages = listOf(
            "org.lsposed.manager",
            "io.github.lsposed.manager"
        )
        for (pkg in lsposedManagerPackages) {
            try {
                val pi = pm.getPackageInfo(pkg, 0)
                frameworkName = "LSPosed"
                frameworkVersion = if (apiVersion > 0) "API $apiVersion" else formatPkgVersionName(pi.versionName)
                managerName = "LSPosed Manager"
                managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
            } catch (_: Throwable) {}
        }

        // 5. Try LSPatch Manager (only when hook didn't detect anything — module not active)
        val lspatchPackages = listOf(
            "org.lsposed.lspatch",
            "io.github.lsposed.lspatch"
        )
        for (pkg in lspatchPackages) {
            try {
                val pi = pm.getPackageInfo(pkg, 0)
                frameworkName = "LSPatch"
                frameworkVersion = if (apiVersion > 0) "API $apiVersion" else formatPkgVersionName(pi.versionName)
                managerName = "LSPatch Manager"
                managerVersion = formatPkgVersion(pi.versionName, pi.longVersionCode)
                return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
            } catch (_: Throwable) {}
        }

        // 6. Fallback: check system props for older Xposed frameworks
        if (apiVersion > 0) {
            frameworkName = "Xposed"
            frameworkVersion = "API $apiVersion"
        }

        return FrameworkInfo(frameworkName, frameworkVersion, managerName, managerVersion)
    }

    /**
     * Detect LSPosed installation via root by checking /data/adb/lspd/ directory.
     * Returns Pair(name, version) or null if not found.
     */
    private fun detectLsposedViaRoot(): Pair<String, String>? {
        return try {
            // Check if LSPosed directory exists
            val checkProcess = Runtime.getRuntime().exec(
                arrayOf("su", "-c", "ls /data/adb/lspd/ 2>/dev/null")
            )
            val dirOutput = BufferedReader(InputStreamReader(checkProcess.inputStream)).readText().trim()
            checkProcess.waitFor()
            if (dirOutput.isEmpty()) return null

            // Try to read LSPosed version from multiple sources
            var name = "LSPosed"
            var version = "Detected"

            // Try reading the module prop file for version info
            val propPaths = listOf(
                "/data/adb/modules/zygisk_lsposed/module.prop",
                "/data/adb/modules/riru_lsposed/module.prop",
                "/data/adb/modules/lsposed/module.prop"
            )
            for (propPath in propPaths) {
                try {
                    val catProcess = Runtime.getRuntime().exec(
                        arrayOf("su", "-c", "cat $propPath 2>/dev/null")
                    )
                    val propContent = BufferedReader(InputStreamReader(catProcess.inputStream)).readText().trim()
                    catProcess.waitFor()
                    if (propContent.isBlank()) continue

                    // Parse module.prop: id=..., name=..., version=..., versionCode=...
                    val props = propContent.lines().associate { line ->
                        val parts = line.split("=", limit = 2)
                        if (parts.size == 2) parts[0].trim() to parts[1].trim() else "" to ""
                    }
                    val moduleName = props["name"]
                    val moduleVersion = props["version"]
                    val moduleVersionCode = props["versionCode"]

                    if (!moduleName.isNullOrBlank()) name = moduleName
                    if (!moduleVersion.isNullOrBlank()) {
                        var cleanVer = moduleVersion.trimStart('v', 'V')
                        val pIdx = cleanVer.indexOf('(')
                        if (pIdx >= 0) cleanVer = cleanVer.substring(0, pIdx).trim()
                        version = if (!moduleVersionCode.isNullOrBlank()) {
                            "v$cleanVer ($moduleVersionCode)"
                        } else {
                            "v$cleanVer"
                        }
                    }
                    break
                } catch (_: Throwable) {}
            }

            // If no module.prop found, try listing modules dir to find the actual LSPosed module name
            if (version == "Detected") {
                try {
                    val lsProcess = Runtime.getRuntime().exec(
                        arrayOf("su", "-c", "ls -1 /data/adb/modules/ 2>/dev/null")
                    )
                    val modules = BufferedReader(InputStreamReader(lsProcess.inputStream))
                        .readLines().filter { it.contains("lsposed", ignoreCase = true) }
                    lsProcess.waitFor()
                    if (modules.isNotEmpty()) {
                        val moduleName = modules.first()
                        // Read that module's prop
                        val catProcess = Runtime.getRuntime().exec(
                            arrayOf("su", "-c", "cat /data/adb/modules/$moduleName/module.prop 2>/dev/null")
                        )
                        val propContent = BufferedReader(InputStreamReader(catProcess.inputStream)).readText().trim()
                        catProcess.waitFor()
                        if (propContent.isNotBlank()) {
                            val props = propContent.lines().associate { line ->
                                val parts = line.split("=", limit = 2)
                                if (parts.size == 2) parts[0].trim() to parts[1].trim() else "" to ""
                            }
                            val mn = props["name"]
                            val mv = props["version"]
                            val mvc = props["versionCode"]
                            if (!mn.isNullOrBlank()) name = mn
                            if (!mv.isNullOrBlank()) {
                                var cleanMv = mv.trimStart('v', 'V')
                                val pIdx = cleanMv.indexOf('(')
                                if (pIdx >= 0) cleanMv = cleanMv.substring(0, pIdx).trim()
                                version = if (!mvc.isNullOrBlank()) "v$cleanMv ($mvc)" else "v$cleanMv"
                            }
                        }
                    }
                } catch (_: Throwable) {}
            }

            Pair(name, version)
        } catch (_: Throwable) {
            null
        }
    }

    // ── Root detection ──

    data class RootInfo(
        val name: String = "Unknown",
        val version: String = "Unknown"
    )

    /** Root manager app packages — checked via PackageManager (no root needed) */
    private val rootManagerPackages = listOf(
        // SukiSU (must check before KernelSU since it's a fork)
        "com.sukisu.ultra" to "SukiSU Ultra",
        "com.rifsxd.ksunext" to "SukiSU Ultra",
        // KernelSU and forks
        "me.weishu.kernelsu" to "KernelSU",
        // Magisk (various forks)
        "com.topjohnwu.magisk" to "Magisk",
        "io.github.vvb2060.magisk" to "Magisk Alpha",
        "io.github.huskydg.magisk" to "Magisk Delta",
        "io.github.backslashxx.magisk" to "Magisk Kitsune",
        // APatch
        "me.bmax.apatch" to "APatch",
        // SuperSU (legacy)
        "eu.chainfire.supersu" to "SuperSU",
    )

    @Suppress("DEPRECATION")
    fun getRootInfo(context: Context): RootInfo {
        val pm = context.packageManager

        // 1. Try PackageManager detection (works without root)
        for ((pkg, name) in rootManagerPackages) {
            try {
                val pi = pm.getPackageInfo(pkg, 0)
                return RootInfo(name, formatPkgVersion(pi.versionName, pi.longVersionCode))
            } catch (_: Throwable) {}
        }

        // 2. Fallback: check system properties
        // KernelSU / SukiSU
        val ksuVersion = getProp("ro.kernelsu.version")
        if (ksuVersion.isNotEmpty()) {
            val sukisuVersion = getProp("ro.sukisu.version")
            return if (sukisuVersion.isNotEmpty()) {
                RootInfo("SukiSU Ultra", sukisuVersion)
            } else {
                RootInfo("KernelSU", ksuVersion)
            }
        }
        // APatch
        val apatchVersion = getProp("ro.apatch.version")
        if (apatchVersion.isNotEmpty()) {
            return RootInfo("APatch", apatchVersion)
        }

        // 3. Fallback: file path checks (may fail without root access)
        return try {
            when {
                File("/data/adb/ap").exists() -> RootInfo("APatch", apatchVersion.ifBlank { "Detected" })
                File("/data/adb/ksud").exists() -> RootInfo("KernelSU", "Detected")
                File("/data/adb/magisk").exists() -> RootInfo("Magisk", "Detected")
                hasSuBinary() -> RootInfo("Root", "Detected")
                else -> RootInfo()
            }
        } catch (_: Throwable) { RootInfo() }
    }

    fun getRootManager(): String = "Unknown" // Legacy compat — use getRootInfo() instead

    /**
     * Root permission state for the current app.
     * - NOT_INSTALLED: no root manager package detected
     * - NOT_GRANTED: root manager installed but the app doesn't hold root permission
     * - GRANTED: `su -c id` returned uid=0 for this process
     */
    enum class RootState { NOT_INSTALLED, NOT_GRANTED, GRANTED }

    /** Cached root state — avoids red→green flash when navigating back to HomeScreen. */
    @Volatile
    var lastKnownRootState: RootState = RootState.NOT_GRANTED
        private set

    /**
     * Check whether this app currently holds root permission.
     * Runs `su -c id` synchronously; safe to call from a background thread.
     * Returns GRANTED only if the su binary exists AND the prompt has been accepted for this uid.
     */
    fun checkRootState(context: Context): RootState {
        // If no manager package at all, short-circuit.
        val info = getRootInfo(context)
        if (info.name == "Unknown") {
            // Still try `which su` as a last resort — some custom ROMs have su without a manager app.
            if (!hasSuBinary()) {
                lastKnownRootState = RootState.NOT_INSTALLED
                return RootState.NOT_INSTALLED
            }
        }
        val result = if (isRootGranted()) RootState.GRANTED else RootState.NOT_GRANTED
        lastKnownRootState = result
        return result
    }

    /**
     * Execute a harmless `id` command via `su` and check if we got uid=0.
     * This will trigger the root manager's permission dialog if not yet granted.
     */
    fun isRootGranted(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText().trim()
            process.waitFor()
            output.contains("uid=0")
        } catch (_: Throwable) {
            false
        }
    }

    private fun hasSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/bin/su", "/system/xbin/su",
            "/sbin/su", "/vendor/bin/su",
            "/system_ext/bin/su", "/product/bin/su"
        )
        return paths.any { File(it).exists() }
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

    private fun getAppId(): String = try { com.kangqi.hic.BuildConfig.APPLICATION_ID } catch (_: Throwable) { "com.kangqi.hic" }
    private fun getVersionName(): String = try { com.kangqi.hic.BuildConfig.VERSION_NAME } catch (_: Throwable) { "1.0.0" }
    private fun getVersionCode(): Int = try { com.kangqi.hic.BuildConfig.VERSION_CODE } catch (_: Throwable) { 1 }

    private fun batteryHealthString(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Unknown ($health)"
    }
}
