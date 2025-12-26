package com.app

import com.app.config.Configuration
import com.app.geometry.shapes.KotlinLogo
import com.app.lighting.PointLight
import com.app.math.ProjectionConfig
import com.app.rendering.RenderConfig
import com.app.rendering.Renderer
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.joml.Vector3d

fun main() = runBlocking {
    val terminal = Terminal()

    val geometry = KotlinLogo.geometry

    val light =
            PointLight(
                    Vector3d(
                            Configuration.LIGHT_X * Configuration.LIGHT_DISTANCE,
                            Configuration.LIGHT_Y * Configuration.LIGHT_DISTANCE,
                            Configuration.LIGHT_Z * Configuration.LIGHT_DISTANCE
                    )
            )

    val projectionConfig =
            ProjectionConfig(
                    zOffset = Configuration.Z_OFFSET,
                    scaleX = Configuration.SCALE_X,
                    screenWidth = Configuration.SCREEN_WIDTH,
                    screenHeight = Configuration.SCREEN_HEIGHT
            )

    val renderer =
            Renderer(
                    geometry = geometry,
                    config = projectionConfig,
                    light = light,
                    renderConfig =
                            RenderConfig(minColorBrightness = Configuration.MIN_COLOR_BRIGHTNESS)
            )

    val frameChannel = Channel<String>(capacity = Channel.BUFFERED) // 64 frame buffer

    // Rendering coroutine - runs on CPU thread pool
    launch(Dispatchers.Default) {
        // Thread-local variables - safe from cross-thread visibility issues
        var angleX = 0.0
        var angleY = 0.0

        while (isActive) {
            val frame = renderer.renderFrame(angleX, angleY)
            frameChannel.send(frame)

            angleX += Configuration.SPEED_A
            angleY += Configuration.SPEED_B

            delay(Configuration.FRAME_DELAY_MS)
        }
    }

    // Display with explicit cursor positioning
    terminal.cursor.hide(showOnExit = true)

    while (true) {
        // Get latest frame from channel without blocking
        var frame: String? = null
        while (true) {
            val next = frameChannel.tryReceive().getOrNull()
            if (next == null) break
            frame = next
        }

        if (frame != null) {
            terminal.cursor.move {
                up(Configuration.SCREEN_HEIGHT)
                startOfLine()
            }
            terminal.print(frame)
        }

        delay(Configuration.FRAME_DELAY_MS)
    }
}
