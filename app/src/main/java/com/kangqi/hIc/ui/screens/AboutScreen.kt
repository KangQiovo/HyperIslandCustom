package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import top.yukonga.miuix.kmp.theme.MiuixTheme

// ═══════════════ 模块介绍 — moved from CreditsScreen ═══════════════

private const val GITHUB_URL = "https://github.com/KangQiovo/HyperIslandCustom/tree/main"

private val featureDescriptions = listOf(
    "三主题系统" to "应用同时支持 Miuix (HyperOS)、Material Design 3、Liquid Glass 三套原生主题。" +
            "通过 ThemeManager + CompositionLocal 实现全局主题切换，每个 UI 组件 (HyperSwitch, HyperSlider, " +
            "HyperFilterChip, GlassCard 等) 内部使用 when(appTheme) 三路分发，调用各主题对应的原生控件。",
    "Liquid Glass 特效" to "使用 Kyant0 的 AndroidLiquidGlass 库实现 backdrop blur、vibrancy、lens 效果。" +
            "通过 LocalBackdrop 向子组件提供 Backdrop 实例，各卡片/导航栏独立创建 layerBackdrop 实现逐组件模糊，" +
            "而非全页模糊。GlassToggle / GlassSlider 的滑块使用 drawBackdrop 绘制液态玻璃材质。",
    "超级岛控制" to "通过 Xposed Hook HyperOS 3 SystemUI，拦截 BroadcastReceiver 并构建自定义 Notification " +
            "触发超级岛 (Focus Notification)。支持标题/内容/进度条/计时器/按钮/图标/包名伪装等完整参数控制。",
    "日志系统" to "双通道日志：模块日志通过 logcat 实时监听 (HicLog tag)，LSPosed 日志通过读取 " +
            "/data/adb/lspd/log/ 目录解析 (支持 modules_*.log / verbose_*.log)。" +
            "日志导出为 ZIP 包，包含设备信息、模块日志、原始 LSPosed 日志文件。",
    "配置备份" to "通过 ConfigBackupHelper 将 SharedPreferences (island_settings) 和 DataStore (theme_prefs) " +
            "序列化为 JSON 文件。支持导出/导入/重置，通过 FileProvider 共享文件。",
    "框架检测" to "Xposed 框架 API 版本通过 XposedBridge.getXposedVersion() 在 Hook 入口获取并回传至 UI。" +
            "管理器版本通过 PackageManager 查询 LSPosed/LSPatch 包获取。Root 管理器通过 PackageManager 检测 " +
            "Magisk/KernelSU/SukiSU/APatch 管理器 APP 实现无 Root 权限检测。",
    "预测性返回手势" to "通过 AndroidManifest 启用 enableOnBackInvokedCallback，所有二级页面使用 " +
            "BackHandler 拦截返回手势，确保返回上一级而非退出应用，支持 Android 14+ 预测性返回动画。",
)

private const val COOLAPK_URL = "http://www.coolapk.com/u/21241695"
private const val COOLAPK_USER = "@KangQi_ovo"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
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
    val titleColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onBackground
        AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
        AppTheme.MD3 -> MaterialTheme.colorScheme.onBackground
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

    val versionName = try {
        com.kangqi.hic.BuildConfig.VERSION_NAME
    } catch (_: Throwable) { "1.0.0" }
    val versionCode = try {
        com.kangqi.hic.BuildConfig.VERSION_CODE
    } catch (_: Throwable) { 1 }

    // Load the app launcher icon as a bitmap. painterResource can't handle
    // AdaptiveIconDrawable (mipmap-anydpi-v26), so we rasterize it ourselves.
    val appIcon: ImageBitmap = remember(context) {
        val drawable = context.packageManager.getApplicationIcon(context.packageName)
        val size = maxOf(drawable.intrinsicWidth, drawable.intrinsicHeight, 108)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        bitmap.asImageBitmap()
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
                    title = { Text("关于") },
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
                            tint = titleColor
                        )
                    }
                    Text(
                        "关于",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
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
                            tint = titleColor
                        )
                    }
                    Text(
                        "关于",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
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
            // ═══════════════ App Header (icon + name + version) ═══════════════
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = appIcon,
                        contentDescription = "App Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "HyperIsland Custom",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "v$versionName ($versionCode)",
                    fontSize = 14.sp,
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "HyperOS 3 超级岛自定义模块",
                    fontSize = 13.sp,
                    color = subtitleColor
                )
            }
            Spacer(modifier = Modifier.height(28.dp))

            // ═══════════════ 项目地址 ═══════════════
            HyperSectionHeader("项目地址")
            Spacer(modifier = Modifier.height(4.dp))
            GlassCardColumn {
                HyperSettingsItem(
                    icon = Icons.AutoMirrored.Filled.OpenInNew,
                    iconTint = iconTint,
                    title = "GitHub 开源地址",
                    subtitle = GITHUB_URL,
                    showArrow = true,
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                            )
                        } catch (_: Throwable) {}
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════ 模块介绍 ═══════════════
            HyperSectionHeader("模块介绍")
            Spacer(modifier = Modifier.height(4.dp))
            GlassCardColumn {
                featureDescriptions.forEachIndexed { index, (title, desc) ->
                    if (index > 0) HyperDivider()
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = iconTint
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            desc,
                            fontSize = 13.sp,
                            color = subtitleColor,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════ 联系作者 — Coolapk ═══════════════
            HyperSectionHeader("联系作者")
            Spacer(modifier = Modifier.height(4.dp))
            GlassCardColumn {
                HyperSettingsItem(
                    icon = Icons.AutoMirrored.Filled.OpenInNew,
                    iconTint = iconTint,
                    title = "酷安 $COOLAPK_USER",
                    subtitle = "点击跳转到酷安个人主页",
                    showArrow = true,
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(COOLAPK_URL))
                            )
                        } catch (_: Throwable) {}
                    }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
