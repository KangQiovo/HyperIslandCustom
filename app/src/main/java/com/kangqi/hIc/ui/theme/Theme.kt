package com.kangqi.hIc.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalContentColor
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

// Static color schemes used as fallback when dynamic Monet colors aren't available
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3482FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2060D0),
    secondary = Color(0xFF5A9FFF),
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFF2C2C2E),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFF38383A),
    outlineVariant = Color(0xFF38383A),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3482FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5A9FFF),
    secondary = Color(0xFF2060D0),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEEEEEE),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFDCDCDC),
    outlineVariant = Color(0xFFE5E5E5),
)

val LocalBackdrop = compositionLocalOf<Backdrop?> { null }

/**
 * Theme-aware dark mode flag — respects ThemeManager.darkMode setting,
 * not just system dark mode. Use this instead of isSystemInDarkTheme()
 * throughout the app to ensure consistent dark mode behavior.
 */
val LocalIsDark = compositionLocalOf { false }

@Composable
fun HyperIslandTheme(
    themeManager: ThemeManager? = null,
    backdrop: Backdrop? = null,
    content: @Composable () -> Unit
) {
    val tm = themeManager
    val isDark = when (tm?.darkMode) {
        DarkMode.LIGHT -> false
        DarkMode.DARK -> true
        else -> isSystemInDarkTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()
            // Set window background to match dark/light mode so glass effects have correct base
            window.decorView.setBackgroundColor(
                if (isDark) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#F5F5F5")
            )
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    val appTheme = tm?.appTheme ?: AppTheme.MIUIX

    // MD3 always uses Monet on Android 12+; other themes use static palette
    val staticColors = if (isDark) DarkColorScheme else LightColorScheme
    val md3Colors = if (appTheme == AppTheme.MD3 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        staticColors
    }

    val themedContent = @Composable {
        val contentColor = if (isDark) Color.White else Color.Black
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            MiuixLocalContentColor provides contentColor,
            LocalBackdrop provides backdrop,
            LocalIsDark provides isDark
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (appTheme == AppTheme.LIQUID_GLASS) {
                            // Solid base only — glass effects are per-component (cards, navbar)
                            Modifier.background(GlassTokens.base(isDark))
                        } else {
                            Modifier.background(md3Colors.background)
                        }
                    )
            ) {
                content()
            }
        }
    }

    // Determine Miuix ColorSchemeMode
    val miuixMode = when {
        tm?.monetEnabled == true -> if (isDark) ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight
        isDark -> ColorSchemeMode.Dark
        else -> ColorSchemeMode.Light
    }

    // Always wrap with MiuixTheme so MiuixScaffold gets proper dark/light colors
    val miuixController = remember(miuixMode) { ThemeController(miuixMode) }
    val materialColorScheme = when (appTheme) {
        AppTheme.LIQUID_GLASS -> staticColors.copy(
            background = Color.Transparent,
            surface = Color.Transparent
        )
        else -> md3Colors
    }

    MiuixTheme(controller = miuixController) {
        MaterialTheme(colorScheme = materialColorScheme, content = themedContent)
    }
}