package com.app

import org.joml.Vector3d

/**
 * RGB color representation.
 */
data class Color(val r: Double, val g: Double, val b: Double) {
    companion object {
        val WHITE = Color(255.0, 255.0, 255.0)
        val KOTLIN_BLUE = Color(7.0, 174.0, 255.0)
        val KOTLIN_PURPLE = Color(148.0, 93.0, 255.0)
    }

    /**
     * Linearly interpolate between this color and another.
     * @param other The target color
     * @param t Interpolation factor [0, 1]. 0 = this color, 1 = other color
     */
    fun lerp(other: Color, t: Double): Color {
        val clampedT = t.coerceIn(0.0, 1.0)
        return Color(
            r + (other.r - r) * clampedT,
            g + (other.g - g) * clampedT,
            b + (other.b - b) * clampedT
        )
    }
}

/**
 * Defines a color gradient across a face.
 *
 * @param startCoord Starting point of the gradient in face-local coordinates [-1, 1]
 * @param endCoord Ending point of the gradient in face-local coordinates [-1, 1]
 * @param startColor Color at the start point
 * @param endColor Color at the end point
 */
data class ColorGradient(
    val startCoord: Vector3d,
    val endCoord: Vector3d,
    val startColor: Color,
    val endColor: Color
) {

    /**
     * Calculate the color at a specific point on the face.
     * Projects the point onto the gradient line and interpolates the color.
     */
    fun getColorAt(point: Vector3d): Color {
        val gradientVector = Vector3d(endCoord).sub(startCoord)
        val gradientLength = gradientVector.length()

        if (gradientLength < 0.0001) {
            // Gradient start and end are at the same point, return start color
            return startColor
        }

        val pointVector = Vector3d(point).sub(startCoord)

        // Project point onto gradient line and normalize to [0, 1]
        val projection = pointVector.dot(gradientVector) / (gradientLength * gradientLength)

        return startColor.lerp(endColor, projection)
    }

    companion object {
        /**
         * Creates a gradient with no color variation (solid color).
         */
        fun solid(color: Color): ColorGradient {
            return ColorGradient(
                Vector3d(-1.0, -1.0, 0.0),
                Vector3d(1.0, 1.0, 0.0),
                color,
                color
            )
        }
    }
}

/**
 * Represents one face of a 3D shape.
 */
data class Face(
    val normal: Vector3d,
    val getPoint: (horizontalCoord: Double, verticalCoord: Double) -> Vector3d,
    val gradient: ColorGradient = ColorGradient.solid(Color.WHITE)
)

/**
 * Represents a 3D geometry composed of multiple faces.
 */
data class Geometry(
    val faces: List<Face>
)

/**
 * Predefined 3D geometries.
 */
object Geometries {

    /**
     * A unit cube with edges from -1 to 1.
     */
    val cube = Geometry(
        faces = listOf(
            // Front face (Z=1): Bottom-Left to Top-Right
            Face(
                normal = Vector3d(0.0, 0.0, 1.0),
                getPoint = { h, v -> Vector3d(h, v, 1.0) },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, -1.0, 1.0),
                    endCoord = Vector3d(1.0, 1.0, 1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Back face (Z=-1): Bottom-Left to Top-Right
            Face(
                normal = Vector3d(0.0, 0.0, -1.0),
                getPoint = { h, v -> Vector3d(h, v, -1.0) },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, -1.0, -1.0),
                    endCoord = Vector3d(1.0, 1.0, -1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Top face (Y=1): Front-Left to Back-Right
            Face(
                normal = Vector3d(0.0, 1.0, 0.0),
                getPoint = { h, v -> Vector3d(h, 1.0, v) },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, 1.0, -1.0),
                    endCoord = Vector3d(1.0, 1.0, 1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Bottom face (Y=-1): Front-Left to Back-Right
            Face(
                normal = Vector3d(0.0, -1.0, 0.0),
                getPoint = { h, v -> Vector3d(h, -1.0, v) },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, -1.0, -1.0),
                    endCoord = Vector3d(1.0, -1.0, 1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Right face (X=1): Bottom-Back to Top-Front
            Face(
                normal = Vector3d(1.0, 0.0, 0.0),
                getPoint = { h, v -> Vector3d(1.0, h, v) },
                gradient = ColorGradient(
                    startCoord = Vector3d(1.0, -1.0, -1.0),
                    endCoord = Vector3d(1.0, 1.0, 1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Left face (X=-1): Bottom-Back to Top-Front
            Face(
                normal = Vector3d(-1.0, 0.0, 0.0),
                getPoint = { h, v -> Vector3d(-1.0, h, v) },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, -1.0, -1.0),
                    endCoord = Vector3d(-1.0, 1.0, 1.0),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            )
        )
    )

    /**
     * A triangular prism (cube with two opposite corners removed).
     * Has 2 triangular faces (top-left and bottom-right removed) and 3 rectangular faces.
     */
    val triangularPrism = Geometry(
        faces = listOf(
            Face(
                normal = Vector3d(0.0, 0.0, 1.0),
                getPoint = { h, v ->
                    if (v + h <= 0.0) Vector3d(h, v, 0.5)
                    else Vector3d(0.0, 0.0, 100.0)
                },
                gradient = ColorGradient(
                    startCoord = Vector3d(-1.0, -1.0, 0.5),
                    endCoord = Vector3d(0.5, 0.5, 0.5),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            ),

            // Back triangular face (Now at z = -0.5)
            Face(
                normal = Vector3d(0.0, 0.0, -1.0),
                getPoint = { h, v ->
                    if (v + h <= 0.0) Vector3d(h, v, -0.5)
                    else Vector3d(0.0, 0.0, 100.0)
                },
                gradient = ColorGradient.solid(Color.WHITE)
            ),

            // Bottom rectangular face (y = -1, z scaled to [-0.5, 0.5])
            Face(
                normal = Vector3d(0.0, -1.0, 0.0),
                getPoint = { h, v -> Vector3d(h, -1.0, v * 0.5) },
                gradient = ColorGradient.solid(Color.WHITE)
            ),

            // Left rectangular face (x = -1, z scaled to [-0.5, 0.5])
            Face(
                normal = Vector3d(-1.0, 0.0, 0.0),
                getPoint = { h, v -> Vector3d(-1.0, h, v * 0.5) },
                gradient = ColorGradient.solid(Color.WHITE)
            ),

            // Diagonal face (Hypotenuse)
            Face(
                normal = Vector3d(1.0, 1.0, 0.0).normalize(),
                getPoint = { h, v ->
                    val x = h           // x: -1 to 1
                    val y = -h          // y: 1 to -1
                    val z = v * 0.5     // z scaled: -0.5 to 0.5
                    Vector3d(x, y, z)
                },
                gradient = ColorGradient(
                    // Update gradient end-points to match new Z depth
                    startCoord = Vector3d(-1.0, 1.0, -0.5),
                    endCoord = Vector3d(1.0, -1.0, 0.5),
                    startColor = Color.KOTLIN_BLUE,
                    endColor = Color.KOTLIN_PURPLE
                )
            )
        )
    )
}
