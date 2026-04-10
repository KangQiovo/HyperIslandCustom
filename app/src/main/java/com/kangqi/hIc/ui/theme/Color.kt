package com.kangqi.hIc.ui.theme

import androidx.compose.ui.graphics.Color

// Module active state — used across all themes as a semantic color
val GreenAccent = Color(0xFF34C759)
val GreenCheck = Color(0xFF30D158)

// Warning level color — used in log viewer etc.
val WarningOrange = Color(0xFFFF9500)

/**
 * Semantic tokens for the Liquid Glass theme.
 * Centralizes all Glass-specific colors so no raw Color(0x...) literals leak into composables.
 */
object GlassTokens {
    // Accent (link / focus / interactive highlight)
    val AccentDark = Color(0xFF0091FF)
    val AccentLight = Color(0xFF0088FF)
    fun accent(isDark: Boolean) = if (isDark) AccentDark else AccentLight

    // Background base behind the Glass backdrop layer
    val BaseDark = Color(0xFF0A0A0A)
    val BaseLight = Color(0xFFF5F5F5)
    fun base(isDark: Boolean) = if (isDark) BaseDark else BaseLight

    // Dialog / elevated surface
    val SurfaceDark = Color(0xFF1C1C1E)
    val SurfaceLight = Color(0xFFF2F2F7)
    fun surface(isDark: Boolean) = if (isDark) SurfaceDark else SurfaceLight

    // Toggle green (on state)
    val ToggleGreenDark = Color(0xFF30D158)
    val ToggleGreenLight = Color(0xFF34C759)
    fun toggleGreen(isDark: Boolean) = if (isDark) ToggleGreenDark else ToggleGreenLight

    // Track / inactive tint
    val TrackDark = Color(0xFF787880)
    val TrackLight = Color(0xFF787878)
    fun trackColor(isDark: Boolean) =
        if (isDark) TrackDark.copy(alpha = 0.36f) else TrackLight.copy(alpha = 0.38f)

    // Thumb surface color (needs contrast against both light and dark backgrounds)
    fun thumbSurface(isDark: Boolean) = if (isDark) Color(0xFFE0E0E0) else Color(0xFFE8E8E8)

    // Thumb shadow (stronger in light mode for visibility against light bg)
    fun thumbShadowAlpha(isDark: Boolean) = if (isDark) 0.2f else 0.35f

    // Thumb border for light mode visibility
    val ThumbBorderLight = Color(0xFFCCCCCC)
}

/**
 * Semantic tokens specifically for the Liquid Glass bottom navigation bar.
 */
object GlassNavTokens {
    val AccentDark = Color(0xFF4DA6FF)
    val AccentLight = Color(0xFF0066CC)
    fun accent(isDark: Boolean) = if (isDark) AccentDark else AccentLight

    val SelectedTextDark = Color(0xFFE0E0E0)
    val SelectedTextLight = Color(0xFF1A1A1A)
    fun selectedText(isDark: Boolean) = if (isDark) SelectedTextDark else SelectedTextLight

    val ContainerDark = Color(0xFF121212)
    val ContainerLight = Color(0xFFFAFAFA)
    fun container(isDark: Boolean) = if (isDark) ContainerDark else ContainerLight
}
