package com.app

import FrameBuffer
import org.joml.Vector3d

/**
 * Main renderer that orchestrates the complete rendering pipeline.
 */
class Renderer(
    private val geometry: Geometry,
    private val config: ProjectionConfig,
    private val light: PointLight,
    private val renderConfig: RenderConfig = RenderConfig(),
    private val samplingStep: Double = 0.025
) {

    private val projector = Projector(config)
    private val frameBuffer = FrameBuffer(config.screenWidth, config.screenHeight)

    /**
     * Render a single frame of the rotating geometry.
     */
    fun renderFrame(angleX: Double, angleY: Double): String {
        frameBuffer.clear()

        for (face in geometry.faces) {
            renderFace(face, angleX, angleY)
        }

        return frameBuffer.render()
    }

    /**
     * Render a single face by sampling it in a 2D grid pattern.
     */
    private fun renderFace(face: Face, angleX: Double, angleY: Double) {
        var verticalCoord = -1.0
        while (verticalCoord <= 1.0) {
            var horizontalCoord = -1.0
            while (horizontalCoord <= 1.0) {
                val point = face.getPoint(horizontalCoord, verticalCoord)

                // Skip points that are pushed far away (triangular face masking)
                if (point.z > 50.0 || point.z < -50.0) {
                    horizontalCoord += samplingStep
                    continue
                }

                // Pass the raw horizontal/vertical coords to help the gradient calculation
                renderPoint(
                    point,
                    horizontalCoord,
                    verticalCoord,
                    face.normal,
                    face.gradient,
                    angleX,
                    angleY
                )
                horizontalCoord += samplingStep
            }
            verticalCoord += samplingStep
        }
    }

    private fun renderPoint(
        point: Vector3d,
        h: Double, // Local horizontal
        v: Double, // Local vertical
        normal: Vector3d,
        gradient: ColorGradient,
        angleX: Double,
        angleY: Double
    ) {
        // STEP 1: TRANSFORM - Rotate to world space
        val rotatedPoint = Rotation.rotateXY(point, angleX, angleY)
        val rotatedNormal = Rotation.rotateXY(normal, angleX, angleY)

        // STEP 2: COLOR - Calculate color based on local face coordinates
        // We reconstruct a 2D point from our loop variables (h, v) to ensure
        // the gradient math in ColorGradient.getColorAt works regardless of 3D orientation.
        // We use the 'point' to stay compatible with your current getColorAt(Vector3d) signature.
        val color = gradient.getColorAt(point)

        // STEP 3: LIGHTING - Calculate brightness
        val brightness = light.calculateDiffuseLighting(rotatedPoint, rotatedNormal)

        // STEP 4: PROJECT - Convert to screen coordinates
        val projected = projector.project(rotatedPoint) ?: return

        // STEP 5: RASTERIZE - Map to colored ASCII and draw with Z-test
        val coloredChar = AsciiMapper.coloredChar(brightness, color, renderConfig)
        frameBuffer.trySetPixel(projected.screenX, projected.screenY, projected.depth, coloredChar)
    }
}
