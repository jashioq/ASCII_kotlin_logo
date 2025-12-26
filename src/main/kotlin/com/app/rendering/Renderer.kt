package com.app.rendering

import com.app.config.Configuration
import com.app.geometry.Face
import com.app.geometry.FaceColor
import com.app.geometry.Geometry
import com.app.lighting.PointLight
import com.app.math.Projector
import com.app.math.ProjectionConfig
import com.app.math.Rotation
import org.joml.Vector3d

/**
 * Main renderer that orchestrates the complete rendering pipeline.
 */
class Renderer(
    private val geometry: Geometry,
    private val config: ProjectionConfig,
    private val light: PointLight,
    private val renderConfig: RenderConfig = RenderConfig(),
    private val samplingStep: Double = Configuration.SAMPLING_STEP
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

                if (point != null) {
                    renderPoint(
                        point,
                        face.normal,
                        face.color,
                        angleX,
                        angleY
                    )
                }

                horizontalCoord += samplingStep
            }
            verticalCoord += samplingStep
        }
    }

    private fun renderPoint(
        point: Vector3d,
        normal: Vector3d,
        faceColor: FaceColor,
        angleX: Double,
        angleY: Double
    ) {
        // STEP 1: TRANSFORM - Rotate to world space
        val rotatedPoint = Rotation.rotateXY(point, angleX, angleY)
        val rotatedNormal = Rotation.rotateXY(normal, angleX, angleY)

        // STEP 2: COLOR - Calculate color based on 3D point position
        val color = faceColor.getColorAt(point)

        // STEP 3: LIGHTING - Calculate brightness
        val brightness = light.calculateDiffuseLighting(rotatedPoint, rotatedNormal)

        // STEP 4: PROJECT - Convert to screen coordinates
        val projected = projector.project(rotatedPoint) ?: return

        // STEP 5: RASTERIZE - Map to colored ASCII and draw with Z-test
        val coloredChar = AsciiMapper.coloredChar(brightness, color, renderConfig)
        frameBuffer.trySetPixel(projected.screenX, projected.screenY, projected.depth, coloredChar)
    }
}
