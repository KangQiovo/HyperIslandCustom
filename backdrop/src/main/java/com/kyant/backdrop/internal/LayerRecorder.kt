package com.kyant.backdrop.internal

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.node.requireLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize

internal fun DelegatableNode.recordLayer(
    layer: GraphicsLayer,
    drawScope: DrawScope,
    size: IntSize = drawScope.size.toIntSize(),
    block: DrawScope.() -> Unit
) {
    val density = requireDensity()
    val layoutDirection = requireLayoutDirection()
    layer.record(density, layoutDirection, size) {
        val prevDensity = drawContext.density
        drawContext.density = density
        try {
            this.block()
        } finally {
            drawContext.density = prevDensity
        }
    }
}
