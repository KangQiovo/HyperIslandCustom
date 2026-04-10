package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.components.HyperSettingsItem
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class CreditItem(
    val name: String,
    val author: String,
    val description: String,
    val url: String,
    val license: String = "Apache 2.0"
)

// ═══════════════ 引用项目 (Directly Used Libraries) ═══════════════

private val themeLibraries = listOf(
    CreditItem(
        name = "Miuix",
        author = "YuKongA",
        description = "HyperOS (MIUI) 设计语言组件库，提供 Compose Multiplatform 原生 Miuix 控件",
        url = "https://github.com/miuix-kotlin-multiplatform/miuix",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "AndroidLiquidGlass",
        author = "Kyant0",
        description = "Android Liquid Glass 液态玻璃模糊特效库，实现 backdrop blur / vibrancy / lens 效果",
        url = "https://github.com/Kyant0/AndroidLiquidGlass",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "Material Design 3",
        author = "Google",
        description = "Material You 设计系统，支持动态取色与 MD3 原生组件",
        url = "https://m3.material.io/",
        license = "Apache 2.0"
    ),
)

private val frameworkLibraries = listOf(
    CreditItem(
        name = "LSPosed",
        author = "LSPosed Team",
        description = "LSPosed Xposed 框架 —— Android 运行时 Hook 框架",
        url = "https://github.com/LSPosed/LSPosed",
        license = "GPL 3.0"
    ),
    CreditItem(
        name = "Xposed API",
        author = "rovo89",
        description = "Xposed 框架模块开发 API",
        url = "https://github.com/rovo89/XposedBridge",
        license = "Apache 2.0"
    ),
)

private val uiLibraries = listOf(
    CreditItem(
        name = "Jetpack Compose",
        author = "Google / Android",
        description = "现代声明式 UI 工具包，构建原生 Android 界面",
        url = "https://developer.android.com/jetpack/compose",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "colorpicker-compose",
        author = "skydoves",
        description = "Jetpack Compose 颜色选择器，支持多种调色板模式",
        url = "https://github.com/skydoves/colorpicker-compose",
        license = "Apache 2.0"
    ),
    CreditItem(
        name = "DataStore",
        author = "Google / AndroidX",
        description = "Jetpack DataStore 类型安全偏好存储",
        url = "https://developer.android.com/topic/libraries/architecture/datastore",
        license = "Apache 2.0"
    ),
)

// ═══════════════ 参考项目 (Inspiration & Reference) ═══════════════

private val referenceXposedModules = listOf(
    CreditItem(
        name = "HyperCeiler",
        author = "ReChronoRain (sevtinge)",
        description = "HyperOS 增强模块 —— 参考其日志查看器实现、设备信息收集、配置备份/恢复、LSPosed 日志解析格式",
        url = "https://github.com/ReChronoRain/HyperCeiler",
        license = "AGPL 3.0"
    ),
    CreditItem(
        name = "OShin",
        author = "suqi8",
        description = "ColorOS 增强模块 —— 参考其 LSPosed/Root 框架版本检测方案与设备信息展示",
        url = "https://github.com/suqi8/OShin",
        license = "AGPL 3.0"
    ),
)

private val referenceRootProjects = listOf(
    CreditItem(
        name = "SukiSU Ultra",
        author = "SukiSU Team",
        description = "基于 KernelSU 的 Root 方案 —— 参考其 Root 管理器检测与版本获取方式",
        url = "https://github.com/SukiSU-Ultra/SukiSU-Ultra",
        license = "GPL 2.0"
    ),
)

// Note: "功能实现手法" section moved to AboutScreen (设置 → 关于 → HyperIsland Custom)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val themeManager = LocalThemeManager.current
    val appTheme = themeManager.appTheme
    val isDark = LocalIsDark.current
    val context = LocalContext.current

    val iconTint = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
        AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
        AppTheme.MD3 -> MaterialTheme.colorScheme.primary
    }
    val subtitleColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val screenBg = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.background
        AppTheme.LIQUID_GLASS -> GlassTokens.base(isDark)
        AppTheme.MD3 -> MaterialTheme.colorScheme.background
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // ═══════════════ Top Bar ═══════════════
        when (appTheme) {
            AppTheme.MD3 -> {
                TopAppBar(
                    title = { Text("引用项目 & 参考") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                )
            }
            AppTheme.MIUIX -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        "引用项目 & 参考",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            AppTheme.LIQUID_GLASS -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }
                    Text(
                        "引用项目 & 参考",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        val contentHPadding = when (appTheme) {
            AppTheme.MIUIX -> 16.dp
            AppTheme.MD3 -> 20.dp
            AppTheme.LIQUID_GLASS -> 20.dp
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = contentHPadding, vertical = 8.dp)
        ) {
            // ═══════════════ Section 1: 引用项目 ═══════════════
            HyperSectionHeader("引用项目 — 主题 & 设计")
            Spacer(modifier = Modifier.height(4.dp))
            CreditCategoryCard(themeLibraries, iconTint, context)

            Spacer(modifier = Modifier.height(16.dp))

            HyperSectionHeader("引用项目 — Xposed 框架")
            Spacer(modifier = Modifier.height(4.dp))
            CreditCategoryCard(frameworkLibraries, iconTint, context)

            Spacer(modifier = Modifier.height(16.dp))

            HyperSectionHeader("引用项目 — UI & 存储")
            Spacer(modifier = Modifier.height(4.dp))
            CreditCategoryCard(uiLibraries, iconTint, context)

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════ Section 2: 参考项目 ═══════════════
            HyperSectionHeader("参考项目 — Xposed 模块")
            Spacer(modifier = Modifier.height(4.dp))
            CreditCategoryCard(referenceXposedModules, iconTint, context)

            Spacer(modifier = Modifier.height(16.dp))

            HyperSectionHeader("参考项目 — Root 方案")
            Spacer(modifier = Modifier.height(4.dp))
            CreditCategoryCard(referenceRootProjects, iconTint, context)

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════ Section 3: 致谢 ═══════════════
            HyperSectionHeader("特别感谢")
            Spacer(modifier = Modifier.height(4.dp))
            GlassCardColumn {
                Text(
                    text = "感谢以上开源项目的作者和贡献者，使 HyperIsland Custom 的开发成为可能。\n\n" +
                            "本项目采用 MIT License 开源。",
                    fontSize = 14.sp,
                    color = subtitleColor,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun CreditCategoryCard(
    items: List<CreditItem>,
    iconTint: Color,
    context: android.content.Context
) {
    GlassCardColumn {
        items.forEachIndexed { index, credit ->
            if (index > 0) HyperDivider()
            HyperSettingsItem(
                title = credit.name,
                subtitle = "${credit.author}\n${credit.description}\nLicense: ${credit.license}",
                icon = Icons.AutoMirrored.Filled.OpenInNew,
                iconTint = iconTint,
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
}
