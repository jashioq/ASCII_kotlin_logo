package com.app.geometry

/**
 * Represents an RGB color with integer components ranging from 0 to 255.
 *
 * @property r Red component (0-255)
 * @property g Green component (0-255)
 * @property b Blue component (0-255)
 */
data class Color(val r: Int, val g: Int, val b: Int) {
    companion object {
        val KOTLIN_BLUE = Color(7, 174, 255)
        val KOTLIN_PURPLE = Color(148, 93, 255)
        val KOTLIN_PINK = Color(199, 87, 188)
        val KOTLIN_ORANGE = Color(254, 137, 2)
    }

    /**
     * Performs linear interpolation between this color and another color.
     *
     * @param other The target color to interpolate towards
     * @param interpolationFactor Interpolation factor between 0.0 (this color) and 1.0 (other color)
     * @return A new color that is the linear interpolation between the two colors
     */
    fun lerp(other: Color, interpolationFactor: Double): Color {
        val clampedT = interpolationFactor.coerceIn(0.0, 1.0)
        return Color(
            (r + (other.r - r) * clampedT).toInt(),
            (g + (other.g - g) * clampedT).toInt(),
            (b + (other.b - b) * clampedT).toInt()
        )
    }
}
