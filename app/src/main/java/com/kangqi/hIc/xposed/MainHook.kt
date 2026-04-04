package com.kangqi.hIc.xposed

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONArray
import org.json.JSONObject

/**
 * Xposed entry point — self-hook + SystemUI hook.
 *
 * Key enhancements over basic approach:
 * 1. Package spoofing: can disguise notification as coming from any app (navigation, music, etc.)
 *    so HyperOS island system renders the correct built-in template for that business type.
 * 2. Custom icon: supports user-provided bitmap for island icon, not just app icon.
 * 3. Full miui.focus.param JSON with all HyperOS 3 template fields.
 */
class MainHook : IXposedHookLoadPackage {

    companion object {
        private const val MY_PACKAGE = "com.kangqi.hic.preview"
        private const val SYSTEMUI_PACKAGE = "com.android.systemui"
        private const val ACTION_SHOW = "com.kangqi.hIc.ACTION_SHOW_ISLAND"
        private const val ACTION_DISMISS = "com.kangqi.hIc.ACTION_DISMISS_ISLAND"
        private const val CHANNEL_ID = "hic_island_channel"
        private const val CUSTOM_ICON_MARKER = "hic_custom_icon"
        @Volatile @JvmStatic var pendingCustomIcon: Icon? = null
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            MY_PACKAGE -> hookSelf(lpparam)
            SYSTEMUI_PACKAGE -> hookSystemUI(lpparam)
        }
    }

    private fun hookSelf(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.kangqi.hIc.MainActivity", lpparam.classLoader, "isModuleActive",
                XC_MethodReplacement.returnConstant(true)
            )
            XposedBridge.log("[HIC] Self hook installed — isModuleActive() → true")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] Self hook failed: ${e.message}")
        }
    }

    private fun hookSystemUI(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.systemui.SystemUIApplication",
                lpparam.classLoader,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val ctx = param.thisObject as Context
                        registerIslandReceiver(ctx)
                    }
                }
            )
            XposedBridge.log("[HIC] SystemUI hook installed — waiting for onCreate")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] SystemUI hook failed: ${e.message}")
        }

        // Hook StatusBarNotification.getPackageName() to return spoofed package
        try {
            XposedHelpers.findAndHookMethod(
                "android.service.notification.StatusBarNotification",
                lpparam.classLoader,
                "getPackageName",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val sbn = param.thisObject
                            val notification = XposedHelpers.callMethod(sbn, "getNotification") as? Notification ?: return
                            val spoofPkg = notification.extras?.getString("miui.focus.pkg")
                            if (!spoofPkg.isNullOrBlank()) {
                                param.result = spoofPkg
                            }
                        } catch (_: Throwable) {}
                    }
                }
            )
            XposedBridge.log("[HIC] StatusBarNotification.getPackageName hook installed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] SBN.getPackageName hook failed: ${e.message}")
        }

        // Hook StatusBarNotification.getOpPkg() to return spoofed package
        try {
            XposedHelpers.findAndHookMethod(
                "android.service.notification.StatusBarNotification",
                lpparam.classLoader,
                "getOpPkg",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val sbn = param.thisObject
                            val notification = XposedHelpers.callMethod(sbn, "getNotification") as? Notification ?: return
                            val spoofPkg = notification.extras?.getString("miui.focus.pkg")
                            if (!spoofPkg.isNullOrBlank()) {
                                param.result = spoofPkg
                            }
                        } catch (_: Throwable) {}
                    }
                }
            )
            XposedBridge.log("[HIC] StatusBarNotification.getOpPkg hook installed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] SBN.getOpPkg hook failed: ${e.message}")
        }

        // Try to hook known HyperOS focus notification classes
        val focusCandidates = listOf(
            "com.android.systemui.statusbar.notification.focus.FocusNotificationManager",
            "com.android.systemui.statusbar.notification.MiuiFocusManager",
            "com.android.systemui.statusbar.notification.focus.FocusNotifHelper",
            "com.android.systemui.focusnotification.FocusNotificationManager",
            "com.miui.systemui.statusbar.notification.focus.FocusNotificationManager",
        )
        for (className in focusCandidates) {
            try {
                val clazz = XposedHelpers.findClass(className, lpparam.classLoader)
                // Hook all methods that have StatusBarNotification parameter
                for (method in clazz.declaredMethods) {
                    val paramTypes = method.parameterTypes
                    if (paramTypes.any { it.name.contains("StatusBarNotification") || it.name.contains("Notification") }) {
                        XposedBridge.hookMethod(method, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                // For any StatusBarNotification param, ensure our spoofed package is used
                                param.args.forEachIndexed { index, arg ->
                                    if (arg is Notification) {
                                        val spoofPkg = arg.extras?.getString("miui.focus.pkg")
                                        if (!spoofPkg.isNullOrBlank()) {
                                            // Set the pkg field via reflection for this notification
                                            try {
                                                val field = Notification::class.java.getDeclaredField("mPkg")
                                                field.isAccessible = true
                                                field.set(arg, spoofPkg)
                                            } catch (_: Throwable) {}
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
                XposedBridge.log("[HIC] Hooked focus class: $className")
                break // Successfully hooked one class, stop trying
            } catch (_: Throwable) {
                // Class not found, try next candidate
            }
        }

        // Hook Notification.getLargeIcon() to return custom icon when marked
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Notification",
                lpparam.classLoader,
                "getLargeIcon",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val notif = param.thisObject as Notification
                            if (notif.extras?.getBoolean(CUSTOM_ICON_MARKER, false) == true) {
                                pendingCustomIcon?.let { param.result = it }
                            }
                        } catch (_: Throwable) {}
                    }
                }
            )
            XposedBridge.log("[HIC] Notification.getLargeIcon hook installed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] getLargeIcon hook failed: ${e.message}")
        }

        // Hook Notification.getSmallIcon() to return spoofed package's icon
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Notification",
                lpparam.classLoader,
                "getSmallIcon",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val notif = param.thisObject as Notification
                            val spoofPkg = notif.extras?.getString("miui.focus.pkg")
                            if (!spoofPkg.isNullOrBlank()) {
                                val ctx = de.robv.android.xposed.XposedHelpers.callStaticMethod(
                                    Class.forName("android.app.ActivityThread"),
                                    "currentApplication"
                                ) as? Context ?: return
                                val appInfo = ctx.packageManager.getApplicationInfo(spoofPkg, 0)
                                if (appInfo.icon != 0) {
                                    param.result = Icon.createWithResource(spoofPkg, appInfo.icon)
                                }
                            }
                        } catch (_: Throwable) {}
                    }
                }
            )
            XposedBridge.log("[HIC] Notification.getSmallIcon hook installed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] getSmallIcon hook failed: ${e.message}")
        }

        // Hook StatusBarNotification.getUid() to return spoofed package UID
        try {
            XposedHelpers.findAndHookMethod(
                "android.service.notification.StatusBarNotification",
                lpparam.classLoader,
                "getUid",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val sbn = param.thisObject
                            val notification = XposedHelpers.callMethod(sbn, "getNotification") as? Notification ?: return
                            val spoofPkg = notification.extras?.getString("miui.focus.pkg")
                            if (!spoofPkg.isNullOrBlank()) {
                                val ctx = de.robv.android.xposed.XposedHelpers.callStaticMethod(
                                    Class.forName("android.app.ActivityThread"),
                                    "currentApplication"
                                ) as? Context ?: return
                                val appInfo = ctx.packageManager.getApplicationInfo(spoofPkg, 0)
                                param.result = appInfo.uid
                            }
                        } catch (_: Throwable) {}
                    }
                }
            )
            XposedBridge.log("[HIC] StatusBarNotification.getUid hook installed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] SBN.getUid hook failed: ${e.message}")
        }
    }

    private fun registerIslandReceiver(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    ACTION_SHOW -> handleShowIsland(ctx, intent)
                    ACTION_DISMISS -> handleDismissIsland(ctx)
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(ACTION_SHOW)
            addAction(ACTION_DISMISS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        XposedBridge.log("[HIC] Island receiver registered in SystemUI process")
    }

    // ─── Island Trigger ───────────────────────────────────────────────────

    private fun handleShowIsland(ctx: Context, intent: Intent) {
        try {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // ── Read all extras ──
            val expMain = intent.getStringExtra("expMain") ?: ""
            val expSub = intent.getStringExtra("expSub") ?: ""
            val capMain = intent.getStringExtra("capMain") ?: ""
            val capSub = intent.getStringExtra("capSub") ?: ""
            val frontTitle = intent.getStringExtra("frontTitle") ?: ""
            val aodTitle = intent.getStringExtra("aodTitle") ?: ""

            val rightType = intent.getIntExtra("rightType", 2)
            val rightText = intent.getStringExtra("rightText") ?: ""
            val leftType = intent.getIntExtra("leftType", 1)

            val borderColor = intent.getStringExtra("borderColor") ?: ""
            val highlightColor = intent.getStringExtra("highlightColor") ?: ""
            val emphasisColor = intent.getStringExtra("emphasisColor") ?: ""
            val islandBackgroundColor = intent.getStringExtra("islandBackgroundColor") ?: ""
            val textColor = intent.getStringExtra("textColor") ?: ""
            val contentColor = intent.getStringExtra("contentColor") ?: ""

            val isPersistent = intent.getBooleanExtra("isPersistent", false)
            val isShowNotification = intent.getBooleanExtra("isShowNotification", true)
            val timeout = intent.getIntExtra("timeout", 5)
            val islandFirstFloat = intent.getBooleanExtra("islandFirstFloat", true)
            val enableFloat = intent.getBooleanExtra("enableFloat", false)
            val templateType = intent.getIntExtra("templateType", 1)
            val business = intent.getStringExtra("business") ?: "custom"
            val islandProperty = intent.getIntExtra("islandProperty", 1)
            val substName = intent.getStringExtra("substName") ?: "HyperIsland Custom"

            // Package spoofing
            val spoofPackage = intent.getStringExtra("spoofPackage") ?: ""

            // Progress
            val showProgress = intent.getBooleanExtra("showProgress", false)
            val progress = intent.getIntExtra("progress", 0)
            val maxProgress = intent.getIntExtra("maxProgress", 100)
            val progressColor = intent.getStringExtra("progressColor") ?: ""
            val progressType = intent.getIntExtra("progressType", 1)
            val progressGradientStart = intent.getStringExtra("progressGradientStart") ?: ""
            val progressGradientEnd = intent.getStringExtra("progressGradientEnd") ?: ""
            val progressNodeCount = intent.getIntExtra("progressNodeCount", 0)

            // Timer
            val showTimer = intent.getBooleanExtra("showTimer", false)
            val timerDurationMs = intent.getLongExtra("timerDurationMs", 0)

            // Buttons
            val showButton = intent.getBooleanExtra("showButton", false)
            val buttonType = intent.getIntExtra("buttonType", 1)
            val buttonCount = intent.getIntExtra("buttonCount", 1)
            val buttonText1 = intent.getStringExtra("buttonText1") ?: ""
            val buttonText2 = intent.getStringExtra("buttonText2") ?: ""
            val buttonText3 = intent.getStringExtra("buttonText3") ?: ""
            val buttonColor = intent.getStringExtra("buttonColor") ?: ""
            val buttonTextColor = intent.getStringExtra("buttonTextColor") ?: ""
            val showButtonProgress = intent.getBooleanExtra("showButtonProgress", false)
            val buttonProgressValue = intent.getIntExtra("buttonProgressValue", 0)

            // Small island
            val smallIslandType = intent.getIntExtra("smallIslandType", 0)
            val showSmallIslandProgress = intent.getBooleanExtra("showSmallIslandProgress", false)

            // Rich text
            val useHighLight = intent.getBooleanExtra("useHighLight", true)
            val delimiterVisible = intent.getBooleanExtra("delimiterVisible", false)
            val showCoverImage = intent.getBooleanExtra("showCoverImage", false)

            // Custom image bitmap
            val customBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("customImage", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("customImage") as? Bitmap
            }

            // ══════════════════════════════════════════════════════════════
            // Build miui.focus.param JSON
            // ══════════════════════════════════════════════════════════════
            val root = JSONObject()
            val paramV2 = JSONObject()

            paramV2.put("enableFloat", enableFloat)
            paramV2.put("islandFirstFloat", islandFirstFloat)
            paramV2.put("isShowNotification", isShowNotification)
            if (templateType > 0) paramV2.put("templateType", templateType)
            if (business.isNotBlank()) paramV2.put("business", business)

            val tickerStr = if (capSub.isNotBlank()) "$capMain $capSub" else capMain
            paramV2.put("ticker", tickerStr.ifEmpty { " " })
            paramV2.put("tickerPic", "miui.focus.pic_custom_icon")

            if (aodTitle.isNotBlank()) paramV2.put("aodTitle", aodTitle)

            val baseInfo = JSONObject()
            baseInfo.put("type", 1)
            baseInfo.put("title", expMain.ifEmpty { " " })
            if (expSub.isNotBlank()) baseInfo.put("content", expSub)
            if (frontTitle.isNotBlank()) baseInfo.put("frontTitle", frontTitle)
            paramV2.put("baseInfo", baseInfo)

            if (showTimer && timerDurationMs > 0) {
                paramV2.put("countDownDuration", timerDurationMs)
            }

            // ── param_island ──
            val paramIsland = JSONObject()
            paramIsland.put("islandProperty", islandProperty)
            paramIsland.put("islandTimeout", if (isPersistent) 0 else timeout)
            if (highlightColor.isNotBlank()) paramIsland.put("highlightColor", highlightColor)
            if (borderColor.isNotBlank() && borderColor != highlightColor) {
                paramIsland.put("borderColor", borderColor)
            }
            if (emphasisColor.isNotBlank()) paramIsland.put("emphasisColor", emphasisColor)
            if (islandBackgroundColor.isNotBlank()) paramIsland.put("backgroundColor", islandBackgroundColor)

            // ── bigIslandArea ──
            val bigIslandArea = JSONObject()

            val imageTextInfoLeft = JSONObject()
            imageTextInfoLeft.put("type", leftType)

            val bigPicInfo = JSONObject()
            bigPicInfo.put("type", 1)
            bigPicInfo.put("pic", "miui.focus.pic_custom_icon")
            imageTextInfoLeft.put("picInfo", bigPicInfo)

            val bigTextInfo = JSONObject()
            bigTextInfo.put("title", expMain.ifEmpty { " " })
            if (expSub.isNotBlank()) bigTextInfo.put("content", expSub)
            if (frontTitle.isNotBlank()) bigTextInfo.put("frontTitle", frontTitle)
            bigTextInfo.put("useHighLight", useHighLight)
            if (delimiterVisible) bigTextInfo.put("delimiterVisible", true)
            if (textColor.isNotBlank()) bigTextInfo.put("titleColor", textColor)
            if (contentColor.isNotBlank()) bigTextInfo.put("contentColor", contentColor)

            if (emphasisColor.isNotBlank()) {
                val emphasisInfo = JSONObject()
                emphasisInfo.put("color", emphasisColor)
                bigTextInfo.put("emphasisInfo", emphasisInfo)
            }
            imageTextInfoLeft.put("textInfo", bigTextInfo)
            bigIslandArea.put("imageTextInfoLeft", imageTextInfoLeft)

            if (rightType > 0 && (rightText.isNotBlank() || showProgress || showCoverImage)) {
                val imageTextInfoRight = JSONObject()
                imageTextInfoRight.put("type", rightType)

                if (rightText.isNotBlank()) {
                    val rightTextInfo = JSONObject()
                    rightTextInfo.put("title", rightText)
                    if (textColor.isNotBlank()) rightTextInfo.put("titleColor", textColor)
                    imageTextInfoRight.put("textInfo", rightTextInfo)
                }

                if (showProgress && rightType == 3) {
                    val pInfo = JSONObject()
                    pInfo.put("progress", progress)
                    pInfo.put("maxProgress", maxProgress)
                    if (progressColor.isNotBlank()) pInfo.put("color", progressColor)
                    imageTextInfoRight.put("progressInfo", pInfo)
                }

                if (showCoverImage && rightType == 6) {
                    val coverPicInfo = JSONObject()
                    coverPicInfo.put("type", 1)
                    coverPicInfo.put("pic", "miui.focus.pic_custom_icon")
                    imageTextInfoRight.put("picInfo", coverPicInfo)
                }

                bigIslandArea.put("imageTextInfoRight", imageTextInfoRight)
            }

            if (showProgress && rightType != 3) {
                val progressArea = JSONObject()
                progressArea.put("type", progressType)
                progressArea.put("progress", progress)
                progressArea.put("maxProgress", maxProgress)
                if (progressColor.isNotBlank()) progressArea.put("color", progressColor)
                if (progressGradientStart.isNotBlank() && progressGradientEnd.isNotBlank()) {
                    progressArea.put("gradientStartColor", progressGradientStart)
                    progressArea.put("gradientEndColor", progressGradientEnd)
                }
                if (progressType == 3 && progressNodeCount > 0) {
                    progressArea.put("nodeCount", progressNodeCount)
                }
                bigIslandArea.put("progressArea", progressArea)
            }

            if (showButton && buttonText1.isNotBlank()) {
                val buttonArea = JSONObject()
                buttonArea.put("type", buttonType)

                val buttons = JSONArray()
                val btn1 = JSONObject()
                btn1.put("text", buttonText1)
                if (buttonColor.isNotBlank()) btn1.put("color", buttonColor)
                if (buttonTextColor.isNotBlank()) btn1.put("textColor", buttonTextColor)
                if (showButtonProgress) {
                    btn1.put("showProgress", true)
                    btn1.put("progressValue", buttonProgressValue)
                }
                buttons.put(btn1)

                if (buttonCount >= 2 && buttonText2.isNotBlank()) {
                    val btn2 = JSONObject()
                    btn2.put("text", buttonText2)
                    if (buttonColor.isNotBlank()) btn2.put("color", buttonColor)
                    if (buttonTextColor.isNotBlank()) btn2.put("textColor", buttonTextColor)
                    buttons.put(btn2)
                }
                if (buttonCount >= 3 && buttonText3.isNotBlank()) {
                    val btn3 = JSONObject()
                    btn3.put("text", buttonText3)
                    if (buttonColor.isNotBlank()) btn3.put("color", buttonColor)
                    if (buttonTextColor.isNotBlank()) btn3.put("textColor", buttonTextColor)
                    buttons.put(btn3)
                }

                buttonArea.put("buttons", buttons)
                bigIslandArea.put("buttonArea", buttonArea)
            }

            paramIsland.put("bigIslandArea", bigIslandArea)

            val smallIslandArea = JSONObject()
            smallIslandArea.put("type", smallIslandType)
            if (showSmallIslandProgress) smallIslandArea.put("showProgress", true)
            paramIsland.put("smallIslandArea", smallIslandArea)

            paramV2.put("param_island", paramIsland)
            root.put("param_v2", paramV2)

            val islandParams = root.toString()

            // ══════════════════════════════════════════════════════════════
            // Build icon — support custom user bitmap
            // ══════════════════════════════════════════════════════════════
            var finalIcon: Icon
            var smallIconResId: Int

            if (customBitmap != null) {
                finalIcon = Icon.createWithBitmap(customBitmap)
                smallIconResId = android.R.drawable.sym_def_app_icon
            } else if (spoofPackage.isNotBlank()) {
                // Try to load spoofed app's icon
                try {
                    val spoofAppInfo = ctx.packageManager.getApplicationInfo(spoofPackage, 0)
                    val spoofIconResId = if (spoofAppInfo.icon != 0) spoofAppInfo.icon else android.R.drawable.sym_def_app_icon
                    finalIcon = Icon.createWithResource(spoofPackage, spoofIconResId)
                    smallIconResId = spoofIconResId
                } catch (_: Throwable) {
                    val appInfo = ctx.packageManager.getApplicationInfo(MY_PACKAGE, 0)
                    val resId = if (appInfo.icon != 0) appInfo.icon else android.R.drawable.sym_def_app_icon
                    finalIcon = Icon.createWithResource(MY_PACKAGE, resId)
                    smallIconResId = resId
                }
            } else {
                val appInfo = ctx.packageManager.getApplicationInfo(MY_PACKAGE, 0)
                val resId = if (appInfo.icon != 0) appInfo.icon else android.R.drawable.sym_def_app_icon
                finalIcon = Icon.createWithResource(MY_PACKAGE, resId)
                smallIconResId = resId
            }

            // Custom icon marker and caching for getLargeIcon hook
            if (customBitmap != null) {
                pendingCustomIcon = finalIcon
                // Clear after 10 seconds
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    pendingCustomIcon = null
                }, 10000)
            }

            val picsBundle = Bundle()
            picsBundle.putParcelable("miui.focus.pic_custom_icon", finalIcon)

            val masterBundle = Bundle()
            masterBundle.putString("miui.focus.param", islandParams)
            masterBundle.putBundle("miui.focus.pics", picsBundle)
            masterBundle.putString("android.substName", substName)

            // Custom icon marker for the getLargeIcon hook
            if (customBitmap != null) {
                masterBundle.putBoolean(CUSTOM_ICON_MARKER, true)
            }

            // ══════════════════════════════════════════════════════════════
            // Always set spoof package in extras for the SBN hook to read
            // ══════════════════════════════════════════════════════════════
            if (spoofPackage.isNotBlank()) {
                masterBundle.putString("miui.focus.pkg", spoofPackage)
                masterBundle.putString("android.appInfo.packageName", spoofPackage)
            }

            // Ensure channel exists
            val channel = NotificationChannel(
                CHANNEL_ID, "超级岛专用", NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)

            // Build and post notification
            val notification = Notification.Builder(ctx, CHANNEL_ID)
                .setContentTitle(expMain)
                .setContentText(expSub.ifEmpty { rightText })
                .setSmallIcon(smallIconResId)
                .setLargeIcon(finalIcon)
                .setTicker(tickerStr)
                .addExtras(masterBundle)
                .apply {
                    if (!isPersistent) {
                        setTimeoutAfter((timeout * 1000 + 2000).toLong())
                    }
                }
                .build()

            nm.notify(System.currentTimeMillis().toInt(), notification)

            XposedBridge.log("[HIC] Island posted: expMain=$expMain, template=$templateType, business=$business, spoof=$spoofPackage")

        } catch (e: Throwable) {
            XposedBridge.log("[HIC] Island trigger error: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun handleDismissIsland(ctx: Context) {
        try {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancelAll()
            XposedBridge.log("[HIC] Island dismissed")
        } catch (e: Throwable) {
            XposedBridge.log("[HIC] Dismiss error: ${e.message}")
        }
    }
}
