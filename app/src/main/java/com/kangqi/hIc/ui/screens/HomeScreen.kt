package com.kangqi.hIc.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.model.ModuleStatus
import com.kangqi.hIc.ui.components.GlassCard
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperPageTitle
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.theme.GreenAccent
import com.kangqi.hIc.ui.theme.GreenCheck
import com.kangqi.hIc.utils.DeviceInfoHelper

@Composable
fun HomeScreen(
    moduleStatus: ModuleStatus,
    whitelistCount: Int,
    templateCount: Int
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    val context = LocalContext.current
    val systemInfo = remember { collectSystemInfo(context) }
    val hPadding = if (isMiuix) 16.dp else 20.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = hPadding, vertical = 12.dp)
    ) {
        HyperPageTitle("HyperIsland")

        // Module status card
        val activeColor = GreenCheck
        val inactiveColor = MaterialTheme.colorScheme.error

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (moduleStatus.isActive) "模块已激活" else "模块未激活",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (moduleStatus.isActive) activeColor else inactiveColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (moduleStatus.isActive) "Xposed 框架运行正常"
                        else "请在 LSPosed 中启用本模块",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "版本 ${getVersionName()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (moduleStatus.isActive) GreenAccent.copy(alpha = 0.15f)
                            else inactiveColor.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (moduleStatus.isActive) Icons.Filled.CheckCircle
                        else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (moduleStatus.isActive) activeColor else inactiveColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
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

        // Framework info
        HyperSectionHeader("框架信息")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            InfoRow("Xposed 框架", systemInfo.frameworkName)
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("框架版本", systemInfo.frameworkVersion)
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("Root 管理器", systemInfo.rootManager)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Device info
        HyperSectionHeader("设备信息")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            InfoRow("设备", "${Build.BRAND} ${Build.MODEL}")
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("系统版本", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow("内核版本", getKernelVersion())
            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            val hyperOs = DeviceInfoHelper.getProp("ro.mi.os.version.code")
            if (hyperOs.isNotEmpty()) {
                InfoRow("HyperOS", hyperOs)
                HyperDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
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

// ── System info helpers — using centralized DeviceInfoHelper ──

private data class SystemInfo(
    val frameworkName: String,
    val frameworkVersion: String,
    val rootManager: String,
)

private fun collectSystemInfo(context: Context): SystemInfo {
    val framework = DeviceInfoHelper.getFrameworkInfo(context)
    return SystemInfo(
        frameworkName = framework.frameworkName,
        frameworkVersion = framework.frameworkVersion,
        rootManager = DeviceInfoHelper.getRootManager(),
    )
}

private fun getVersionName(): String {
    return try {
        com.kangqi.hic.preview.BuildConfig.VERSION_NAME
    } catch (_: Throwable) {
        "1.0.0"
    }
}

private fun getKernelVersion(): String {
    return try {
        System.getProperty("os.version") ?: "Unknown"
    } catch (_: Throwable) {
        "Unknown"
    }
}
