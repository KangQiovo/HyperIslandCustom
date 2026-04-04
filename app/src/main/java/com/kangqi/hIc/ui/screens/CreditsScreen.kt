package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperPage
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.components.HyperSettingsItem
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class CreditItem(
    val name: String,
    val author: String,
    val description: String,
    val url: String,
    val license: String = "Apache 2.0"
)

private val creditsList = listOf(
    CreditItem(
        name = "Miuix",
        author = "YuKongA",
        description = "HyperOS (MIUI) design language components for Compose Multiplatform",
        url = "https://github.com/miuix-kotlin-multiplatform/miuix",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "AndroidLiquidGlass",
        author = "Kyant0",
        description = "Android Liquid Glass backdrop blur effects library",
        url = "https://github.com/Kyant0/AndroidLiquidGlass",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "LSPosed",
        author = "LSPosed Team",
        description = "LSPosed Xposed Framework - runtime hooking framework for Android",
        url = "https://github.com/LSPosed/LSPosed",
        license = "GPL 3.0"
    ),
    CreditItem(
        name = "Xposed API",
        author = "rovo89",
        description = "Xposed Framework API for module development",
        url = "https://github.com/rovo89/XposedBridge",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "HyperCeiler",
        author = "ReChronoRain (sevtinge)",
        description = "HyperOS enhancement module - reference implementation for log viewer, device info, config backup",
        url = "https://github.com/ReChronoRain/HyperCeiler",
        license = "AGPL 3.0"
    ),
    CreditItem(
        name = "Jetpack Compose",
        author = "Google / Android",
        description = "Modern declarative UI toolkit for Android",
        url = "https://developer.android.com/jetpack/compose",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "Material Design 3",
        author = "Google",
        description = "Material You design system with dynamic color support",
        url = "https://m3.material.io/",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "colorpicker-compose",
        author = "skydoves",
        description = "Jetpack Compose color picker with various palettes",
        url = "https://github.com/skydoves/colorpicker-compose",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "DataStore",
        author = "Google / AndroidX",
        description = "Jetpack DataStore for type-safe preference storage",
        url = "https://developer.android.com/topic/libraries/architecture/datastore",
        license = "Apache 2.0"
    ),
)

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    val themeManager = LocalThemeManager.current
    val appTheme = themeManager.appTheme
    val isMiuix = appTheme == AppTheme.MIUIX
    val isDark = LocalIsDark.current
    val context = LocalContext.current
    val hPadding = if (isMiuix) 16.dp else 20.dp

    HyperPage(
        title = "引用项目",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = hPadding, vertical = 8.dp)
        ) {
            HyperSectionHeader("开源项目")
            Spacer(modifier = Modifier.height(4.dp))

            val creditIconTint = when (appTheme) {
                AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
                AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
                AppTheme.MD3 -> MaterialTheme.colorScheme.primary
            }
            GlassCardColumn {
                creditsList.forEachIndexed { index, credit ->
                    if (index > 0) HyperDivider()

                    HyperSettingsItem(
                        title = credit.name,
                        subtitle = "${credit.author}\n${credit.description}\nLicense: ${credit.license}",
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        iconTint = creditIconTint,
                        showArrow = true,
                        onClick = {
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(credit.url))
                                )
                            } catch (_: Throwable) {}
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HyperSectionHeader("特别感谢")
            Spacer(modifier = Modifier.height(4.dp))

            val thanksTextColor = when (appTheme) {
                AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f)
                AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            GlassCardColumn {
                Text(
                    text = "感谢以上开源项目的作者和贡献者，使 HyperIsland Custom 的开发成为可能。\n\n" +
                            "本项目采用 MIT License 开源。",
                    fontSize = 14.sp,
                    color = thanksTextColor,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
