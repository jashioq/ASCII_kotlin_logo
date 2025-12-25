package com.app

import com.github.ajalt.mordant.animation.textAnimation
import com.github.ajalt.mordant.terminal.Terminal
import org.joml.Vector3d

const val LIGHT_DISTANCE = 10000.0
const val SPEED_A = 0.015
const val SPEED_B = 0.012

fun main() {
    val terminal = Terminal()

    // Choose geometry to render (swap between Geometries.cube and Geometries.triangularPrism)
    val geometry = Geometries.cube

    val light = PointLight(
        Vector3d(
            1.0 * LIGHT_DISTANCE,
            -1.0 * LIGHT_DISTANCE,
            -1.0 * LIGHT_DISTANCE
        )
    )

    val projectionConfig = ProjectionConfig(
        zOffset = 5.0,
        scaleX = 140.0,
        screenWidth = 240,
        screenHeight = 80
    )

    val renderer = Renderer(
        geometry = geometry,
        config = projectionConfig,
        light = light,
        renderConfig = RenderConfig(minColorBrightness = 0.5)  // Colors stay at least 50% bright
    )

    var angleX = 0.0
    var angleY = 0.0

    val animation = terminal.textAnimation<Unit> {
        renderer.renderFrame(angleX, angleY)
    }

    terminal.cursor.hide(showOnExit = true)

    while (true) {
        animation.update(Unit)
        angleX += SPEED_A
        angleY += SPEED_B
        Thread.sleep(16)
    }
}
