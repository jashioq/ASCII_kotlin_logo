package com.app

import com.app.config.Configuration
import com.app.geometry.shapes.KotlinLogo
import com.app.lighting.PointLight
import com.app.math.ProjectionConfig
import com.app.rendering.RenderConfig
import com.app.rendering.Renderer
import org.joml.Vector3d

fun main() {
    val geometry = KotlinLogo.geometry

    val light = PointLight(
        Vector3d(
            Configuration.LIGHT_DIRECTION_X * Configuration.LIGHT_DISTANCE,
            Configuration.LIGHT_DIRECTION_Y * Configuration.LIGHT_DISTANCE,
            Configuration.LIGHT_DIRECTION_Z * Configuration.LIGHT_DISTANCE
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

    // Hide cursor using ANSI escape code
    print("\u001B[?25l")

    // Add shutdown hook to show cursor on exit
    Runtime.getRuntime().addShutdownHook(Thread {
        print("\u001B[?25h")
    })

    var angleX = 0.0
    var angleY = 0.0

    while (true) {
        val frame = renderer.renderFrame(angleX, angleY)

        // Move cursor up and to start of line using ANSI escape codes
        print("\u001B[${Configuration.SCREEN_HEIGHT}A\r")
        print(frame)

        angleX += Configuration.ROTATION_SPEED_X
        angleY += Configuration.ROTATION_SPEED_Y

        Thread.sleep(Configuration.FRAME_DELAY_MS)
    }
}
