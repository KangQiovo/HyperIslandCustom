package com.kangqi.hIc.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import kotlin.math.atan2

/**
 * Provides device tilt as a normalized (x, y) in [-1, 1] derived from the
 * rotation-vector sensor. Falls back to accelerometer if rotation-vector is
 * unavailable. The values map roughly to:
 *   x → left/right tilt (roll)
 *   y → forward/backward tilt (pitch)
 *
 * A composable gradient brush can convert these into a highlight angle:
 *   angle = atan2(tilt.y, tilt.x)
 */
@Stable
class TiltState {
    /** Normalized left/right tilt in [-1, 1]. */
    var x by mutableFloatStateOf(0f)
        internal set

    /** Normalized forward/backward tilt in [-1, 1]. */
    var y by mutableFloatStateOf(0f)
        internal set

    /** Angle in radians from positive X-axis, derived from (x, y). */
    val angleRad: Float get() = atan2(y, x)

    /** Highlight position as (0..1, 0..1) for gradient stops. */
    val normalizedOffset: Offset
        get() = Offset((x + 1f) / 2f, (y + 1f) / 2f)
}

/** Provides the current [TiltState] to the composition tree. */
val LocalTiltState = compositionLocalOf { TiltState() }

/**
 * Remembers and manages a [TiltState] that updates from the device sensors.
 * Automatically registers/unregisters the listener with the composable lifecycle.
 */
@Composable
fun rememberTiltState(): TiltState {
    val context = LocalContext.current
    val state = remember { TiltState() }

    DisposableEffect(context) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            ?: return@DisposableEffect onDispose {}

        // Prefer game rotation vector (no magnetometer jitter); fall back to rotation vector, then accelerometer
        val sensor = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (sensor != null) {
            val rotMat = FloatArray(9)
            val orientation = FloatArray(3)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    SensorManager.getRotationMatrixFromVector(rotMat, event.values)
                    SensorManager.getOrientation(rotMat, orientation)
                    // orientation[1] = pitch (-π..π), orientation[2] = roll (-π..π)
                    val pitch = orientation[1]  // forward/backward
                    val roll = orientation[2]   // left/right
                    // Clamp to ±45° and normalize to [-1, 1]
                    val cap = (Math.PI / 4).toFloat()
                    state.x = (roll / cap).coerceIn(-1f, 1f)
                    state.y = (pitch / cap).coerceIn(-1f, 1f)
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            onDispose { sm.unregisterListener(listener) }
        } else {
            // Accelerometer-only fallback
            val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accel != null) {
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        val g = 9.81f
                        state.x = (-event.values[0] / g).coerceIn(-1f, 1f)
                        state.y = (event.values[1] / g).coerceIn(-1f, 1f)
                    }
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }
                sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
                onDispose { sm.unregisterListener(listener) }
            } else {
                onDispose {}
            }
        }
    }

    return state
}
