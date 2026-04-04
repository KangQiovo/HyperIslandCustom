package com.kangqi.hIc.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.kangqi.hIc.ui.theme.LocalIsDark
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.GlassTokens
import com.kangqi.hIc.ui.theme.LocalBackdrop
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.Shadow
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.Switch as MiuixSwitch
import top.yukonga.miuix.kmp.basic.Slider as MiuixSlider
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.BasicComponent as MiuixBasicComponent
import top.yukonga.miuix.kmp.extra.SuperSwitch as MiuixSuperSwitch
import top.yukonga.miuix.kmp.basic.SmallTitle as MiuixSmallTitle
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixDivider
import top.yukonga.miuix.kmp.basic.Surface as MiuixSurface
import top.yukonga.miuix.kmp.theme.miuixShape
import androidx.compose.ui.graphics.lerp as colorLerp

// ─── HyperOS Native Switch ──────────────────────────────────────────────────

@Composable
fun HyperSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val themeManager = LocalThemeManager.current
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = modifier
        )
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        LiquidGlassToggle(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
        return
    }

    // MD3 - native Material3 Switch
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier
    )
}

// ─── HyperOS Native Slider ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true
) {
    val themeManager = LocalThemeManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val range = valueRange.endInclusive - valueRange.start
    val hapticThreshold = range * 0.05f
    var lastHapticValue by remember { mutableFloatStateOf(value) }

    val hapticOnValueChange: (Float) -> Unit = { newValue ->
        if (kotlin.math.abs(newValue - lastHapticValue) >= hapticThreshold) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            lastHapticValue = newValue
        }
        onValueChange(newValue)
    }

    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixSlider(
            value = value,
            onValueChange = hapticOnValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps
        )
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        LiquidGlassSlider(
            value = value,
            onValueChange = hapticOnValueChange,
            modifier = modifier,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled
        )
        return
    }

    // MD3 - native Material3 Slider with default colors
    Slider(
        value = value,
        onValueChange = hapticOnValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    )
}

// ─── HyperOS Primary Button ────────────────────────────────────────────────

@Composable
fun HyperButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    containerColor: Color = Color.Unspecified
) {
    val themeManager = LocalThemeManager.current
    val effectiveColor = containerColor.takeOrElse { MaterialTheme.colorScheme.primary }
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            colors = MiuixButtonDefaults.buttonColors(color = effectiveColor)
        ) {
            if (icon != null) {
                MiuixIcon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            MiuixText(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        LiquidGlassButton(onClick = onClick, modifier = modifier, enabled = enabled, surfaceColor = effectiveColor) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
        }
        return
    }

    // MD3 - native Material3 Button
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = effectiveColor,
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ─── HyperOS Secondary Button ──────────────────────────────────────────────

@Composable
fun HyperSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val themeManager = LocalThemeManager.current
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            colors = MiuixButtonDefaults.buttonColors(
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (icon != null) {
                MiuixIcon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            MiuixText(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        val isDark = LocalIsDark.current
        LiquidGlassButton(
            onClick = onClick,
            modifier = modifier,
            surfaceColor = (if (isDark) Color.White else Color.Black).copy(alpha = 0.08f)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    // MD3 - native Material3 OutlinedButton
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ─── HyperOS Text Field ────────────────────────────────────────────────────

@Composable
fun HyperTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    val themeManager = LocalThemeManager.current
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            useLabelAsPlaceholder = true,
            modifier = modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines
        )
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        // Liquid Glass - custom glass-style text field
        val isDark = LocalIsDark.current
        val accentColor = GlassTokens.accent(isDark)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = MaterialTheme.colorScheme.outline) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = (if (isDark) Color.White else Color.Black).copy(alpha = 0.15f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = accentColor,
                focusedLabelColor = accentColor,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines
        )
        return
    }

    // MD3 - native Material3 OutlinedTextField with default styling
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines
    )
}

// ─── HyperOS Settings Item ─────────────────────────────────────────────────

