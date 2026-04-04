package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.kangqi.hIc.ui.components.HyperFilterChip
import com.kangqi.hIc.ui.components.HyperPage
import com.kangqi.hIc.ui.components.HyperTextField
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.WarningOrange
import androidx.core.content.FileProvider
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private enum class LogFilter(val label: String) {
    ALL("全部"), DEBUG("调试"), INFO("信息"), HOOK("Hook"), ISLAND("岛触发"), WARN("警告"), ERROR("错误")
}

@Composable
fun LogViewerScreen(onBack: () -> Unit) {
    val themeManager = LocalThemeManager.current
    val appTheme = themeManager.appTheme
    val isDark = LocalIsDark.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LogFilter.ALL) }
    val moduleListState = rememberLazyListState()
    val lsposedListState = rememberLazyListState()

    val moduleLogs by LogCollector.moduleLogs.collectAsState()
    val lsposedLogs by LogCollector.lsposedLogs.collectAsState()

    LaunchedEffect(Unit) { LogCollector.syncLsposedLogs() }

    val currentLogs = if (selectedTab == 0) moduleLogs else lsposedLogs
    val listState = if (selectedTab == 0) moduleListState else lsposedListState

    val filteredLogs by remember(currentLogs, selectedFilter, searchQuery) {
        derivedStateOf {
            currentLogs.filter { entry ->
                val matchesFilter = when (selectedFilter) {
                    LogFilter.ALL -> true
                    LogFilter.DEBUG -> entry.level == LogLevel.DEBUG
                    LogFilter.INFO -> entry.level == LogLevel.INFO
                    LogFilter.HOOK -> entry.level == LogLevel.HOOK
                    LogFilter.ISLAND -> entry.level == LogLevel.ISLAND
                    LogFilter.WARN -> entry.level == LogLevel.WARN
                    LogFilter.ERROR -> entry.level == LogLevel.ERROR
                }
                val matchesSearch = searchQuery.isBlank() ||
                        entry.message.contains(searchQuery, ignoreCase = true) ||
                        entry.tag.contains(searchQuery, ignoreCase = true)
                matchesFilter && matchesSearch
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

    // Share action
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

    HyperPage(
        title = "运行日志",
        onBack = onBack,
        actions = {
            LogActionButtons(selectedTab, scope, listState, filteredLogs, onShare = onShare)
        }
    ) {
        // ═══════════════ Tab Selector ═══════════════
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
                LogFilter.entries.forEach { filter ->
                    HyperFilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
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
            AppTheme.MIUIX -> 2.dp
            AppTheme.MD3 -> 4.dp
            AppTheme.LIQUID_GLASS -> 4.dp
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
                    LogEntryRow(entry, searchQuery, appTheme, isDark)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ═══════════════ Shared Action Buttons ═══════════════

@Composable
private fun LogActionButtons(
    selectedTab: Int,
    scope: CoroutineScope,
    listState: LazyListState,
    filteredLogs: List<LogEntry>,
    onShare: () -> Unit
) {
    val themeManager = LocalThemeManager.current
    val appTheme = themeManager.appTheme
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
        IconButton(onClick = { LogCollector.clearModuleLogs() }) {
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
private fun LogEntryRow(entry: LogEntry, searchQuery: String, appTheme: AppTheme, isDark: Boolean) {
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

    val entryBg = when (appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.surfaceContainer
        AppTheme.LIQUID_GLASS -> (if (isDark) Color.White else Color.Black).copy(alpha = 0.06f)
        AppTheme.MD3 -> MaterialTheme.colorScheme.surfaceContainer
    }
    val cornerRadius = when (appTheme) {
        AppTheme.MIUIX -> 14.dp
        AppTheme.LIQUID_GLASS -> 12.dp
        AppTheme.MD3 -> 12.dp
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(entryBg)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
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
                Text(
                    entry.tag,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
