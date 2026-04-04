package com.kyant.backdrop.effects

import android.os.Build
import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.toAndroidTileMode
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.internal.isRenderEffectSupported

fun BackdropEffectScope.blur(
    @FloatRange(from = 0.0) radius: Float,
    edgeTreatment: TileMode = TileMode.Clamp
) {
    if (!isRenderEffectSupported()) return
    if (radius <= 0f) return

    if (edgeTreatment != TileMode.Clamp || renderEffect != null) {
        if (radius > padding) {
            padding = radius
        }
    }

    val currentEffect = renderEffect
    @Suppress("NewApi")
    renderEffect =
        if (currentEffect != null) {
            android.graphics.RenderEffect.createBlurEffect(
                radius,
                radius,
                currentEffect.asAndroidRenderEffect(),
                edgeTreatment.toAndroidTileMode()
            ).asComposeRenderEffect()
        } else {
            android.graphics.RenderEffect.createBlurEffect(
                radius,
                radius,
                edgeTreatment.toAndroidTileMode()
            ).asComposeRenderEffect()
        }
}
