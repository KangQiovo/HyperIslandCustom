package com.kangqi.hIc.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

enum class AppTheme { MIUIX, MD3, LIQUID_GLASS }
enum class DockStyle { MIUIX, LIQUID_GLASS, MD3 }
enum class DarkMode { FOLLOW_SYSTEM, LIGHT, DARK }

class ThemeManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
        private val KEY_DOCK_STYLE = stringPreferencesKey("dock_style")
        private val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        private val KEY_MONET = booleanPreferencesKey("monet_enabled")
        private val KEY_GLASS_BAR_ALPHA = floatPreferencesKey("glass_bar_alpha")
        private val KEY_GLASS_SLIDER_ALPHA = floatPreferencesKey("glass_slider_alpha")
        private val KEY_PREDICTIVE_BACK = booleanPreferencesKey("predictive_back")
        private val KEY_PREFS_VERSION = intPreferencesKey("prefs_version")
        private const val CURRENT_PREFS_VERSION = 3
    }

    var appTheme by mutableStateOf(AppTheme.MIUIX)
        private set
    var dockStyle by mutableStateOf(DockStyle.LIQUID_GLASS)
        private set
    var darkMode by mutableStateOf(DarkMode.FOLLOW_SYSTEM)
        private set
    var monetEnabled by mutableStateOf(false)
        private set
    var glassBarAlpha by mutableFloatStateOf(1.0f)
        private set
    var glassSliderAlpha by mutableFloatStateOf(0.3f)
        private set
    var predictiveBackEnabled by mutableStateOf(true)
        private set

    suspend fun load() {
        val prefs = context.themeDataStore.data.first()
        val storedVersion = prefs[KEY_PREFS_VERSION] ?: 0

        // Migrate to current defaults: Miuix theme + Liquid Glass dock + glassBar 100% + glassSlider 30%
        if (storedVersion < CURRENT_PREFS_VERSION) {
            context.themeDataStore.edit {
                it[KEY_APP_THEME] = AppTheme.MIUIX.name
                it[KEY_DOCK_STYLE] = DockStyle.LIQUID_GLASS.name
                it[KEY_GLASS_BAR_ALPHA] = 1.0f
                it[KEY_GLASS_SLIDER_ALPHA] = 0.3f
                it[KEY_PREFS_VERSION] = CURRENT_PREFS_VERSION
            }
            appTheme = AppTheme.MIUIX
            dockStyle = DockStyle.LIQUID_GLASS
            glassBarAlpha = 1.0f
            glassSliderAlpha = 0.3f
        } else {
            appTheme = prefs[KEY_APP_THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.MIUIX
            dockStyle = prefs[KEY_DOCK_STYLE]?.let { runCatching { DockStyle.valueOf(it) }.getOrNull() } ?: DockStyle.LIQUID_GLASS
            glassBarAlpha = prefs[KEY_GLASS_BAR_ALPHA] ?: 1.0f
            glassSliderAlpha = prefs[KEY_GLASS_SLIDER_ALPHA] ?: 0.3f
        }

        darkMode = prefs[KEY_DARK_MODE]?.let { runCatching { DarkMode.valueOf(it) }.getOrNull() } ?: DarkMode.FOLLOW_SYSTEM
        monetEnabled = prefs[KEY_MONET] ?: false
        predictiveBackEnabled = prefs[KEY_PREDICTIVE_BACK] ?: true
    }

    fun updateAppTheme(theme: AppTheme) {
        appTheme = theme
        scope.launch { context.themeDataStore.edit { it[KEY_APP_THEME] = theme.name } }
    }

    fun updateDockStyle(style: DockStyle) {
        dockStyle = style
        scope.launch { context.themeDataStore.edit { it[KEY_DOCK_STYLE] = style.name } }
    }

    fun updateDarkMode(mode: DarkMode) {
        darkMode = mode
        scope.launch { context.themeDataStore.edit { it[KEY_DARK_MODE] = mode.name } }
    }

    fun updateMonetEnabled(enabled: Boolean) {
        monetEnabled = enabled
        scope.launch { context.themeDataStore.edit { it[KEY_MONET] = enabled } }
    }

    fun updateGlassBarAlpha(alpha: Float) {
        glassBarAlpha = alpha
        scope.launch { context.themeDataStore.edit { it[KEY_GLASS_BAR_ALPHA] = alpha } }
    }

    fun updateGlassSliderAlpha(alpha: Float) {
        glassSliderAlpha = alpha
        scope.launch { context.themeDataStore.edit { it[KEY_GLASS_SLIDER_ALPHA] = alpha } }
    }

    fun updatePredictiveBackEnabled(enabled: Boolean) {
        predictiveBackEnabled = enabled
        scope.launch { context.themeDataStore.edit { it[KEY_PREDICTIVE_BACK] = enabled } }
    }
}

val LocalThemeManager = compositionLocalOf<ThemeManager> { error("ThemeManager not provided") }
