package com.kangqi.hIc.utils

import android.content.Context
import android.net.Uri
import com.kangqi.hIc.ui.theme.themeDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Export/Import/Reset module configuration.
 * Handles both SharedPreferences (island_settings) and DataStore (theme_prefs).
 */
object ConfigBackupHelper {

    private const val BACKUP_VERSION = 1
    private const val ISLAND_PREFS_NAME = "island_settings"

    /**
     * Export all config to a JSON file in cache dir. Returns the file.
     */
    suspend fun exportConfig(context: Context): File {
        val exportDir = File(context.cacheDir, "config_export").apply { mkdirs() }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(exportDir, "hic_config_$timestamp.json")

        val root = JSONObject()
        root.put("version", BACKUP_VERSION)
        root.put("app_version", try { com.kangqi.hic.BuildConfig.VERSION_NAME } catch (_: Throwable) { "1.0.0" })
        root.put("timestamp", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()))

        // Export island_settings (SharedPreferences)
        val islandPrefs = context.getSharedPreferences(ISLAND_PREFS_NAME, Context.MODE_PRIVATE)
        val islandJson = JSONObject()
        islandPrefs.all.forEach { (key, value) ->
            when (value) {
                is Boolean -> islandJson.put(key, value)
                is Int -> islandJson.put(key, value)
                is Long -> islandJson.put(key, value)
                is Float -> islandJson.put(key, value.toDouble())
                is String -> islandJson.put(key, value)
            }
        }
        root.put("island_settings", islandJson)

        // Export theme_prefs (DataStore)
        val themePrefs = context.themeDataStore.data.first()
        val themeJson = JSONObject()
        themePrefs.asMap().forEach { (key, value) ->
            when (value) {
                is Boolean -> themeJson.put(key.name, value)
                is Int -> themeJson.put(key.name, value)
                is Long -> themeJson.put(key.name, value)
                is Float -> themeJson.put(key.name, value.toDouble())
                is String -> themeJson.put(key.name, value)
            }
        }
        root.put("theme_prefs", themeJson)

        file.writeText(root.toString(2))
        return file
    }

    /**
     * Import config from a JSON file URI.
     * Returns a human-readable result message.
     */
    suspend fun importConfig(context: Context, uri: Uri): String {
        return try {
            val jsonText = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                ?: return "Failed: Cannot read file"
            val root = JSONObject(jsonText)

            val version = root.optInt("version", 0)
            if (version < 1) return "Failed: Invalid config file format"

            var restoredCount = 0

            // Restore island_settings
            if (root.has("island_settings")) {
                val islandJson = root.getJSONObject("island_settings")
                val editor = context.getSharedPreferences(ISLAND_PREFS_NAME, Context.MODE_PRIVATE).edit()
                editor.clear()
                islandJson.keys().forEach { key ->
                    val value = islandJson.get(key)
                    when (value) {
                        is Boolean -> editor.putBoolean(key, value)
                        is Int -> editor.putInt(key, value)
                        is Long -> editor.putLong(key, value)
                        is Double -> editor.putFloat(key, value.toFloat())
                        is String -> editor.putString(key, value)
                    }
                    restoredCount++
                }
                editor.apply()
            }

            // Restore theme_prefs
            if (root.has("theme_prefs")) {
                val themeJson = root.getJSONObject("theme_prefs")
                context.themeDataStore.edit { prefs ->
                    prefs.clear()
                    themeJson.keys().forEach { key ->
                        val value = themeJson.get(key)
                        when (value) {
                            is Boolean -> prefs[booleanPreferencesKey(key)] = value
                            is Int -> prefs[intPreferencesKey(key)] = value
                            is Long -> prefs[intPreferencesKey(key)] = value.toInt()
                            is Double -> prefs[floatPreferencesKey(key)] = value.toFloat()
                            is String -> prefs[stringPreferencesKey(key)] = value
                        }
                        restoredCount++
                    }
                }
            }

            "Success: Restored $restoredCount settings. Please restart the app."
        } catch (e: Exception) {
            "Failed: ${e.message}"
        }
    }

    /**
     * Reset all config to defaults.
     */
    suspend fun resetAllConfig(context: Context) {
        // Clear island_settings
        context.getSharedPreferences(ISLAND_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()

        // Clear theme_prefs (DataStore will use defaults on next load)
        context.themeDataStore.edit { it.clear() }
    }
}
