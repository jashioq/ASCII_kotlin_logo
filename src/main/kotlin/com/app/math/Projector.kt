package com.app.math

import org.joml.Vector3d
import kotlin.math.roundToInt

/**
 * Configuration for perspective projection.
 *
 * @param zOffset Camera distance from origin. Prevents division by zero and controls
 * how close objects can get before clipping. Larger values = camera further back.
 * @param scaleX Horizontal field of view scaling. Larger values = narrower FOV = more zoomed in.
 * Controls how much of the X-axis fits on screen. scaleY is automatically set to
 * scaleX/2 to compensate for terminal character aspect ratio (2:1 height:width).
 * @param screenWidth Width of the terminal in characters.
 * @param screenHeight Height of the terminal in characters.
 */
data class ProjectionConfig(
    val zOffset: Double = 5.0,
    val scaleX: Double = 140.0,
    val screenWidth: Int = 240,
    val screenHeight: Int = 80
) {
    val scaleY: Double = scaleX / 2.0
}

/**
 * Result of projecting a 3D point to 2D screen space.
 */
data class ProjectedPoint(
    val screenX: Int,
    val screenY: Int,
    val depth: Double
)

/**
 * Handles perspective projection from 3D world to 2D screen.
 */
class Projector(private val config: ProjectionConfig) {

    /**
     * Project a 3D point to 2D screen coordinates with perspective.
     *
     * Steps:
     * 1. Calculate oneOverZ = 1/(z + offset) for perspective division
     * 2. Multiply world X and Y coordinates by oneOverZ (closer objects appear larger)
     * 3. Apply field-of-view scaling (scaleX, scaleY) and center the result on screen
     * 4. Check if the resulting screen coordinates are within bounds, return null if outside
     *
     * The division by (z + offset) creates the perspective effect:
     * - Far objects (large z) -> small oneOverZ -> coordinates near center -> appear small
     * - Near objects (small z) -> large oneOverZ -> coordinates far from center -> appear large
     */
    fun project(point: Vector3d): ProjectedPoint? {
        val oneOverZ = 1.0 / (point.z + config.zOffset)

        val xScreen = (config.screenWidth / 2 + point.x * config.scaleX * oneOverZ).roundToInt()
        val yScreen = (config.screenHeight / 2 - point.y * config.scaleY * oneOverZ).roundToInt()

        return if (xScreen in 0 until config.screenWidth &&
            yScreen in 0 until config.screenHeight) {
            ProjectedPoint(xScreen, yScreen, oneOverZ)
        } else {
            null
        }
    }
}
