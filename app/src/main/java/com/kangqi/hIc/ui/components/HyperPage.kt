package com.kangqi.hIc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Unified page template inspired by OShin's FunPage pattern.
 *
 * For secondary pages (onBack != null): shows a themed compact top bar with
 * back button, title, and optional action buttons. Handles status bar insets.
 *
 * For main pages (onBack == null): just wraps content. The scaffold or
 * HyperPageTitle handles the title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperPage(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val themeManager = LocalThemeManager.current
    val isDark = LocalIsDark.current
    val isSecondary = onBack != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isSecondary) Modifier.windowInsetsPadding(WindowInsets.statusBars)
                else Modifier
            )
    ) {
        if (onBack != null) {
            HyperTopBar(
                title = title,
                onBack = onBack,
                actions = actions
            )
        }
        content()
    }
}

/**
 * Themed compact top bar with back button for secondary pages.
 * Adapts to MIUIX, MD3, and Liquid Glass themes.
 * Eliminates per-screen duplicated top bar code (from HyperCeiler/OShin pattern).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val themeManager = LocalThemeManager.current
    val isDark = LocalIsDark.current

    when (themeManager.appTheme) {
        AppTheme.MD3 -> {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(),
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                modifier = modifier
            )
        }

        AppTheme.MIUIX -> {
            Row(
                modifier = modifier
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
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiuixTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                actions()
            }
        }

        AppTheme.LIQUID_GLASS -> {
            Row(
                modifier = modifier
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
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                actions()
            }
        }
    }
}

/**
 * Large page title for main pages (non-MIUIX themes).
 * MIUIX theme uses the scaffold's MiuixTopAppBar instead.
 * Inspired by OShin/SukiSU-Ultra's large title pattern.
 */
@Composable
fun HyperPageTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    val themeManager = LocalThemeManager.current
    val isDark = LocalIsDark.current

    if (themeManager.appTheme == AppTheme.MIUIX) {
        Spacer(modifier = modifier.height(8.dp))
        return
    }

    val titleColor = when (themeManager.appTheme) {
        AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
        else -> MaterialTheme.colorScheme.onBackground
    }

    Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = titleColor,
        modifier = modifier.padding(bottom = 20.dp, top = 8.dp)
    )
}
