package com.kangqi.hIc.service

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.kangqi.hIc.log.HicLog
import com.kangqi.hIc.model.IslandConfig
import com.kangqi.hIc.model.IslandTemplate
import org.json.JSONArray
import org.json.JSONObject

class IslandManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("island_settings", Context.MODE_PRIVATE)

    /**
     * Send broadcast to the injected receiver inside SystemUI.
     * Passes all IslandConfig fields as intent extras for maximum customization.
     */
    fun showIsland(config: IslandConfig) {
        val intent = Intent("com.kangqi.hIc.ACTION_SHOW_ISLAND").apply {
            // Content
            putExtra("expMain", config.title)
            putExtra("expSub", config.content)
            putExtra("capMain", config.smallIslandText.ifEmpty { config.title })
            putExtra("capSub", "")
            putExtra("frontTitle", config.frontTitle)
            putExtra("aodTitle", config.aodTitle)

            // Layout
            putExtra("leftType", config.leftComponentType)
            putExtra("rightType", config.rightComponentType)
            putExtra("rightText", config.rightText.ifEmpty { config.content.ifEmpty { config.title } })
            putExtra("showCoverImage", config.showCoverImage)

            // Colors
            putExtra("highlightColor", config.highlightColor)
            putExtra("borderColor", config.borderColor)
            putExtra("emphasisColor", config.emphasisColor)
            putExtra("islandBackgroundColor", config.islandBackgroundColor)
            putExtra("textColor", config.textColor)
            putExtra("contentColor", config.contentColor)

            // Behavior
            putExtra("isPersistent", config.updatable)
            putExtra("isShowNotification", config.isShowNotification)
            putExtra("timeout", (config.duration / 1000).coerceAtLeast(1))
            putExtra("islandFirstFloat", config.islandFirstFloat)
            putExtra("enableFloat", config.enableFloat)
            putExtra("templateType", config.templateType)
            putExtra("business", config.business)
            putExtra("islandProperty", config.islandProperty)
            putExtra("substName", config.substName)

            // Progress
            putExtra("showProgress", config.showProgress)
            putExtra("progress", config.progress)
            putExtra("maxProgress", config.maxProgress)
            putExtra("progressColor", config.progressColor)
            putExtra("progressType", config.progressType)
            putExtra("progressGradientStart", config.progressGradientStart)
            putExtra("progressGradientEnd", config.progressGradientEnd)
            putExtra("progressNodeCount", config.progressNodeCount)

            // Timer
            putExtra("showTimer", config.showTimer)
            putExtra("timerDurationMs", config.timerDurationMs)

            // Buttons
            putExtra("showButton", config.showButton)
            putExtra("buttonType", config.buttonType)
            putExtra("buttonCount", config.buttonCount)
            putExtra("buttonText1", config.buttonText1)
            putExtra("buttonText2", config.buttonText2)
            putExtra("buttonText3", config.buttonText3)
            putExtra("buttonColor", config.buttonColor)
            putExtra("buttonTextColor", config.buttonTextColor)
            putExtra("showButtonProgress", config.showButtonProgress)
            putExtra("buttonProgressValue", config.buttonProgressValue)

            // Small island
            putExtra("smallIslandType", config.smallIslandType)
            putExtra("showSmallIslandProgress", config.showSmallIslandProgress)

            // Rich text
            putExtra("useHighLight", config.useHighLight)
            putExtra("delimiterVisible", config.delimiterVisible)

            // Package spoofing
            putExtra("spoofPackage", config.spoofPackage)

            // Custom image — load bitmap from URI, app icon, or default
            if (config.customImageUri.isNotBlank()) {
                val bitmap = loadBitmapFromUri(config.customImageUri)
                if (bitmap != null) {
                    putExtra("customImage", bitmap)
                }
            } else if (config.iconPackage.isNotBlank()) {
                val bitmap = loadAppIconBitmap(config.iconPackage)
                if (bitmap != null) {
                    putExtra("customImage", bitmap)
                }
            }
        }
        HicLog.island("IslandManager", "showIsland: title=${config.title}, business=${config.business}, template=${config.templateType}")
        context.sendBroadcast(intent)
    }

    private fun loadAppIconBitmap(packageName: String): Bitmap? {
        return try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            val w = drawable.intrinsicWidth.coerceAtLeast(1)
            val h = drawable.intrinsicHeight.coerceAtLeast(1)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bmp)
            drawable.setBounds(0, 0, w, h)
            drawable.draw(canvas)
            val maxSize = 256
            if (bmp.width > maxSize || bmp.height > maxSize) {
                val ratio = minOf(maxSize.toFloat() / bmp.width, maxSize.toFloat() / bmp.height)
                Bitmap.createScaledBitmap(bmp, (bmp.width * ratio).toInt(), (bmp.height * ratio).toInt(), true)
            } else bmp
        } catch (_: Throwable) { null }
    }

    private fun loadBitmapFromUri(uriString: String): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val original = BitmapFactory.decodeStream(stream)
                if (original != null) {
                    // Scale to reasonable size for notification icon (max 256x256)
                    val maxSize = 256
                    if (original.width > maxSize || original.height > maxSize) {
                        val ratio = minOf(maxSize.toFloat() / original.width, maxSize.toFloat() / original.height)
                        val w = (original.width * ratio).toInt()
                        val h = (original.height * ratio).toInt()
                        Bitmap.createScaledBitmap(original, w, h, true)
                    } else {
                        original
                    }
                } else null
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun dismissIsland() {
        HicLog.island("IslandManager", "dismissIsland")
        val intent = Intent("com.kangqi.hIc.ACTION_DISMISS_ISLAND")
        context.sendBroadcast(intent)
    }

    // --- Settings ---

    var isEnabled: Boolean
        get() = prefs.getBoolean("enabled", true)
        set(value) = prefs.edit().putBoolean("enabled", value).apply()

    var duration: Int
        get() = prefs.getInt("duration", 5000)
        set(value) = prefs.edit().putInt("duration", value).apply()

    var highlightColor: String
        get() = prefs.getString("highlightColor", "#3482FF") ?: "#3482FF"
        set(value) = prefs.edit().putString("highlightColor", value).apply()

    var borderColor: String
        get() = prefs.getString("borderColor", "#FFFFFF") ?: "#FFFFFF"
        set(value) = prefs.edit().putString("borderColor", value).apply()

    // --- Whitelist ---

    var whitelist: Set<String>
        get() = prefs.getString("whitelist", "")
            ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
        set(value) = prefs.edit().putString("whitelist", value.joinToString(",")).apply()

    fun addToWhitelist(packageName: String) {
        HicLog.i("IslandManager", "addToWhitelist: $packageName")
        whitelist = whitelist + packageName
    }
    fun removeFromWhitelist(packageName: String) {
        HicLog.i("IslandManager", "removeFromWhitelist: $packageName")
        whitelist = whitelist - packageName
    }

    // --- Templates ---

    fun getTemplates(): List<IslandTemplate> {
        val json = prefs.getString("templates", "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                IslandTemplate(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    title = obj.optString("title", ""),
                    content = obj.optString("content", ""),
                    duration = obj.optInt("duration", 5000),
                    highlightColor = obj.optString("highlightColor", "#3482FF"),
                    borderColor = obj.optString("borderColor", "#FFFFFF"),
                    showProgress = obj.optBoolean("showProgress", false),
                    showTimer = obj.optBoolean("showTimer", false),
                    timerDurationMs = obj.optLong("timerDurationMs", 0),
                    templateType = obj.optInt("templateType", 1),
                    business = obj.optString("business", "custom"),
                    icon = if (obj.has("icon")) obj.optString("icon") else null,
                    showButton = obj.optBoolean("showButton", false),
                    buttonText1 = obj.optString("buttonText1", ""),
                    buttonColor = obj.optString("buttonColor", "#3482FF"),
                    progressType = obj.optInt("progressType", 1),
                    rightComponentType = obj.optInt("rightComponentType", 2),
                    rightText = obj.optString("rightText", ""),
                    smallIslandType = obj.optInt("smallIslandType", 0),
                    emphasisColor = obj.optString("emphasisColor", "#3482FF"),
                )
            }
        } catch (_: Throwable) { emptyList() }
    }

    fun saveTemplate(template: IslandTemplate) {
        val templates = getTemplates().toMutableList()
        val index = templates.indexOfFirst { it.id == template.id }
        if (index >= 0) templates[index] = template else templates.add(template)
        saveTemplatesList(templates)
    }

    fun deleteTemplate(id: String) {
        saveTemplatesList(getTemplates().filter { it.id != id })
    }

    private fun saveTemplatesList(templates: List<IslandTemplate>) {
        val arr = JSONArray()
        templates.forEach { t ->
            arr.put(JSONObject().apply {
                put("id", t.id)
                put("name", t.name)
                put("title", t.title)
                put("content", t.content)
                put("duration", t.duration)
                put("highlightColor", t.highlightColor)
                put("borderColor", t.borderColor)
                put("showProgress", t.showProgress)
                put("showTimer", t.showTimer)
                put("timerDurationMs", t.timerDurationMs)
                put("templateType", t.templateType)
                put("business", t.business)
                t.icon?.let { put("icon", it) }
                put("showButton", t.showButton)
                put("buttonText1", t.buttonText1)
                put("buttonColor", t.buttonColor)
                put("progressType", t.progressType)
                put("rightComponentType", t.rightComponentType)
                put("rightText", t.rightText)
                put("smallIslandType", t.smallIslandType)
                put("emphasisColor", t.emphasisColor)
            })
        }
        prefs.edit().putString("templates", arr.toString()).apply()
    }
}
