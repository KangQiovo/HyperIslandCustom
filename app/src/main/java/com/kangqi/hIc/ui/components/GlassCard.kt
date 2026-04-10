package com.kangqi.hIc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.kangqi.hIc.ui.theme.LocalIsDark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp

/**
 * HyperOS-style card with subtle glass border effect.
 * Adapts to theme:
 * - MD3/Miuix: Solid surface fill
 * - Liquid Glass: Real-time blur using Backdrop API
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val themeManager = LocalThemeManager.current
    val isLiquidGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    val isDark = LocalIsDark.current
    val shape = RoundedCornerShape(cornerRadius)

    if (isMiuix) {
        MiuixCard(
            modifier = modifier,
            cornerRadius = cornerRadius,
            onClick = onClick
        ) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                this.content()
            }
        }
        return
    }

    if (!isLiquidGlass) {
        // MD3 — native Material3 Card with default styling
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors()
            ) {
                Box(Modifier.fillMaxWidth().padding(20.dp)) { content() }
            }
        } else {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors()
            ) {
                Box(Modifier.fillMaxWidth().padding(20.dp)) { content() }
            }
        }
        return
    }

    // Liquid Glass — real-time blur with tilt-reactive border
    val tilt = LocalTiltState.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBackdrop(
                backdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop(),
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
            .tiltGlassBorder(tilt, isDark, shape)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
            .padding(20.dp),
        content = content
    )
}

/**
 * Column variant of GlassCard for vertical layouts.
 */
@Composable
fun GlassCardColumn(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val themeManager = LocalThemeManager.current
    val isLiquidGlass = themeManager.appTheme == AppTheme.LIQUID_GLASS
    val isMiuix = themeManager.appTheme == AppTheme.MIUIX
    val isDark = LocalIsDark.current
    val shape = RoundedCornerShape(cornerRadius)

    if (isMiuix) {
        MiuixCard(
            modifier = modifier,
            cornerRadius = cornerRadius,
            onClick = onClick
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                this@MiuixCard.content()
            }
        }
        return
    }

    if (!isLiquidGlass) {
        // MD3 — native Material3 Card with default styling
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors()
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) { content() }
            }
        } else {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors()
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) { content() }
            }
        }
        return
    }

    // Liquid Glass — real-time blur with tilt-reactive border
    val tilt = LocalTiltState.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBackdrop(
                backdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop(),
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
            .tiltGlassBorder(tilt, isDark, shape)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        content = content
    )
}

/**
 * iOS 26-style tilt-reactive glass border.
 * A specular highlight sweeps along the border following the device tilt,
 * creating a liquid glass light/shadow effect.
 */
fun Modifier.tiltGlassBorder(
    tilt: TiltState,
    isDark: Boolean,
    shape: Shape,
    width: Dp = 0.5.dp,
): Modifier = this.drawWithContent {
    drawContent()

    val baseAlpha = if (isDark) 0.06f else 0.04f
    val peakAlpha = if (isDark) 0.45f else 0.55f

    // Convert tilt to angle in [0, 2π]
    val tiltAngle = (atan2(tilt.y, tilt.x) + 2f * PI.toFloat()) % (2f * PI.toFloat())

    // Generate sweep gradient with Gaussian highlight at tilt angle
    val sigma = 0.4f
    val stops = Array(25) { i ->
        val frac = i / 24f
        val stopAngle = frac * 2f * PI.toFloat()
        var diff = abs(stopAngle - tiltAngle)
        if (diff > PI.toFloat()) diff = 2f * PI.toFloat() - diff
        val intensity = exp(-(diff * diff) / (2f * sigma * sigma))
        frac to Color.White.copy(alpha = baseAlpha + (peakAlpha - baseAlpha) * intensity)
    }

    val brush = Brush.sweepGradient(
        *stops,
        center = Offset(size.width / 2f, size.height / 2f)
    )

    val outline = shape.createOutline(size, layoutDirection, this)
    val borderPath = when (outline) {
        is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
        is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is Outline.Generic -> outline.path
    }
    drawPath(path = borderPath, brush = brush, style = Stroke(width = width.toPx()))
}
