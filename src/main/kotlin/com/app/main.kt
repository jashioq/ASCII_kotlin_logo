package com.app

import com.app.config.Configuration
import com.app.geometry.shapes.KotlinLogo
import com.app.lighting.PointLight
import com.app.math.ProjectionConfig
import com.app.rendering.RenderConfig
import com.app.rendering.Renderer
import com.github.ajalt.mordant.terminal.Terminal
import org.joml.Vector3d

fun main() {
    val terminal = Terminal()

    val geometry = KotlinLogo.geometry

    val light = PointLight(
        Vector3d(
            Configuration.LIGHT_X * Configuration.LIGHT_DISTANCE,
            Configuration.LIGHT_Y * Configuration.LIGHT_DISTANCE,
            Configuration.LIGHT_Z * Configuration.LIGHT_DISTANCE
        )
    )

    val projectionConfig = ProjectionConfig(
        zOffset = Configuration.Z_OFFSET,
        scaleX = Configuration.SCALE_X,
        screenWidth = Configuration.SCREEN_WIDTH,
        screenHeight = Configuration.SCREEN_HEIGHT
    )

    val renderer = Renderer(
        geometry = geometry,
        config = projectionConfig,
        light = light,
        renderConfig = RenderConfig(minColorBrightness = Configuration.MIN_COLOR_BRIGHTNESS)
    )

    terminal.cursor.hide(showOnExit = true)

    var angleX = 0.0
    var angleY = 0.0

    while (true) {
        val frame = renderer.renderFrame(angleX, angleY)

        terminal.cursor.move {
            up(Configuration.SCREEN_HEIGHT)
            startOfLine()
        }
        terminal.print(frame)

        angleX += Configuration.SPEED_A
        angleY += Configuration.SPEED_B

        Thread.sleep(Configuration.FRAME_DELAY_MS)
    }
}