@Composable
fun HyperSettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val themeManager = LocalThemeManager.current
    val resolvedIconTint = iconTint.takeOrElse { MaterialTheme.colorScheme.onSurfaceVariant }
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixBasicComponent(
            title = title,
            summary = subtitle,
            startAction = icon?.let { {
                MiuixIcon(it, contentDescription = null, tint = resolvedIconTint, modifier = Modifier.size(22.dp))
            } },
            endActions = trailing?.let { { it() } },
            onClick = onClick,
            modifier = modifier
        )
        return
    }

    val titleColor = MaterialTheme.colorScheme.onSurface
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = resolvedIconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = titleColor,
                fontWeight = FontWeight.Normal
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = subtitleColor,
                    lineHeight = 16.sp
                )
            }
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        } else if (showArrow) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── HyperOS Toggle Item ───────────────────────────────────────────────────

@Composable
fun HyperToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified
) {
    val themeManager = LocalThemeManager.current
    val resolvedIconTint = iconTint.takeOrElse { MaterialTheme.colorScheme.onSurfaceVariant }
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixSuperSwitch(
            title = title,
            summary = subtitle,
            checked = checked,
            onCheckedChange = onCheckedChange,
            startAction = icon?.let { {
                MiuixIcon(it, contentDescription = null, tint = resolvedIconTint, modifier = Modifier.size(22.dp))
            } },
            modifier = modifier
        )
        return
    }

    HyperSettingsItem(
        title = title,
        subtitle = subtitle,
        icon = icon,
        iconTint = resolvedIconTint,
        modifier = modifier,
        onClick = { onCheckedChange(!checked) },
        trailing = {
            HyperSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

// ─── HyperOS Section Header ────────────────────────────────────────────────

@Composable
fun HyperSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val themeManager = LocalThemeManager.current
    if (themeManager.appTheme == AppTheme.MIUIX) {
        // HyperCeiler style: normal case section titles with SmallTitle
        MiuixSmallTitle(
            text = title,
            modifier = modifier.padding(top = 4.dp)
        )
        return
    }

    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp, top = 4.dp)
    )
}

// ─── HyperOS Divider ───────────────────────────────────────────────────────

@Composable
fun HyperDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 0.dp
) {
    val themeManager = LocalThemeManager.current
    if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixDivider(
            modifier = modifier.padding(start = startIndent)
        )
        return
    }

    if (themeManager.appTheme == AppTheme.LIQUID_GLASS) {
        val isDark = LocalIsDark.current
        val dividerColor = (if (isDark) Color.White else Color.Black).copy(alpha = 0.08f)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = startIndent)
                .height(0.5.dp)
                .background(dividerColor)
        )
    } else {
        // MD3 - native Material3 divider
        HorizontalDivider(
            modifier = modifier.padding(start = startIndent),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// ─── HyperOS Value Row ─────────────────────────────────────────────────────

@Composable
fun HyperValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = valueColor.takeOrElse { MaterialTheme.colorScheme.primary })
    }
}

// ─── Theme-aware AlertDialog ──────────────────────────────────────────────

@Composable
fun HyperAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    val themeManager = LocalThemeManager.current
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX

    if (isMiuix) {
        @Suppress("DEPRECATION")
        top.yukonga.miuix.kmp.extra.SuperDialog(
            show = remember { androidx.compose.runtime.mutableStateOf(true) },
            onDismissRequest = onDismissRequest,
            title = null,
        ) {
            title()
            Spacer(modifier = Modifier.height(12.dp))
            text()
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dismissButton != null) {
                    dismissButton()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                confirmButton()
            }
        }
    } else {
        val isGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS
        val isDark = LocalIsDark.current
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            containerColor = if (isGlass) {
                GlassTokens.surface(isDark).copy(alpha = 0.85f)
            } else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(if (isGlass) 20.dp else 16.dp),
            title = title,
            text = text,
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}

// ─── HyperOS FilterChip ────────────────────────────────────────────────────

