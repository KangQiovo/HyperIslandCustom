package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.log.LogCollector
import com.kangqi.hIc.log.LogEntry
import com.kangqi.hIc.log.LogLevel
import com.kangqi.hIc.ui.components.HyperAlertDialog
import com.kangqi.hIc.ui.components.HyperFilterChip
import com.kangqi.hIc.ui.components.HyperTextButton
import com.kangqi.hIc.ui.components.HyperTextField
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.WarningOrange
import androidx.core.content.FileProvider
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private enum class LogFilter(val label: String) {
    ALL("全部"), DEBUG("调试"), INFO("信息"), HOOK("Hook"), ISLAND("岛触发"), WARN("警告"), ERROR("错误")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogViewerScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val themeManager = LocalThemeManager.current
    val appTheme = themeManager.appTheme
    val isDark = LocalIsDark.current
    val isMiuix = appTheme == AppTheme.MIUIX
    val isGlass = appTheme == AppTheme.LIQUID_GLASS
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    // Separate filter state per tab (not synced)
    var moduleFilter by remember { mutableStateOf(LogFilter.ALL) }
    var lsposedFilter by remember { mutableStateOf(LogFilter.ALL) }
    // LSPosed tab has an additional "本应用" toggle that can combine with level filter
    var lsposedThisAppOnly by remember { mutableStateOf(true) }
    var showClearDialog by remember { mutableStateOf(false) }
    var detailEntry by remember { mutableStateOf<LogEntry?>(null) }
    val moduleListState = rememberLazyListState()
    val lsposedListState = rememberLazyListState()
    val selectedFilter = if (selectedTab == 0) moduleFilter else lsposedFilter

    // Collect real-time log streams from LogCollector
    val moduleLogs by LogCollector.moduleLogs.collectAsState()
    val lsposedLogs by LogCollector.lsposedLogs.collectAsState()

    // Trigger initial LSPosed sync
    LaunchedEffect(Unit) { LogCollector.syncLsposedLogs() }

    val currentLogs = if (selectedTab == 0) moduleLogs else lsposedLogs
    val listState = if (selectedTab == 0) moduleListState else lsposedListState

    val filteredLogs by remember(currentLogs, selectedFilter, searchQuery, lsposedThisAppOnly, selectedTab) {
        derivedStateOf {
            currentLogs.filter { entry ->
                // Level filter
                val matchesLevel = when (selectedFilter) {
                    LogFilter.ALL -> true
                    LogFilter.DEBUG -> entry.level == LogLevel.DEBUG
                    LogFilter.INFO -> entry.level == LogLevel.INFO
                    LogFilter.HOOK -> entry.level == LogLevel.HOOK
                    LogFilter.ISLAND -> entry.level == LogLevel.ISLAND
                    LogFilter.WARN -> entry.level == LogLevel.WARN
                    LogFilter.ERROR -> entry.level == LogLevel.ERROR
                }
                // THIS_APP toggle — only applies on LSPosed tab, combines with level filter
                val matchesThisApp = if (selectedTab == 1 && lsposedThisAppOnly) {
                    entry.packageName.contains("kangqi", ignoreCase = true) ||
                            entry.packageName.contains("hic", ignoreCase = true) ||
                            entry.tag.contains("hIc", ignoreCase = true) ||
                            entry.tag.contains("HIC", ignoreCase = false)
                } else true
                val matchesSearch = searchQuery.isBlank() ||
                        entry.message.contains(searchQuery, ignoreCase = true) ||
                        entry.tag.contains(searchQuery, ignoreCase = true)
                matchesLevel && matchesThisApp && matchesSearch
            }
        }
    }

    // Auto-scroll to bottom when new module logs arrive
    LaunchedEffect(moduleLogs.size) {
        if (selectedTab == 0 && moduleLogs.isNotEmpty()) {
            val lastVisibleIndex = moduleListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = moduleListState.layoutInfo.totalItemsCount
            if (totalItems > 0 && lastVisibleIndex >= totalItems - 3) {
                moduleListState.animateScrollToItem(filteredLogs.lastIndex.coerceAtLeast(0))
            }
        }
    }

    // Share action — exports as ZIP with device info
    val onShare = {
        scope.launch {
            Toast.makeText(context, "Exporting logs...", Toast.LENGTH_SHORT).show()
            val zipFile = LogCollector.exportToZip(context)
            if (zipFile != null && zipFile.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    zipFile
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "HyperIsland Custom Logs")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "分享日志"))
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
        Unit
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
                // MD3 - native Material3 TopAppBar
                TopAppBar(
                    title = { Text("运行日志") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        LogActionButtons(selectedTab, scope, listState, filteredLogs,
                            onShare = onShare, onClear = { showClearDialog = true })
                    },
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                )
            }
            AppTheme.MIUIX -> {
                // Miuix - HyperOS native styled header
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
                        "运行日志",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    LogActionButtons(selectedTab, scope, listState, filteredLogs,
                        onShare = onShare, onClear = { showClearDialog = true }, appTheme = appTheme)
                }
            }
            AppTheme.LIQUID_GLASS -> {
                // Liquid Glass - translucent styled header
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
                        "运行日志",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    LogActionButtons(selectedTab, scope, listState, filteredLogs,
                        onShare = onShare, onClear = { showClearDialog = true }, appTheme = appTheme)
                }
            }
        }

        // ═══════════════ Tab Selector ═══════════════
        when (appTheme) {
            AppTheme.MD3 -> {
                // MD3 - native SecondaryTabRow
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "模块日志 (${moduleLogs.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; LogCollector.syncLsposedLogs() },
                        text = {
                            Text(
                                "LSPosed (${lsposedLogs.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            AppTheme.MIUIX -> {
                // Miuix - native chip-based tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HyperFilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = "模块日志 (${moduleLogs.size})"
                    )
                    HyperFilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; LogCollector.syncLsposedLogs() },
                        label = "LSPosed (${lsposedLogs.size})"
                    )
                }
            }
            AppTheme.LIQUID_GLASS -> {
                // Liquid Glass - native chip-based tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HyperFilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = "模块日志 (${moduleLogs.size})"
                    )
                    HyperFilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; LogCollector.syncLsposedLogs() },
                        label = "LSPosed (${lsposedLogs.size})"
                    )
                }
            }
        }

        // ═══════════════ Search + Filter ═══════════════
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            HyperTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "搜索日志 (关键词/标签)",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Separate "本应用" toggle chip — only on LSPosed tab, combines with level filter
                if (selectedTab == 1) {
                    HyperFilterChip(
                        selected = lsposedThisAppOnly,
                        onClick = { lsposedThisAppOnly = !lsposedThisAppOnly },
                        label = "本应用"
                    )
                }
                LogFilter.entries.forEach { filter ->
                    HyperFilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            if (selectedTab == 0) moduleFilter = filter
                            else lsposedFilter = filter
                        },
                        label = filter.label
                    )
                }
            }
        }

        // ═══════════════ Status Bar ═══════════════
        val statusVariantColor = when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
            AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f)
            AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        val statusPrimaryColor = when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
            AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
            AppTheme.MD3 -> MaterialTheme.colorScheme.primary
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${filteredLogs.size} 条日志",
                fontSize = 12.sp,
                color = statusVariantColor
            )
            if (selectedTab == 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(statusPrimaryColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("实时监听中", fontSize = 12.sp, color = statusPrimaryColor)
                }
            }
        }

        // ═══════════════ Log List ═══════════════
        val listHPadding = when (appTheme) {
            AppTheme.MIUIX -> 12.dp
            AppTheme.MD3 -> 16.dp
            AppTheme.LIQUID_GLASS -> 16.dp
        }
        val listSpacing = when (appTheme) {
            AppTheme.MIUIX -> 10.dp
            AppTheme.MD3 -> 8.dp
            AppTheme.LIQUID_GLASS -> 10.dp
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = listHPadding),
            verticalArrangement = Arrangement.spacedBy(listSpacing)
        ) {
            if (filteredLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLogs.isEmpty()) {
                                if (selectedTab == 0) "暂无模块日志\nlogcat 实时监听已启动，操作模块后将自动记录"
                                else "暂无 LSPosed 日志\n点击同步按钮从 /data/adb/lspd/log/ 读取"
                            } else "没有匹配的日志",
                            fontSize = 14.sp,
                            color = statusVariantColor,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                itemsIndexed(
                    filteredLogs,
                    key = { _, entry -> "${entry.index}_${entry.timestamp}" }
                ) { _, entry ->
                    LogEntryRow(
                        entry = entry,
                        searchQuery = searchQuery,
                        appTheme = appTheme,
                        isDark = isDark,
                        onClick = { detailEntry = entry }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // ═══════════════ Clear Confirmation Dialog ═══════════════
    HyperAlertDialog(
        show = showClearDialog,
        onDismissRequest = { showClearDialog = false },
        title = {
            Text(
                "清空日志",
                color = when (appTheme) {
                    AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurface
                    AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
                    AppTheme.MD3 -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "确定要清空所有模块日志吗？此操作不可撤销。",
                color = when (appTheme) {
                    AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                    AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f)
                    AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        confirmButton = {
            HyperTextButton(
                text = "确认清空",
                onClick = {
                    showClearDialog = false
                    LogCollector.clearModuleLogs()
                }
            )
        },
        dismissButton = {
            HyperTextButton(
                text = "取消",
                onClick = { showClearDialog = false },
                color = when (appTheme) {
                    AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantActions
                    AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f)
                    AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    )

    // ═══════════════ Log Detail Overlay ═══════════════
    detailEntry?.let { entry ->
        LogDetailScreen(entry = entry, appTheme = appTheme, isDark = isDark, onBack = { detailEntry = null })
    }
}

// ═══════════════ Shared Action Buttons ═══════════════

@Composable
private fun LogActionButtons(
    selectedTab: Int,
    scope: CoroutineScope,
    listState: LazyListState,
    filteredLogs: List<LogEntry>,
    onShare: () -> Unit,
    onClear: () -> Unit = {},
    appTheme: AppTheme = AppTheme.MD3
) {
    val isDark = LocalIsDark.current
    val primaryTint = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
        AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
        AppTheme.MD3 -> MaterialTheme.colorScheme.primary
    }
    val secondaryTint = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantActions
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    if (selectedTab == 1) {
        IconButton(onClick = { LogCollector.syncLsposedLogs() }) {
            Icon(
                Icons.Filled.Sync,
                contentDescription = "同步 LSPosed 日志",
                tint = primaryTint
            )
        }
    }
    if (selectedTab == 0) {
        IconButton(onClick = onClear) {
            Icon(
                Icons.Filled.DeleteSweep,
                contentDescription = "清除日志",
                tint = secondaryTint
            )
        }
    }
    IconButton(onClick = {
        scope.launch {
            if (filteredLogs.isNotEmpty()) {
                listState.animateScrollToItem(filteredLogs.lastIndex)
            }
        }
    }) {
        Icon(
            Icons.Filled.VerticalAlignBottom,
            contentDescription = "滚动到底部",
            tint = secondaryTint
        )
    }
    IconButton(onClick = onShare) {
        Icon(
            Icons.Filled.Share,
            contentDescription = "导出",
            tint = secondaryTint
        )
    }
}

// ═══════════════ Theme-Aware Log Entry Row ═══════════════

@Composable
private fun LogEntryRow(entry: LogEntry, searchQuery: String, appTheme: AppTheme, isDark: Boolean, onClick: () -> Unit = {}) {
    val levelColor = when (entry.level) {
        LogLevel.DEBUG -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.outline
            AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f)
            AppTheme.MD3 -> MaterialTheme.colorScheme.outline
        }
        LogLevel.INFO -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
            AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f)
            AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        LogLevel.HOOK -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
            AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
            AppTheme.MD3 -> MaterialTheme.colorScheme.primary
        }
        LogLevel.ISLAND -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.secondary
            AppTheme.LIQUID_GLASS -> Color(0xFF5AC8FA)
            AppTheme.MD3 -> MaterialTheme.colorScheme.tertiary
        }
        LogLevel.WARN -> WarningOrange
        LogLevel.ERROR -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.error
            AppTheme.LIQUID_GLASS -> Color(0xFFFF3B30)
            AppTheme.MD3 -> MaterialTheme.colorScheme.error
        }
    }

    val cornerRadius = when (appTheme) {
        AppTheme.MIUIX -> 14.dp
        AppTheme.LIQUID_GLASS -> 14.dp
        AppTheme.MD3 -> 12.dp
    }
    val shape = RoundedCornerShape(cornerRadius)

    // Theme-appropriate text colors
    val onSurfaceColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurface
        AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurface
    }
    val onSurfaceVariantColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.75f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val outlineColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.outline
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.45f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.outline
    }

    // Theme-appropriate container modifier
    // MIUIX: surfaceContainer bg with subtle native divider-line border
    // LIQUID_GLASS: real-time backdrop blur + vibrancy with gradient glass border (matching GlassCard)
    // MD3: native surfaceContainer fill
    val containerModifier = when (appTheme) {
        AppTheme.MIUIX -> Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MiuixTheme.colorScheme.surfaceContainer)
            .border(
                width = 0.5.dp,
                color = MiuixTheme.colorScheme.dividerLine,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
        AppTheme.LIQUID_GLASS -> Modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBackdrop(
                backdrop = rememberLayerBackdrop(),
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(12f.dp.toPx())
                },
                onDrawSurface = {
                    drawRect(
                        (if (isDark) Color.White else Color.Black).copy(
                            alpha = if (isDark) 0.08f else 0.04f
                        )
                    )
                }
            )
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.04f),
                        )
                    } else {
                        listOf(
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.02f),
                        )
                    }
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
        AppTheme.MD3 -> Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    }

    Column(modifier = containerModifier) {
        // Header: level badge + tag + timestamp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Level badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(levelColor.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        entry.level.short,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = levelColor
                    )
                }
                // Tag
                Text(
                    entry.tag,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Timestamp
            Text(
                entry.timestamp,
                fontSize = 10.sp,
                color = outlineColor,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Message body with optional search highlight
        val highlightColor = when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
            AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
            AppTheme.MD3 -> MaterialTheme.colorScheme.primary
        }
        if (searchQuery.isNotBlank() && entry.message.contains(searchQuery, ignoreCase = true)) {
            HighlightedText(
                text = entry.message,
                highlight = searchQuery,
                baseColor = onSurfaceVariantColor,
                highlightColor = highlightColor
            )
        } else {
            Text(
                entry.message,
                fontSize = 12.sp,
                color = onSurfaceVariantColor,
                lineHeight = 16.sp,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.Monospace
            )
        }

        // PID/TID info if available
        if (entry.pid > 0) {
            Text(
                "PID:${entry.pid} TID:${entry.tid}",
                fontSize = 9.sp,
                color = outlineColor.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ═══════════════ Search Keyword Highlighting ═══════════════

@Composable
private fun HighlightedText(
    text: String,
    highlight: String,
    baseColor: Color,
    highlightColor: Color
) {
    val annotated = buildAnnotatedString {
        var lastIndex = 0
        val lowerText = text.lowercase()
        val lowerHighlight = highlight.lowercase()
        var searchFrom = 0
        while (true) {
            val idx = lowerText.indexOf(lowerHighlight, searchFrom)
            if (idx == -1) break
            withStyle(SpanStyle(color = baseColor)) {
                append(text.substring(lastIndex, idx))
            }
            withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(text.substring(idx, idx + highlight.length))
            }
            lastIndex = idx + highlight.length
            searchFrom = lastIndex
        }
        if (lastIndex < text.length) {
            withStyle(SpanStyle(color = baseColor)) {
                append(text.substring(lastIndex))
            }
        }
    }
    Text(
        annotated,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        maxLines = 8,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily.Monospace
    )
}

// ═══════════════ Log Detail Screen (full content overlay) ═══════════════

@Composable
private fun LogDetailScreen(entry: LogEntry, appTheme: AppTheme, isDark: Boolean, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val bgColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.background
        AppTheme.LIQUID_GLASS -> GlassTokens.base(isDark)
        AppTheme.MD3 -> MaterialTheme.colorScheme.background
    }
    val onSurfaceColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurface
        AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurface
    }
    val onSurfaceVariantColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onSurfaceVariantSummary
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val outlineColor = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.outline
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.outline
    }
    val levelColor = when (entry.level) {
        LogLevel.DEBUG -> outlineColor
        LogLevel.INFO -> onSurfaceVariantColor
        LogLevel.HOOK -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.primary
            AppTheme.LIQUID_GLASS -> GlassTokens.accent(isDark)
            AppTheme.MD3 -> MaterialTheme.colorScheme.primary
        }
        LogLevel.ISLAND -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.secondary
            AppTheme.LIQUID_GLASS -> Color(0xFF5AC8FA)
            AppTheme.MD3 -> MaterialTheme.colorScheme.tertiary
        }
        LogLevel.WARN -> WarningOrange
        LogLevel.ERROR -> when (appTheme) {
            AppTheme.MIUIX -> MiuixTheme.colorScheme.error
            AppTheme.LIQUID_GLASS -> Color(0xFFFF3B30)
            AppTheme.MD3 -> MaterialTheme.colorScheme.error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Header
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
                    tint = onSurfaceColor
                )
            }
            Text(
                "日志详情",
                fontSize = when (appTheme) {
                    AppTheme.MIUIX -> 18.sp
                    AppTheme.MD3 -> 20.sp
                    AppTheme.LIQUID_GLASS -> 20.sp
                },
                fontWeight = FontWeight.SemiBold,
                color = onSurfaceColor,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Level + Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(levelColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        entry.level.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = levelColor
                    )
                }
                Text(
                    entry.tag,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata
            Text("时间", fontSize = 11.sp, color = outlineColor, fontWeight = FontWeight.Medium)
            Text(entry.timestamp, fontSize = 13.sp, color = onSurfaceVariantColor, fontFamily = FontFamily.Monospace)

            if (entry.pid > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("PID / TID", fontSize = 11.sp, color = outlineColor, fontWeight = FontWeight.Medium)
                Text("${entry.pid} / ${entry.tid}", fontSize = 13.sp, color = onSurfaceVariantColor, fontFamily = FontFamily.Monospace)
            }

            if (entry.packageName.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("包名", fontSize = 11.sp, color = outlineColor, fontWeight = FontWeight.Medium)
                Text(entry.packageName, fontSize = 13.sp, color = onSurfaceVariantColor, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full message
            Text("日志内容", fontSize = 11.sp, color = outlineColor, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))

            val detailShape = RoundedCornerShape(14.dp)
            val detailModifier = when (appTheme) {
                AppTheme.MIUIX -> Modifier
                    .fillMaxWidth()
                    .clip(detailShape)
                    .background(MiuixTheme.colorScheme.surfaceContainer)
                    .border(
                        width = 0.5.dp,
                        color = MiuixTheme.colorScheme.dividerLine,
                        shape = detailShape
                    )
                    .padding(14.dp)
                AppTheme.LIQUID_GLASS -> Modifier
                    .fillMaxWidth()
                    .clip(detailShape)
                    .drawBackdrop(
                        backdrop = rememberLayerBackdrop(),
                        shape = { detailShape },
                        effects = {
                            vibrancy()
                            blur(12f.dp.toPx())
                        },
                        onDrawSurface = {
                            drawRect(
                                (if (isDark) Color.White else Color.Black).copy(
                                    alpha = if (isDark) 0.08f else 0.04f
                                )
                            )
                        }
                    )
                    .border(
                        width = 0.5.dp,
                        brush = Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.04f),
                                )
                            } else {
                                listOf(
                                    Color.Black.copy(alpha = 0.08f),
                                    Color.Black.copy(alpha = 0.02f),
                                )
                            }
                        ),
                        shape = detailShape
                    )
                    .padding(14.dp)
                AppTheme.MD3 -> Modifier
                    .fillMaxWidth()
                    .clip(detailShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)
            }
            Box(modifier = detailModifier) {
                Text(
                    entry.message,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// Export text helper removed — ZIP export used via LogCollector.exportToZip()
