package com.app.geometry

import org.joml.Vector3d

sealed interface FaceColor {
    fun getColorAt(point: Vector3d): Color

    data class Solid(val color: Color) : FaceColor {
        override fun getColorAt(point: Vector3d) = color
    }

    data class Gradient(
        val startPoint: Vector3d,
        val endPoint: Vector3d,
        val startColor: Color,
        val endColor: Color
    ) : FaceColor {
        override fun getColorAt(point: Vector3d): Color {
            val gradientVector = Vector3d(endPoint).sub(startPoint)
            val gradientLength = gradientVector.length()
            if (gradientLength < 0.0001) return startColor

            val pointVector = Vector3d(point).sub(startPoint)
            val projection = pointVector.dot(gradientVector) / (gradientLength * gradientLength)
            return startColor.lerp(endColor, projection)
        }
    }
}