@Composable
fun HyperFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val themeManager = LocalThemeManager.current
    val isGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS
    val isDark = LocalIsDark.current

    if (isGlass) {
        val accentColor = GlassTokens.accent(isDark)
        val selectedBg = accentColor.copy(alpha = 0.2f)
        val unselectedBg = (if (isDark) Color.White else Color.Black).copy(alpha = 0.06f)
        val textColor = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (selected) selectedBg else unselectedBg)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 13.sp, color = textColor, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        }
        return
    }

    if (themeManager.appTheme == AppTheme.MIUIX) {
        // Miuix — use FilterChip with Miuix-style colors
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = { Text(label, fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = modifier
        )
        return
    }

    // MD3 — pure native defaults (no custom colors, shape, or border)
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        modifier = modifier
    )
}

// ─── HyperOS TextButton ────────────────────────────────────────────────────

@Composable
fun HyperTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val themeManager = LocalThemeManager.current
    val isDark = LocalIsDark.current
    val isGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS

    val resolvedColor = if (color.isSpecified) color else {
        if (isGlass) GlassTokens.accent(isDark) else MaterialTheme.colorScheme.primary
    }

    Text(
        text = text,
        color = resolvedColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// ─── Liquid Glass Private Implementations ──────────────────────────────────
// ═══════════════════════════════════════════════════════════════════════════

// ─── LiquidGlass Toggle ────────────────────────────────────────────────────

@Composable
private fun LiquidGlassToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean
) {
    val isDark = LocalIsDark.current
    val rootBackdrop = LocalBackdrop.current ?: rememberLayerBackdrop()

    val accentColor = GlassTokens.toggleGreen(isDark)
    val trackColor = GlassTokens.trackColor(isDark)

    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val dragWidth = with(density) { 20f.dp.toPx() }
    val animationScope = rememberCoroutineScope()
    var didDrag by remember { mutableStateOf(false) }
    var fraction by remember { mutableFloatStateOf(if (checked) 1f else 0f) }

    val dampedDragAnimation = remember(animationScope) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = fraction,
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 1.5f,
            onDragStarted = {},
            onDragStopped = {
                if (didDrag) {
                    fraction = if (targetValue >= 0.5f) 1f else 0f
                    onCheckedChange(fraction == 1f)
                    didDrag = false
                } else {
                    fraction = if (checked) 0f else 1f
                    onCheckedChange(fraction == 1f)
                }
            },
            onDrag = { _, dragAmount ->
                if (!didDrag) didDrag = dragAmount.x != 0f
                val delta = dragAmount.x / dragWidth
                fraction = if (isLtr) (fraction + delta).fastCoerceIn(0f, 1f)
                else (fraction - delta).fastCoerceIn(0f, 1f)
            }
        )
    }

    LaunchedEffect(dampedDragAnimation) {
        snapshotFlow { fraction }
            .collectLatest { f -> dampedDragAnimation.updateValue(f) }
    }
    LaunchedEffect(checked) {
        val target = if (checked) 1f else 0f
        if (target != fraction) {
            fraction = target
            dampedDragAnimation.animateToValue(target)
        }
    }

    val trackBackdrop = rememberLayerBackdrop()

    Box(
        modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) { onCheckedChange(!checked) }
            .size(64.dp, 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track
        Box(
            Modifier
                .layerBackdrop(trackBackdrop)
                .clip(RoundedCornerShape(50))
                .drawBehind {
                    drawRect(colorLerp(trackColor, accentColor, dampedDragAnimation.value))
                }
                .size(64.dp, 28.dp)
        )

        // Thumb
        Box(
            Modifier
                .graphicsLayer {
                    val padding = 2f.dp.toPx()
                    translationX = if (isLtr) lerp(padding, padding + dragWidth, dampedDragAnimation.value)
                    else lerp(-padding, -(padding + dragWidth), dampedDragAnimation.value)
                }
                .semantics { role = Role.Switch }
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(rootBackdrop, trackBackdrop),
                    shape = { RoundedCornerShape(50) },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        blur(8f.dp.toPx() * (1f - progress))
                        lens(5f.dp.toPx() * progress, 10f.dp.toPx() * progress, chromaticAberration = true)
                    },
                    shadow = { Shadow(radius = 4f.dp, color = Color.Black.copy(alpha = 0.05f)) },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 50f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = 1f - progress))
                    }
                )
                .size(40.dp, 24.dp)
        )
    }
}

// ─── LiquidGlass Slider ────────────────────────────────────────────────────

