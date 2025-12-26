package com.app.config

object Configuration {
    // Lighting
    const val LIGHT_DISTANCE = 10000.0
    const val LIGHT_DIRECTION_X = 1.0
    const val LIGHT_DIRECTION_Y = 1.0
    const val LIGHT_DIRECTION_Z = -1.0

    // Animation
    const val ROTATION_SPEED_X = 0.02
    const val ROTATION_SPEED_Y = 0.012
    const val FRAME_DELAY_MS = 8L //8L is 120fps, 16L is 60fps

    // Projection
    const val Z_OFFSET = 5.0
    const val SCALE_X = 140.0
    const val SCREEN_WIDTH = 240
    const val SCREEN_HEIGHT = 60

    // Rendering
    const val MIN_COLOR_BRIGHTNESS = 0.85
    const val SAMPLING_STEP = 0.025
}
