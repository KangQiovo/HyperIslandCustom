package com.kangqi.hIc.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangqi.hIc.model.IslandConfig
import com.kangqi.hIc.model.IslandTemplate
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperAlertDialog
import com.kangqi.hIc.ui.components.HyperButton
import com.kangqi.hIc.ui.components.HyperTextButton
import com.kangqi.hIc.ui.components.HyperTextField
import androidx.compose.material3.MaterialTheme
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.AppTheme
import java.util.UUID

@Composable
fun TemplatesScreen(
    templates: List<IslandTemplate>,
    onApplyTemplate: (IslandConfig) -> Unit,
    onSaveTemplate: (IslandTemplate) -> Unit,
    onDeleteTemplate: (String) -> Unit
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isMiuix) {
                item {
                    Text(
                        "模板管理", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            if (templates.isEmpty()) {
                item {
                    GlassCardColumn {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("暂无模板", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "点击右下角 + 创建超级岛模板",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            items(templates, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onApply = {
                        onApplyTemplate(
                            IslandConfig(
                                title = template.title, content = template.content,
                                duration = template.duration,
                                highlightColor = template.highlightColor,
                                borderColor = template.borderColor,
                                showProgress = template.showProgress,
                                showTimer = template.showTimer,
                                timerDurationMs = template.timerDurationMs,
                                templateType = template.templateType,
                                business = template.business
                            )
                        )
                    },
                    onDelete = { onDeleteTemplate(template.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "创建模板")
        }
    }

    CreateTemplateDialog(
        show = showCreateDialog,
        onDismiss = { showCreateDialog = false },
        onSave = { onSaveTemplate(it); showCreateDialog = false }
    )
}

@Composable
private fun TemplateCard(
    template: IslandTemplate,
    onApply: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCardColumn {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(parseColor(template.highlightColor))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        template.name, fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        buildString {
                            append("${template.duration / 1000}秒")
                            append(" · ${template.title}")
                            if (template.showProgress) append(" · 进度条")
                            if (template.showTimer) append(" · 倒计时")
                        },
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onApply) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "应用", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete, contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        if (template.content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(template.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
private fun CreateTemplateDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSave: (IslandTemplate) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var highlightColor by remember { mutableStateOf("#3482FF") }

    HyperAlertDialog(
        show = show,
        onDismissRequest = onDismiss,
        title = { Text("创建模板", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HyperTextField(
                    value = name, onValueChange = { name = it },
                    label = "模板名称"
                )
                HyperTextField(
                    value = title, onValueChange = { title = it },
                    label = "岛标题"
                )
                HyperTextField(
                    value = content, onValueChange = { content = it },
                    label = "岛内容"
                )
                HyperTextField(
                    value = highlightColor, onValueChange = { highlightColor = it },
                    label = "高亮颜色 (HEX)"
                )
            }
        },
        confirmButton = {
            HyperButton(
                text = "保存",
                onClick = {
                    if (name.isNotBlank() && title.isNotBlank()) {
                        onSave(
                            IslandTemplate(
                                id = UUID.randomUUID().toString(), name = name,
                                title = title, content = content,
                                highlightColor = highlightColor
                            )
                        )
                    }
                }
            )
        },
        dismissButton = {
            HyperTextButton(text = "取消", onClick = onDismiss, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    )
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Throwable) {
        Color.White
    }
}
