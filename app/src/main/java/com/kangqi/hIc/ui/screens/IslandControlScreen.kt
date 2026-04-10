package com.kangqi.hIc.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kangqi.hIc.model.IslandConfig
import com.kangqi.hIc.model.IslandScenario
import com.kangqi.hIc.model.SpoofPackages
import com.kangqi.hIc.ui.components.GlassCardColumn
import com.kangqi.hIc.ui.components.HyperButton
import com.kangqi.hIc.ui.components.HyperDivider
import com.kangqi.hIc.ui.components.HyperFilterChip
import com.kangqi.hIc.ui.components.HyperSecondaryButton
import com.kangqi.hIc.ui.components.HyperSectionHeader
import com.kangqi.hIc.ui.components.HyperSlider
import com.kangqi.hIc.ui.components.HyperTextButton
import com.kangqi.hIc.ui.components.HyperTextField
import com.kangqi.hIc.ui.components.HyperToggleItem
import com.kangqi.hIc.ui.components.HyperValueRow
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IslandControlScreen(
    onShowIsland: (IslandConfig) -> Unit,
    onDismissIsland: () -> Unit,
    onPickImage: () -> Unit = {},
    customImageUri: String = "",
    onRestartClick: () -> Unit = {},
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX

    // ── Content ──
    var title by remember { mutableStateOf("HyperIsland") }
    var content by remember { mutableStateOf("自定义超级岛内容") }
    var frontTitle by remember { mutableStateOf("") }
    var ticker by remember { mutableStateOf("") }
    var aodTitle by remember { mutableStateOf("") }

    // ── Timing ──
    var duration by remember { mutableFloatStateOf(5f) }
    var isPersistent by remember { mutableStateOf(false) }

    // ── Colors ──
    var highlightColor by remember { mutableStateOf("#3482FF") }
    var borderColor by remember { mutableStateOf("#FFFFFF") }
    var progressColor by remember { mutableStateOf("#3482FF") }
    var emphasisColor by remember { mutableStateOf("#3482FF") }
    var islandBackgroundColor by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf("") }
    var contentColor by remember { mutableStateOf("") }

    // ── Progress ──
    var showProgress by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(50f) }
    var maxProgress by remember { mutableIntStateOf(100) }
    var progressType by remember { mutableIntStateOf(1) }
    var progressGradientStart by remember { mutableStateOf("") }
    var progressGradientEnd by remember { mutableStateOf("") }
    var progressNodeCount by remember { mutableIntStateOf(0) }

    // ── Timer ──
    var showTimer by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableFloatStateOf(60f) }

    // ── Scenario ──
    var selectedScenario by remember { mutableStateOf(IslandScenario.CUSTOM) }

    // ── Layout ──
    var leftComponentType by remember { mutableIntStateOf(1) }
    var rightComponentType by remember { mutableIntStateOf(2) }
    var rightText by remember { mutableStateOf("") }
    var showCoverImage by remember { mutableStateOf(false) }

    // ── Buttons ──
    var showButton by remember { mutableStateOf(false) }
    var buttonType by remember { mutableIntStateOf(1) }
    var buttonCount by remember { mutableIntStateOf(1) }
    var buttonText1 by remember { mutableStateOf("") }
    var buttonText2 by remember { mutableStateOf("") }
    var buttonText3 by remember { mutableStateOf("") }
    var buttonColor by remember { mutableStateOf("#3482FF") }
    var buttonTextColor by remember { mutableStateOf("#FFFFFF") }
    var showButtonProgress by remember { mutableStateOf(false) }
    var buttonProgressValue by remember { mutableFloatStateOf(0f) }

    // ── Small Island ──
    var smallIslandText by remember { mutableStateOf("") }
    var smallIslandType by remember { mutableIntStateOf(0) }
    var showSmallIslandProgress by remember { mutableStateOf(false) }

    // ── Rich Text ──
    var useHighLight by remember { mutableStateOf(true) }
    var delimiterVisible by remember { mutableStateOf(false) }

    // ── Behavior ──
    var islandFirstFloat by remember { mutableStateOf(true) }
    var enableFloat by remember { mutableStateOf(false) }
    var updatable by remember { mutableStateOf(false) }
    var isShowNotification by remember { mutableStateOf(true) }
    var substName by remember { mutableStateOf("HyperIsland Custom") }

    // ── Notification ──
    var notifTitle by remember { mutableStateOf("") }
    var notifContent by remember { mutableStateOf("") }

    // ── Package Spoofing (auto-linked to scenario) ──
    var spoofPackage by remember { mutableStateOf("") }
    var customSpoofPackage by remember { mutableStateOf("") }

    // ── Icon ──
    var iconPackage by remember { mutableStateOf("") }
    var showAppIconPicker by remember { mutableStateOf(false) }

    // ── Section visibility ──
    var showEffectsSection by remember { mutableStateOf(false) }
    var showLayoutSection by remember { mutableStateOf(false) }
    var showButtonSection by remember { mutableStateOf(false) }
    var showSmallIslandSection by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
    var showMoreColors by remember { mutableStateOf(false) }

    // ── Auto-link: scenario → spoof package ──
    LaunchedEffect(selectedScenario) {
        if (selectedScenario.recommendedPackage.isNotBlank()) {
            spoofPackage = selectedScenario.recommendedPackage
            customSpoofPackage = selectedScenario.recommendedPackage
        } else if (selectedScenario == IslandScenario.CUSTOM) {
            // Don't auto-clear if user manually set a package
        }
    }

    // ── Feature compatibility: auto-resolve conflicts ──
    LaunchedEffect(showProgress) {
        if (showProgress && showTimer) showTimer = false
    }
    LaunchedEffect(showTimer) {
        if (showTimer && showProgress) showProgress = false
    }
    LaunchedEffect(showCoverImage) {
        if (showCoverImage) rightComponentType = 6
    }
    LaunchedEffect(smallIslandType) {
        if (smallIslandType == 0) showSmallIslandProgress = false
    }

    if (showAppIconPicker) {
        AppIconPickerDialog(
            onDismiss = { showAppIconPicker = false },
            onAppSelected = { pkg ->
                iconPackage = pkg
                showAppIconPicker = false
            }
        )
    }

    val isDark = LocalIsDark.current
    val headerTextColor = when (themeManager.appTheme) {
        AppTheme.MIUIX -> MiuixTheme.colorScheme.onBackground
        AppTheme.LIQUID_GLASS -> if (isDark) Color.White else Color.Black
        AppTheme.MD3 -> MaterialTheme.colorScheme.onBackground
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ═══════════════ Fixed Header Row (does not scroll) ═══════════════
        // MD3/Glass: inline title + restart button on the same horizontal line
        // Miuix: only restart button (title is in MiuixTopAppBar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isMiuix) 16.dp else 24.dp,
                    end = 16.dp,
                    top = if (isMiuix) 0.dp else 16.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (!isMiuix) {
                Text(
                    "超级岛控制", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = headerTextColor,
                    modifier = Modifier.weight(1f)
                )
            }
            IconButton(onClick = onRestartClick) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "重启系统界面",
                    tint = headerTextColor
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
        // ═══════════════ 1. Scenario + Auto Spoof ═══════════════
        HyperSectionHeader("场景模板")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IslandScenario.entries.forEach { scenario ->
                    HyperFilterChip(
                        selected = selectedScenario == scenario,
                        onClick = { selectedScenario = scenario },
                        label = scenario.label
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════ 2. Content ═══════════════
        HyperSectionHeader("显示内容")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn(cornerRadius = 16.dp) {
            HyperTextField(
                value = title, onValueChange = { title = it },
                label = "大岛标题"
            )
            Spacer(modifier = Modifier.height(12.dp))
            HyperTextField(
                value = content, onValueChange = { content = it },
                label = "大岛内容", singleLine = false, minLines = 2, maxLines = 4
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════ 3. Duration ═══════════════
        HyperSectionHeader("显示时长")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            HyperToggleItem(
                title = "常驻",
                subtitle = "超级岛将持续显示，不会自动消失",
                checked = isPersistent,
                onCheckedChange = { isPersistent = it }
            )
            if (!isPersistent) {
                HyperDivider()
                Spacer(modifier = Modifier.height(8.dp))
                HyperValueRow("时长", "${duration.toInt()} 秒")
                Spacer(modifier = Modifier.height(8.dp))
                HyperSlider(
                    value = duration, onValueChange = { duration = it },
                    valueRange = 1f..60f, steps = 58
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════ 4. Colors (simplified) ═══════════════
        HyperSectionHeader("颜色")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            Text("高亮颜色", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            ColorPalette(selectedColor = highlightColor, onColorSelected = { highlightColor = it })

            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                HyperTextField(
                    value = borderColor,
                    onValueChange = { if (it.startsWith("#") && it.length <= 7) borderColor = it },
                    label = "边框", modifier = Modifier.weight(1f)
                )
                HyperTextField(
                    value = emphasisColor,
                    onValueChange = { if (it.startsWith("#") && it.length <= 7) emphasisColor = it },
                    label = "强调", modifier = Modifier.weight(1f)
                )
            }

            // Expandable extra colors
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showMoreColors = !showMoreColors },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("更多颜色", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    if (showMoreColors) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            AnimatedVisibility(visible = showMoreColors) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        HyperTextField(
                            value = islandBackgroundColor,
                            onValueChange = { if (it.isEmpty() || (it.startsWith("#") && it.length <= 7)) islandBackgroundColor = it },
                            label = "岛背景", modifier = Modifier.weight(1f)
                        )
                        HyperTextField(
                            value = progressColor,
                            onValueChange = { if (it.startsWith("#") && it.length <= 7) progressColor = it },
                            label = "进度颜色", modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        HyperTextField(
                            value = textColor,
                            onValueChange = { if (it.isEmpty() || (it.startsWith("#") && it.length <= 7)) textColor = it },
                            label = "标题颜色", modifier = Modifier.weight(1f)
                        )
                        HyperTextField(
                            value = contentColor,
                            onValueChange = { if (it.isEmpty() || (it.startsWith("#") && it.length <= 7)) contentColor = it },
                            label = "内容颜色", modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 5. Effects (Progress + Timer combined) ═══════════════
        CollapsibleSection(
            title = "效果 (进度条 / 倒计时)",
            expanded = showEffectsSection,
            onToggle = { showEffectsSection = !showEffectsSection }
        ) {
            // ── Progress ──
            HyperToggleItem("显示进度条", showProgress, { showProgress = it })
            AnimatedVisibility(visible = showProgress) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("进度类型", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1 to "线性", 2 to "状态", 3 to "分阶段").forEach { (type, label) ->
                            HyperFilterChip(
                                selected = progressType == type,
                                onClick = { progressType = type },
                                label = label
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HyperValueRow("进度", "${progress.toInt()}%")
                    HyperSlider(
                        value = progress, onValueChange = { progress = it },
                        valueRange = 0f..100f
                    )
                    AnimatedVisibility(visible = progressType == 3) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            HyperValueRow("节点数", "$progressNodeCount")
                            HyperSlider(
                                value = progressNodeCount.toFloat(),
                                onValueChange = { progressNodeCount = it.toInt() },
                                valueRange = 0f..4f, steps = 3
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), Arrangement.spacedBy(8.dp)) {
                        HyperTextField(
                            value = progressGradientStart,
                            onValueChange = { if (it.isEmpty() || (it.startsWith("#") && it.length <= 7)) progressGradientStart = it },
                            label = "渐变起始", modifier = Modifier.weight(1f)
                        )
                        HyperTextField(
                            value = progressGradientEnd,
                            onValueChange = { if (it.isEmpty() || (it.startsWith("#") && it.length <= 7)) progressGradientEnd = it },
                            label = "渐变结束", modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))

            // ── Timer ──
            HyperToggleItem("倒计时", showTimer, { showTimer = it })
            AnimatedVisibility(visible = showTimer) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HyperValueRow("时长", "${timerSeconds.toInt()} 秒")
                    HyperSlider(
                        value = timerSeconds, onValueChange = { timerSeconds = it },
                        valueRange = 10f..3600f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 6. Icon (enhanced with app picker) ═══════════════
        HyperSectionHeader("超级岛图标")
        Spacer(modifier = Modifier.height(4.dp))
        GlassCardColumn {
            // Show current icon state
            val context = LocalContext.current
            if (iconPackage.isNotBlank()) {
                val appLabel = remember(iconPackage) {
                    try {
                        val ai = context.packageManager.getApplicationInfo(iconPackage, 0)
                        context.packageManager.getApplicationLabel(ai).toString()
                    } catch (_: Exception) { iconPackage }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    AppIcon(iconPackage, Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("已选择: $appLabel", fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary)
                        Text(iconPackage, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            } else if (customImageUri.isNotBlank()) {
                Text(
                    "已选择自定义图片",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                HyperButton(
                    text = "从应用选择",
                    onClick = { showAppIconPicker = true },
                    modifier = Modifier.weight(1f)
                )
                HyperSecondaryButton(
                    text = "选择图片",
                    onClick = { iconPackage = ""; onPickImage() },
                    modifier = Modifier.weight(1f)
                )
            }

            if (iconPackage.isNotBlank() || customImageUri.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HyperTextButton(text = "清除图标", onClick = { iconPackage = "" }, color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 7. Big Island Layout ═══════════════
        CollapsibleSection(
            title = "大岛布局",
            expanded = showLayoutSection,
            onToggle = { showLayoutSection = !showLayoutSection }
        ) {
            Text("左侧组件", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "图文1", 2 to "图文2", 3 to "图文3").forEach { (type, label) ->
                    HyperFilterChip(
                        selected = leftComponentType == type,
                        onClick = { leftComponentType = type },
                        label = label
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("右侧组件", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "空", 1 to "文字", 2 to "图文", 3 to "进度文字",
                    4 to "定宽数字", 5 to "变宽数字", 6 to "大图"
                ).forEach { (type, label) ->
                    HyperFilterChip(
                        selected = rightComponentType == type,
                        onClick = { rightComponentType = type },
                        label = label
                    )
                }
            }
            AnimatedVisibility(visible = rightComponentType > 0) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HyperTextField(
                        value = rightText, onValueChange = { rightText = it },
                        label = "右侧文字内容"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HyperToggleItem("显示封面大图", showCoverImage, { showCoverImage = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 8. Buttons ═══════════════
        CollapsibleSection(
            title = "按钮组件",
            expanded = showButtonSection,
            onToggle = { showButtonSection = !showButtonSection }
        ) {
            HyperToggleItem("启用按钮", showButton, { showButton = it })
            AnimatedVisibility(visible = showButton) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HyperDivider(modifier = Modifier.padding(bottom = 8.dp))
                    Text("按钮类型", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { type ->
                            HyperFilterChip(
                                selected = buttonType == type,
                                onClick = { buttonType = type },
                                label = "类型$type"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HyperValueRow("数量", "$buttonCount")
                    HyperSlider(
                        value = buttonCount.toFloat(),
                        onValueChange = { buttonCount = it.toInt() },
                        valueRange = 1f..3f, steps = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HyperTextField(value = buttonText1, onValueChange = { buttonText1 = it }, label = "按钮1文字")
                    AnimatedVisibility(visible = buttonCount >= 2) {
                        HyperTextField(
                            value = buttonText2, onValueChange = { buttonText2 = it },
                            label = "按钮2文字", modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    AnimatedVisibility(visible = buttonCount >= 3) {
                        HyperTextField(
                            value = buttonText3, onValueChange = { buttonText3 = it },
                            label = "按钮3文字", modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        HyperTextField(
                            value = buttonColor,
                            onValueChange = { if (it.startsWith("#") && it.length <= 7) buttonColor = it },
                            label = "按钮颜色", modifier = Modifier.weight(1f)
                        )
                        HyperTextField(
                            value = buttonTextColor,
                            onValueChange = { if (it.startsWith("#") && it.length <= 7) buttonTextColor = it },
                            label = "文字颜色", modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HyperToggleItem("按钮进度条", showButtonProgress, { showButtonProgress = it })
                    AnimatedVisibility(visible = showButtonProgress) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            HyperValueRow("按钮进度", "${buttonProgressValue.toInt()}%")
                            HyperSlider(
                                value = buttonProgressValue,
                                onValueChange = { buttonProgressValue = it },
                                valueRange = 0f..100f
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 9. Small Island ═══════════════
        CollapsibleSection(
            title = "小岛配置",
            expanded = showSmallIslandSection,
            onToggle = { showSmallIslandSection = !showSmallIslandSection }
        ) {
            Text("小岛类型", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "图标", 1 to "图标+进度", 2 to "图标+文字").forEach { (type, label) ->
                    HyperFilterChip(
                        selected = smallIslandType == type,
                        onClick = { smallIslandType = type },
                        label = label
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(value = smallIslandText, onValueChange = { smallIslandText = it }, label = "小岛文字")
            Spacer(modifier = Modifier.height(8.dp))
            HyperToggleItem("小岛进度条", showSmallIslandProgress, { showSmallIslandProgress = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════ 10. Advanced (merged: ticker/AOD, rich text, behavior, notification, spoof override) ═══════════════
        CollapsibleSection(
            title = "高级选项",
            expanded = showAdvanced,
            onToggle = { showAdvanced = !showAdvanced }
        ) {
            // Ticker & AOD
            HyperTextField(value = frontTitle, onValueChange = { frontTitle = it }, label = "前置描述")
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(value = ticker, onValueChange = { ticker = it }, label = "状态栏文字 (Ticker)")
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(value = aodTitle, onValueChange = { aodTitle = it }, label = "息屏显示文字 (AOD)")

            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Rich text
            HyperToggleItem("高亮文字", useHighLight, { useHighLight = it })
            HyperDivider(modifier = Modifier.padding(vertical = 4.dp))
            HyperToggleItem("显示分隔符", delimiterVisible, { delimiterVisible = it })

            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Behavior
            HyperToggleItem("首次自动展开", islandFirstFloat, { islandFirstFloat = it })
            HyperDivider(modifier = Modifier.padding(vertical = 4.dp))
            HyperToggleItem("更新时展开", enableFloat, { enableFloat = it })
            HyperDivider(modifier = Modifier.padding(vertical = 4.dp))
            HyperToggleItem("持续更新 (常驻)", updatable, { updatable = it })
            HyperDivider(modifier = Modifier.padding(vertical = 4.dp))
            HyperToggleItem("显示通知", isShowNotification, { isShowNotification = it })

            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Notification
            HyperTextField(value = substName, onValueChange = { substName = it }, label = "通知来源名称")
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(value = notifTitle, onValueChange = { notifTitle = it }, label = "通知标题")
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(value = notifContent, onValueChange = { notifContent = it }, label = "通知内容")

            HyperDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Spoof package override
            Text("伪装应用 (手动覆盖)", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface)
            Text(
                "选择场景后会自动设置伪装应用，如需手动指定可在此修改",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpoofPackages.ALL.forEach { (pkg, label) ->
                    HyperFilterChip(
                        selected = spoofPackage == pkg,
                        onClick = { spoofPackage = pkg; customSpoofPackage = pkg },
                        label = label
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HyperTextField(
                value = customSpoofPackage,
                onValueChange = { customSpoofPackage = it; spoofPackage = it },
                label = "自定义包名"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════ Action Buttons ═══════════════
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
            HyperButton(
                text = "展示超级岛",
                onClick = {
                    onShowIsland(
                        IslandConfig(
                            title = title,
                            content = content,
                            frontTitle = frontTitle,
                            ticker = ticker,
                            aodTitle = aodTitle,
                            duration = if (isPersistent) 0 else duration.toInt() * 1000,
                            highlightColor = highlightColor,
                            borderColor = borderColor,
                            progressColor = progressColor,
                            emphasisColor = emphasisColor,
                            islandBackgroundColor = islandBackgroundColor,
                            textColor = textColor,
                            contentColor = contentColor,
                            showProgress = showProgress,
                            progress = progress.toInt(),
                            maxProgress = maxProgress,
                            progressType = progressType,
                            progressGradientStart = progressGradientStart,
                            progressGradientEnd = progressGradientEnd,
                            progressNodeCount = progressNodeCount,
                            showTimer = showTimer,
                            timerDurationMs = timerSeconds.toLong() * 1000,
                            templateType = selectedScenario.templateType,
                            business = selectedScenario.business,
                            leftComponentType = leftComponentType,
                            rightComponentType = rightComponentType,
                            rightText = rightText,
                            showCoverImage = showCoverImage,
                            showButton = showButton,
                            buttonType = buttonType,
                            buttonCount = buttonCount,
                            buttonText1 = buttonText1,
                            buttonText2 = buttonText2,
                            buttonText3 = buttonText3,
                            buttonColor = buttonColor,
                            buttonTextColor = buttonTextColor,
                            showButtonProgress = showButtonProgress,
                            buttonProgressValue = buttonProgressValue.toInt(),
                            islandFirstFloat = islandFirstFloat,
                            enableFloat = enableFloat,
                            updatable = isPersistent || updatable,
                            isShowNotification = isShowNotification,
                            substName = substName,
                            smallIslandText = smallIslandText,
                            smallIslandType = smallIslandType,
                            showSmallIslandProgress = showSmallIslandProgress,
                            useHighLight = useHighLight,
                            delimiterVisible = delimiterVisible,
                            notifTitle = notifTitle,
                            notifContent = notifContent,
                            spoofPackage = spoofPackage,
                            customImageUri = customImageUri,
                            iconPackage = iconPackage,
                        )
                    )
                },
                icon = Icons.Filled.PlayArrow,
                modifier = Modifier.weight(1f).height(52.dp)
            )
            HyperSecondaryButton(
                text = "关闭",
                onClick = onDismissIsland,
                icon = Icons.Filled.Stop,
                modifier = Modifier.weight(1f).height(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// themedFilterChip replaced by HyperFilterChip

// ═══════════════ App Icon Picker Dialog ═══════════════

private data class AppEntry(val packageName: String, val label: String)

@Composable
private fun AppIconPickerDialog(
    onDismiss: () -> Unit,
    onAppSelected: (packageName: String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolved = pm.queryIntentActivities(mainIntent, 0)
                .map { ri ->
                    AppEntry(
                        packageName = ri.activityInfo.packageName,
                        label = ri.loadLabel(pm).toString()
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }
            apps = resolved
            isLoading = false
        }
    }

    val filtered = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter { entry ->
            entry.label.contains(searchQuery, ignoreCase = true) ||
            entry.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    val themeManager = LocalThemeManager.current
    val isGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS
    val isDark = com.kangqi.hIc.ui.theme.LocalIsDark.current
    val dialogBg = if (isGlass) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    } else MaterialTheme.colorScheme.surface

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(dialogBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "选择应用图标", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                HyperTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "搜索应用名或包名"
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("加载中...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filtered) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onAppSelected(entry.packageName) }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(entry.packageName, Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        entry.label, fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        entry.packageName, fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HyperTextButton(
                    text = "取消",
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember(packageName) {
        try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            val w = drawable.intrinsicWidth.coerceAtLeast(1)
            val h = drawable.intrinsicHeight.coerceAtLeast(1)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bmp)
            drawable.setBounds(0, 0, w, h)
            drawable.draw(canvas)
            bmp.asImageBitmap()
        } catch (_: Exception) { null }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(8.dp))
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

// ═══════════════ Helper Composables ═══════════════

@Composable
private fun CollapsibleSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    GlassCardColumn(onClick = onToggle) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                title, fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorPalette(selectedColor: String, onColorSelected: (String) -> Unit) {
    val colors = listOf(
        "#3482FF", "#4CAF50", "#FF5722", "#FF9800", "#E91E63", "#9C27B0",
        "#00BCD4", "#FFEB3B", "#FFFFFF", "#F44336", "#3F51B5", "#009688"
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { hex ->
            val color = parseHexColor(hex)
            val isSelected = hex.equals(selectedColor, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        else Modifier.border(0.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                    .clickable { onColorSelected(hex) }
            )
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Throwable) {
        Color.White
    }
}
