package com.kangqi.hIc.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.model.ModuleStatus
import com.kangqi.hIc.ui.components.GlassCard
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.components.LocalTiltState
import com.kangqi.hIc.ui.components.tiltGlassBorder
import com.kangqi.hIc.ui.theme.GreenAccent
import com.kangqi.hIc.ui.theme.GreenCheck
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.utils.DeviceInfoHelper
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    moduleStatus: ModuleStatus,
    whitelistCount: Int,
    templateCount: Int
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val systemInfo = remember { collectSystemInfo(context) }

    // Root permission state — seeded from cache to avoid red→green flash on navigation back
    var rootState by remember { mutableStateOf(DeviceInfoHelper.lastKnownRootState) }
    LaunchedEffect(Unit) {
        rootState = withContext(Dispatchers.IO) { DeviceInfoHelper.checkRootState(context) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (isMiuix) 16.dp else 24.dp, vertical = 12.dp)
    ) {
        if (!isMiuix) {
            Text(
                text = "HyperIsland",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Status row — module activation & root authorization ──
        val isDark = LocalIsDark.current

        val lspActive = moduleStatus.isActive
        val lspTitle = if (lspActive) "模块已激活" else "模块未激活"
        val lspFrameworkLabel = when {
            lspActive -> systemInfo.frameworkName.ifBlank { "LSPosed" }
            systemInfo.frameworkName.contains("LSPosed", ignoreCase = true) -> systemInfo.frameworkName
            else -> "未检测到 LSPosed"
        }
        val lspVersionLabel = when {
            systemInfo.managerVersion != "Unknown" -> systemInfo.managerVersion
            systemInfo.frameworkVersion != "Unknown" -> systemInfo.frameworkVersion
            else -> "—"
        }

        val rootActive = rootState == DeviceInfoHelper.RootState.GRANTED
        val rootTitle = when (rootState) {
            DeviceInfoHelper.RootState.GRANTED -> "Root 已授权"
            DeviceInfoHelper.RootState.NOT_GRANTED ->
                if (systemInfo.rootManager == "Unknown") "未检测到 Root" else "Root 未授权"
            DeviceInfoHelper.RootState.NOT_INSTALLED -> "未检测到 Root"
        }
        val rootFramework = if (systemInfo.rootManager == "Unknown") "未检测" else systemInfo.rootManager
        val rootVersion = if (systemInfo.rootVersion.isBlank() || systemInfo.rootManager == "Unknown") "—" else systemInfo.rootVersion
        val rootClickable = !rootActive && systemInfo.rootManager != "Unknown"

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                active = lspActive,
                title = lspTitle,
                subtitle = lspFrameworkLabel,
                detail = lspVersionLabel,
                isDark = isDark,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            StatusCard(
                active = rootActive,
                title = rootTitle,
                subtitle = rootFramework,
                detail = rootVersion,
                isDark = isDark,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = if (rootClickable) {
                    {
                        scope.launch {
                            val granted = withContext(Dispatchers.IO) { DeviceInfoHelper.isRootGranted() }
                            rootState = if (granted) DeviceInfoHelper.RootState.GRANTED
                            else DeviceInfoHelper.RootState.NOT_GRANTED
                        }
                    }
                } else null
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCardColumn(modifier = Modifier.weight(1f)) {
                Text("拦截应用", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$whitelistCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            GlassCardColumn(modifier = Modifier.weight(1f)) {
                Text("自定义模板", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$templateCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Device info
        HyperSectionHeader("设备信息")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            InfoRow("应用版本", "v${getVersionName()} (${getVersionCode()})")
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("框架 API", systemInfo.frameworkVersion)
            if (systemInfo.frameworkName == "LSPatch") {
                HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
                InfoRow("LSPatch", systemInfo.managerVersion)
            }
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("设备", "${Build.BRAND} ${Build.MODEL}")
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("系统版本", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("内核版本", getKernelVersion())
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("系统指纹", Build.FINGERPRINT)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About
        HyperSectionHeader("关于")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            Text(
                text = "HyperIsland Custom 是一款 HyperOS 3 超级岛自定义模块，支持自定义岛内容、颜色和时长。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.65f),
            lineHeight = 18.sp
        )
    }
}

/**
 * SukiSU Ultra–style status card with tinted background and a large
 * check/cross circle icon anchored to the bottom-end corner.
 *
 * - Active:   deep green bg + green CheckCircle
 * - Inactive: deep red bg   + red   Cancel
 *
 * Colors adapt to light/dark mode while keeping sufficient contrast.
 */
@Composable
private fun StatusCard(
    active: Boolean,
    title: String,
    subtitle: String,
    detail: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val isLiquidGlass = LocalThemeManager.current.appTheme == AppTheme.LIQUID_GLASS
    val shape = RoundedCornerShape(16.dp)
    val tilt = LocalTiltState.current

    val cardBg by animateColorAsState(
        targetValue = if (active) {
            if (isDark) Color(0xFF1A3825) else Color(0xFFDFFAE4)
        } else {
            if (isDark) Color(0xFF381A1A) else Color(0xFFFADFDF)
        },
        animationSpec = tween(500),
        label = "cardBg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (active) {
            Color(0xFF36D167)
        } else {
            if (isDark) Color(0xFFE05555) else Color(0xFFD32F2F)
        },
        animationSpec = tween(500),
        label = "iconColor"
    )

    // Liquid Glass: backdrop blur + tinted overlay; other themes: solid background
    val boxModifier = modifier
        .fillMaxWidth()
        .clip(shape)
        .then(
            if (isLiquidGlass) {
                Modifier
                    .drawBackdrop(
                        backdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop(),
                        shape = { shape },
                        effects = {
                            vibrancy()
                            blur(12f.dp.toPx())
                        },
                        onDrawSurface = {
                            // Tinted glass surface: green or red tint over blur
                            drawRect(cardBg.copy(alpha = if (isDark) 0.45f else 0.55f))
                        }
                    )
                    .tiltGlassBorder(tilt, isDark, shape)
            } else {
                Modifier.background(cardBg)
            }
        )
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )

    Box(modifier = boxModifier) {
        // Large icon at bottom-end, offset to partially overflow (clipped by card)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 20.dp, y = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Icon(
                imageVector = if (active) Icons.Rounded.CheckCircleOutline else Icons.Rounded.Cancel,
                contentDescription = null,
                tint = iconColor.let { if (isLiquidGlass) it.copy(alpha = 0.7f) else it },
                modifier = Modifier.size(90.dp)
            )
        }
        // Text content
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.85f),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                fontSize = 13.sp,
                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                detail,
                fontSize = 12.sp,
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f),
            )
        }
    }
}

// ── System info helpers — using centralized DeviceInfoHelper ──

private data class SystemInfo(
    val frameworkName: String,
    val frameworkVersion: String,
    val managerName: String,
    val managerVersion: String,
    val rootManager: String,
    val rootVersion: String,
)

private fun collectSystemInfo(context: Context): SystemInfo {
    val framework = DeviceInfoHelper.getFrameworkInfo(context)
    val root = DeviceInfoHelper.getRootInfo(context)
    return SystemInfo(
        frameworkName = framework.frameworkName,
        frameworkVersion = framework.frameworkVersion,
        managerName = framework.managerName,
        managerVersion = framework.managerVersion,
        rootManager = root.name,
        rootVersion = root.version,
    )
}

private fun getVersionName(): String {
    return try {
        com.kangqi.hic.BuildConfig.VERSION_NAME
    } catch (_: Throwable) {
        "1.0.0"
    }
}

private fun getVersionCode(): Int {
    return try {
        com.kangqi.hic.BuildConfig.VERSION_CODE
    } catch (_: Throwable) {
        1
    }
}

private fun getKernelVersion(): String {
    return try {
        System.getProperty("os.version") ?: "Unknown"
    } catch (_: Throwable) {
        "Unknown"
    }
}
