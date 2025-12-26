package com.app.geometry

data class Color(val r: Int, val g: Int, val b: Int) {
    companion object {
        val KOTLIN_BLUE = Color(7, 174, 255)
        val KOTLIN_PURPLE = Color(148, 93, 255)
        val KOTLIN_PINK = Color(199, 87, 188)
        val KOTLIN_ORANGE = Color(254, 137, 2)
    }

    fun lerp(other: Color, t: Double): Color {
        val clampedT = t.coerceIn(0.0, 1.0)
        return Color(
            (r + (other.r - r) * clampedT).toInt(),
            (g + (other.g - g) * clampedT).toInt(),
            (b + (other.b - b) * clampedT).toInt()
        )
    }
}
