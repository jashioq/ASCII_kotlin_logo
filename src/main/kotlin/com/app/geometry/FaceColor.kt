package com.app.geometry

import org.joml.Vector3d

/**
 * Defines how a face should be colored at any given point in 3D space.
 * Supports both solid colors and gradients.
 */
sealed interface FaceColor {
    /**
     * Gets the color at a specific 3D point on the face.
     *
     * @param point The 3D point to get the color for
     * @return The color at the specified point
     */
    fun getColorAt(point: Vector3d): Color

    /**
     * A solid color that is uniform across the entire face.
     *
     * @property color The color to apply
     */
    data class Solid(val color: Color) : FaceColor {
        override fun getColorAt(point: Vector3d) = color
    }

    /**
     * A linear gradient between two points in 3D space.
     * Colors are interpolated based on projection onto the gradient vector.
     *
     * @property startPoint The 3D point where the gradient starts
     * @property endPoint The 3D point where the gradient ends
     * @property startColor The color at the start point
     * @property endColor The color at the end point
     */
    data class Gradient(
        val startPoint: Vector3d,
        val endPoint: Vector3d,
        val startColor: Color,
        val endColor: Color
    ) : FaceColor {
        override fun getColorAt(point: Vector3d): Color {
            val gradientVector = Vector3d(endPoint).sub(startPoint)
            val gradientLength = gradientVector.length()

            // If start and end points are too close, gradient is meaningless
            val minGradientLength = 0.0001
            if (gradientLength < minGradientLength) return startColor

            val pointVector = Vector3d(point).sub(startPoint)
            val projection = pointVector.dot(gradientVector) / (gradientLength * gradientLength)
            return startColor.lerp(endColor, projection)
        }
    }
}
