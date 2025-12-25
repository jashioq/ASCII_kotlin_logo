package com.app

/**
 * Configuration for ASCII rendering with colors.
 *
 * @param minColorBrightness Minimum brightness multiplier for colors [0, 1].
 *                           0.0 = colors can go completely black when unlit
 *                           0.5 = colors stay at least 50% bright even when unlit
 *                           1.0 = colors never darken (only character density changes)
 */
data class RenderConfig(
    val minColorBrightness: Double = 0.5
)

/**
 * Maps brightness values and colors to ASCII characters with ANSI color codes.
 */
object AsciiMapper {

    private const val RAMP = ".,-~:;=!*#\$@"

    /**
     * Convert brightness [0, 1] to ASCII character.
     * Characters progress from sparse (dark) to dense (bright).
     */
    fun brightnessToChar(brightness: Double): Char {
        val clamped = brightness.coerceIn(0.0, 1.0)
        val index = (clamped * (RAMP.length - 1)).toInt()
        return RAMP[index]
    }

    /**
     * Convert brightness and color to an ANSI colored character string.
     *
     * The character density represents brightness, and the color is darkened
     * based on brightness within the configured range.
     *
     * @param brightness Lighting intensity [0, 1] - controls character selection and color darkening
     * @param color RGB color to apply
     * @param config Rendering configuration controlling color darkening behavior
     * @return ANSI escape sequence + character + reset sequence
     */
    fun coloredChar(brightness: Double, color: Color, config: RenderConfig = RenderConfig()): String {
        val char = brightnessToChar(brightness)

        // Map brightness to color multiplier within [minColorBrightness, 1.0]
        // brightness=0.0 → minColorBrightness
        // brightness=1.0 → 1.0
        val colorMultiplier = config.minColorBrightness + (1.0 - config.minColorBrightness) * brightness

        // Apply color multiplier to RGB
        val r = (color.r * colorMultiplier).toInt().coerceIn(0, 255)
        val g = (color.g * colorMultiplier).toInt().coerceIn(0, 255)
        val b = (color.b * colorMultiplier).toInt().coerceIn(0, 255)

        // ANSI 24-bit true color: \u001b[38;2;R;G;Bm
        return "\u001b[38;2;${r};${g};${b}m$char\u001b[0m"
    }
}