@Composable
private fun LiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean
) {
    val isDark = LocalIsDark.current
    val rootBackdrop = LocalBackdrop.current ?: rememberLayerBackdrop()
    val accentColor = GlassTokens.accent(isDark)
    val trackColor = GlassTokens.trackColor(isDark)

    val trackBackdrop = rememberLayerBackdrop()
    val range = valueRange.endInclusive - valueRange.start
    val visibilityThreshold = range * 0.001f

    val snapToStep: (Float) -> Float = { v ->
        if (steps > 0) {
            val stepSize = range / (steps + 1)
            (((v - valueRange.start) / stepSize).fastRoundToInt() * stepSize + valueRange.start)
                .coerceIn(valueRange)
        } else v
    }

    BoxWithConstraints(
        modifier.fillMaxWidth().height(40.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val trackWidth = constraints.maxWidth
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        var didDrag by remember { mutableStateOf(false) }

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = value,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {},
                onDragStopped = {
                    if (didDrag) {
                        onValueChange(snapToStep(targetValue))
                        didDrag = false
                    }
                },
                onDrag = { _, dragAmount ->
                    if (!didDrag) didDrag = dragAmount.x != 0f
                    val delta = range * (dragAmount.x / trackWidth)
                    val newValue = if (isLtr) (targetValue + delta).coerceIn(valueRange)
                    else (targetValue - delta).coerceIn(valueRange)
                    onValueChange(snapToStep(newValue))
                }
            )
        }

        LaunchedEffect(value) {
            if (dampedDragAnimation.targetValue != value) {
                dampedDragAnimation.updateValue(value)
            }
        }

        // Track background + active fill
        Box(Modifier.layerBackdrop(trackBackdrop)) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(trackColor)
                    .pointerInput(animationScope) {
                        detectTapGestures { position ->
                            val delta = range * (position.x / trackWidth)
                            val targetValue = (if (isLtr) valueRange.start + delta
                            else valueRange.endInclusive - delta).coerceIn(valueRange)
                            val snapped = snapToStep(targetValue)
                            dampedDragAnimation.animateToValue(snapped)
                            onValueChange(snapped)
                        }
                    }
                    .height(6.dp)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(accentColor)
                    .height(6.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val width = (constraints.maxWidth * dampedDragAnimation.progress).fastRoundToInt()
                        layout(width, placeable.height) { placeable.place(0, 0) }
                    }
            )
        }

        // Thumb
        Box(
            Modifier
                .graphicsLayer {
                    translationX = (-size.width / 2f + trackWidth * dampedDragAnimation.progress)
                        .fastCoerceIn(-size.width / 4f, trackWidth - size.width * 3f / 4f) * if (isLtr) 1f else -1f
                }
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(rootBackdrop, trackBackdrop),
                    shape = { RoundedCornerShape(50) },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        blur(8f.dp.toPx() * (1f - progress))
                        lens(10f.dp.toPx() * progress, 14f.dp.toPx() * progress, chromaticAberration = true)
                    },
                    shadow = { Shadow(radius = 4f.dp, color = Color.Black.copy(alpha = 0.05f)) },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 10f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = 1f - progress))
                    }
                )
                .size(40.dp, 24.dp)
        )
    }
}

// ─── LiquidGlass Button ────────────────────────────────────────────────────

@Composable
private fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    surfaceColor: Color = Color.Unspecified,
    content: @Composable RowScope.() -> Unit
) {
    val rootBackdrop = LocalBackdrop.current ?: rememberLayerBackdrop()
    val animationScope = rememberCoroutineScope()

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(animationScope = animationScope)
    }

    Row(
        modifier
            .drawBackdrop(
                backdrop = rootBackdrop,
                shape = { RoundedCornerShape(50) },
                effects = {
                    vibrancy()
                    blur(2f.dp.toPx())
                    lens(12f.dp.toPx(), 24f.dp.toPx())
                },
                onDrawSurface = {
                    if (surfaceColor.isSpecified) {
                        drawRect(surfaceColor)
                    }
                }
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .then(interactiveHighlight.modifier)
            .then(interactiveHighlight.gestureModifier)
            .height(48.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
