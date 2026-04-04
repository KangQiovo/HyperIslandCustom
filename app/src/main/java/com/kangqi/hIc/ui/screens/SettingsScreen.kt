package com.kangqi.hIc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperAlertDialog
import com.kangqi.hIc.ui.components.HyperButton
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperFilterChip
import com.kangqi.hIc.ui.components.HyperPageTitle
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.components.HyperSettingsItem
import com.kangqi.hIc.ui.components.HyperSlider
import com.kangqi.hIc.ui.components.HyperTextButton
import com.kangqi.hIc.ui.components.HyperTextField
import com.kangqi.hIc.ui.components.HyperToggleItem
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.DarkMode
import com.kangqi.hIc.ui.theme.DockStyle
import com.kangqi.hIc.ui.theme.LocalThemeManager

@Composable
fun SettingsScreen(
    isEnabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    whitelist: Set<String>,
    onAddToWhitelist: (String) -> Unit,
    onRemoveFromWhitelist: (String) -> Unit,
    onResetSettings: () -> Unit,
    onNavigateToLogs: () -> Unit = {},
    onExportConfig: () -> Unit = {},
    onImportConfig: () -> Unit = {},
    onNavigateToCredits: () -> Unit = {}
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    val hPadding = if (isMiuix) 16.dp else 20.dp
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = hPadding, vertical = 12.dp)
    ) {
        HyperPageTitle("设置")

        // ═══════════════ Theme Settings ═══════════════
        HyperSectionHeader("主题")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            // App Theme selector
            HyperSettingsItem(
                icon = Icons.Filled.Style,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "应用主题",
                subtitle = when (themeManager.appTheme) {
                    AppTheme.MIUIX -> "Miuix (HyperOS)"
                    AppTheme.MD3 -> "Material Design 3"
                    AppTheme.LIQUID_GLASS -> "Android Liquid Glass"
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    HyperFilterChip(
                        selected = themeManager.appTheme == theme,
                        onClick = { themeManager.updateAppTheme(theme) },
                        label = when (theme) {
                            AppTheme.MIUIX -> "Miuix"
                            AppTheme.MD3 -> "MD3"
                            AppTheme.LIQUID_GLASS -> "Liquid Glass"
                        }
                    )
                }
            }
            HyperDivider()

            // Dock Style selector
            HyperSettingsItem(
                icon = Icons.Filled.Navigation,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Dock 栏样式",
                subtitle = when (themeManager.dockStyle) {
                    DockStyle.MIUIX -> "Miuix (HyperOS)"
                    DockStyle.MD3 -> "Material Design 3"
                    DockStyle.LIQUID_GLASS -> "Android Liquid Glass"
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DockStyle.entries.forEach { style ->
                    HyperFilterChip(
                        selected = themeManager.dockStyle == style,
                        onClick = { themeManager.updateDockStyle(style) },
                        label = when (style) {
                            DockStyle.MIUIX -> "Miuix"
                            DockStyle.MD3 -> "MD3"
                            DockStyle.LIQUID_GLASS -> "Liquid Glass"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════ Glass Effects (only when dock is Liquid Glass) ═══════════════
        if (themeManager.dockStyle == DockStyle.LIQUID_GLASS) {
            HyperSectionHeader("Glass 效果")
            Spacer(modifier = Modifier.height(4.dp))
            GlassCardColumn {
                HyperSettingsItem(
                    icon = Icons.Filled.Opacity,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Glass Bottom Bar 透明度",
                    subtitle = "${(themeManager.glassBarAlpha * 100).toInt()}%"
                )
                HyperSlider(
                    value = themeManager.glassBarAlpha,
                    onValueChange = { themeManager.updateGlassBarAlpha(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )

                HyperDivider()

                HyperSettingsItem(
                    icon = Icons.Filled.Opacity,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Glass Slider 透明度",
                    subtitle = "${(themeManager.glassSliderAlpha * 100).toInt()}%"
                )
                HyperSlider(
                    value = themeManager.glassSliderAlpha,
                    onValueChange = { themeManager.updateGlassSliderAlpha(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ═══════════════ Display Settings ═══════════════
        HyperSectionHeader("显示")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            // Dark mode
            HyperSettingsItem(
                icon = Icons.Filled.DarkMode,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "深色模式",
                subtitle = when (themeManager.darkMode) {
                    DarkMode.FOLLOW_SYSTEM -> "跟随系统"
                    DarkMode.LIGHT -> "浅色"
                    DarkMode.DARK -> "深色"
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DarkMode.entries.forEach { mode ->
                    HyperFilterChip(
                        selected = themeManager.darkMode == mode,
                        onClick = { themeManager.updateDarkMode(mode) },
                        label = when (mode) {
                            DarkMode.FOLLOW_SYSTEM -> "跟随系统"
                            DarkMode.LIGHT -> "浅色"
                            DarkMode.DARK -> "深色"
                        }
                    )
                }
            }

            HyperDivider()

            // Predictive back
            HyperToggleItem(
                title = "预测性返回手势",
                subtitle = "启用 Android 预测性返回动画",
                checked = themeManager.predictiveBackEnabled,
                onCheckedChange = { themeManager.updatePredictiveBackEnabled(it) },
                icon = Icons.Filled.SwipeRight,
                iconTint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════ App Whitelist ═══════════════
        HyperSectionHeader("应用白名单")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Apps,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        "白名单管理",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { showAddAppDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "添加应用", tint = MaterialTheme.colorScheme.primary)
                }
            }

            if (whitelist.isEmpty()) {
                Text(
                    "暂无白名单应用，点击 + 添加",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            } else {
                whitelist.forEachIndexed { index, pkg ->
                    if (index > 0) HyperDivider(modifier = Modifier.padding(vertical = 2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pkg,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onRemoveFromWhitelist(pkg) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "移除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════ Config Management ═══════════════
        HyperSectionHeader("模块配置")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            HyperSettingsItem(
                icon = Icons.Filled.FileUpload,
                title = "导出配置",
                subtitle = "将当前配置导出为 JSON 文件",
                showArrow = true,
                onClick = onExportConfig
            )
            HyperDivider()
            HyperSettingsItem(
                icon = Icons.Filled.FileDownload,
                title = "导入配置",
                subtitle = "从 JSON 文件恢复配置",
                showArrow = true,
                onClick = onImportConfig
            )
            HyperDivider()
            HyperSettingsItem(
                icon = Icons.Filled.RestartAlt,
                iconTint = MaterialTheme.colorScheme.error,
                title = "重置所有设置",
                subtitle = "恢复默认配置（不可撤销）",
                showArrow = true,
                onClick = { showResetConfirm = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════ Advanced ═══════════════
        HyperSectionHeader("高级")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            HyperSettingsItem(
                icon = Icons.Filled.BugReport,
                title = "运行日志",
                subtitle = "查看模块运行日志与 Xposed 日志",
                showArrow = true,
                onClick = onNavigateToLogs
            )
            HyperDivider()
            HyperSettingsItem(
                icon = Icons.Filled.Code,
                title = "Xposed API 版本",
                subtitle = "支持 API 100 & 101"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════ About ═══════════════
        HyperSectionHeader("关于")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            HyperSettingsItem(
                icon = Icons.Filled.Info,
                title = "HyperIsland Custom",
                subtitle = "v${try { com.kangqi.hic.preview.BuildConfig.VERSION_NAME } catch (_: Throwable) { "1.0.0" }} · HyperOS 3 超级岛自定义模块"
            )
            HyperDivider()
            HyperSettingsItem(
                icon = Icons.AutoMirrored.Filled.OpenInNew,
                title = "引用项目",
                subtitle = "查看第三方开源项目引用",
                showArrow = true,
                onClick = onNavigateToCredits
            )
            HyperDivider()
            HyperSettingsItem(
                icon = Icons.AutoMirrored.Filled.OpenInNew,
                title = "开源许可",
                subtitle = "MIT License",
                showArrow = true,
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // ── Dialogs ──
    if (showAddAppDialog) {
        AddAppDialog(
            onDismiss = { showAddAppDialog = false },
            onAdd = { pkg ->
                onAddToWhitelist(pkg)
                showAddAppDialog = false
            }
        )
    }

    if (showResetConfirm) {
        HyperAlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("确认重置", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text("这将恢复所有设置为默认值，此操作不可撤销。", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                HyperButton(
                    text = "重置",
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        onResetSettings()
                        showResetConfirm = false
                    }
                )
            },
            dismissButton = {
                HyperTextButton(
                    text = "取消",
                    onClick = { showResetConfirm = false },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
private fun AddAppDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var packageName by remember { mutableStateOf("") }

    HyperAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加应用", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "输入要拦截到超级岛的应用包名",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HyperTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = "包名",
                    placeholder = "com.example.app"
                )
            }
        },
        confirmButton = {
            HyperButton(
                text = "添加",
                onClick = { if (packageName.isNotBlank()) onAdd(packageName.trim()) }
            )
        },
        dismissButton = {
            HyperTextButton(
                text = "取消",
                onClick = onDismiss,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
