package com.app

import org.joml.Vector3d
import kotlin.math.min
import kotlin.math.max

data class Color(val r: Double, val g: Double, val b: Double) {
    companion object {
        val WHITE = Color(255.0, 255.0, 255.0)
        val KOTLIN_BLUE = Color(7.0, 174.0, 255.0)
        val KOTLIN_PURPLE = Color(148.0, 93.0, 255.0)
        val KOTLIN_PINK = Color(199.0, 87.0, 188.0)
        val KOTLIN_ORANGE = Color(254.0, 137.0, 2.0)
    }

    fun lerp(other: Color, t: Double): Color {
        val clampedT = t.coerceIn(0.0, 1.0)
        return Color(
            r + (other.r - r) * clampedT,
            g + (other.g - g) * clampedT,
            b + (other.b - b) * clampedT
        )
    }
}

data class Vector2d(val x: Double, val y: Double)

sealed interface FaceColor {
    fun getColorAt(point: Vector3d): Color

    data class Solid(val color: Color) : FaceColor {
        override fun getColorAt(point: Vector3d) = color
    }

    data class Gradient(
        val startPoint: Vector3d,
        val endPoint: Vector3d,
        val startColor: Color,
        val endColor: Color
    ) : FaceColor {
        override fun getColorAt(point: Vector3d): Color {
            val gradientVector = Vector3d(endPoint).sub(startPoint)
            val gradientLength = gradientVector.length()
            if (gradientLength < 0.0001) return startColor

            val pointVector = Vector3d(point).sub(startPoint)
            val projection = pointVector.dot(gradientVector) / (gradientLength * gradientLength)
            return startColor.lerp(endColor, projection)
        }
    }
}

data class Face(
    val points: List<Vector3d>,
    val color: FaceColor = FaceColor.Solid(Color.WHITE)
) {
    val normal = Vector3d(points[1]).sub(points[0]).cross(Vector3d(points[2]).sub(points[0])).normalize()

    private val origin = points[0]
    private val uAxis = run {
        val arbitrary = if (kotlin.math.abs(normal.x) > 0.5) Vector3d(0.0, 1.0, 0.0) else Vector3d(1.0, 0.0, 0.0)
        Vector3d(arbitrary).sub(Vector3d(normal).mul(arbitrary.dot(normal))).normalize()
    }
    private val vAxis = Vector3d(normal).cross(uAxis).normalize()

    private val projected2D = points.map { p ->
        val rel = Vector3d(p).sub(origin)
        Vector2d(rel.dot(uAxis), rel.dot(vAxis))
    }

    private val minX = projected2D.minOf { it.x }
    private val maxX = projected2D.maxOf { it.x }
    private val minY = projected2D.minOf { it.y }
    private val maxY = projected2D.maxOf { it.y }

    fun getPoint(normalizedX: Double, normalizedY: Double): Vector3d? {
        // Map from normalized [-1,1] to actual face coordinate range
        val faceX = minX + (normalizedX + 1.0) * (maxX - minX) / 2.0
        val faceY = minY + (normalizedY + 1.0) * (maxY - minY) / 2.0

        if (!pointInPolygon(faceX, faceY)) return null

        return Vector3d(origin)
            .add(Vector3d(uAxis).mul(faceX))
            .add(Vector3d(vAxis).mul(faceY))
    }

    private fun pointInPolygon(testX: Double, testY: Double): Boolean {
        var intersections = 0
        for (i in projected2D.indices) {
            val v1 = projected2D[i]
            val v2 = projected2D[(i + 1) % projected2D.size]

            if (v1.y == v2.y) continue
            if (testY < min(v1.y, v2.y) || testY >= max(v1.y, v2.y)) continue

            val xIntersect = v1.x + (testY - v1.y) * (v2.x - v1.x) / (v2.y - v1.y)
            if (testX < xIntersect) intersections++
        }
        return intersections % 2 == 1
    }
}

data class Geometry(val faces: List<Face>)

object Geometries {
    val cube = Geometry(
        faces = listOf(
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, 1.0),
                    Vector3d(1.0, -1.0, 1.0),
                    Vector3d(1.0, 1.0, 1.0),
                    Vector3d(-1.0, 1.0, 1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            ),
            Face(
                points = listOf(
                    Vector3d(1.0, -1.0, -1.0),
                    Vector3d(-1.0, -1.0, -1.0),
                    Vector3d(-1.0, 1.0, -1.0),
                    Vector3d(1.0, 1.0, -1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, 1.0, 1.0),
                    Vector3d(1.0, 1.0, 1.0),
                    Vector3d(1.0, 1.0, -1.0),
                    Vector3d(-1.0, 1.0, -1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, -1.0),
                    Vector3d(1.0, -1.0, -1.0),
                    Vector3d(1.0, -1.0, 1.0),
                    Vector3d(-1.0, -1.0, 1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            ),
            Face(
                points = listOf(
                    Vector3d(1.0, -1.0, 1.0),
                    Vector3d(1.0, -1.0, -1.0),
                    Vector3d(1.0, 1.0, -1.0),
                    Vector3d(1.0, 1.0, 1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, -1.0),
                    Vector3d(-1.0, -1.0, 1.0),
                    Vector3d(-1.0, 1.0, 1.0),
                    Vector3d(-1.0, 1.0, -1.0)
                ),
                color = FaceColor.Solid(Color.WHITE)
            )
        )
    )

    val triangularPrism = Geometry(
        faces = listOf(
            // Top triangle
            Face(
                points = listOf(
                    Vector3d(0.0, 1.0, 0.25),
                    Vector3d(-1.0, 1.0, 0.25),
                    Vector3d(-1.0, 0.0, 0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, 0.0, 0.25), Vector3d(0.0, 1.0, 0.25),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, 0.0, -0.25),
                    Vector3d(-1.0, 1.0, -0.25),
                    Vector3d(0.0, 1.0, -0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, 0.0, -0.5), Vector3d(0.0, 1.0, -0.25),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, 0.0, 0.25),
                    Vector3d(-1.0, 1.0, 0.25),
                    Vector3d(-1.0, 1.0, -0.25),
                    Vector3d(-1.0, 0.0, -0.25)
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, 0.0, 0.0), Vector3d(-1.0, 2.0, 0.0),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, 1.0, 0.25),
                    Vector3d(0.0, 1.0, 0.25),
                    Vector3d(0.0, 1.0, -0.25),
                    Vector3d(-1.0, 1.0, -0.25)
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, 1.0, 0.0), Vector3d(1.0, 1.0, 0.0),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),

            // Orange rectangle
            Face(
                points = listOf(
                    Vector3d(-1.0, 0.0, 0.25),
                    Vector3d(-1.0, -1.0, 0.25),
                    Vector3d(1.0, 1.0, 0.25),
                    Vector3d(0.0, 1.0, 0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, 0.25), Vector3d(1.0, 1.0, 0.25),
                    Color.KOTLIN_PINK, Color.KOTLIN_ORANGE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, -0.25),
                    Vector3d(-1.0, 0.0, -0.25),
                    Vector3d(0.0, 1.0, -0.25),
                    Vector3d(1.0, 1.0, -0.25)
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, -0.25), Vector3d(1.0, 1.0, -0.25),
                    Color.KOTLIN_PINK, Color.KOTLIN_ORANGE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, 0.25),
                    Vector3d(-1.0, 0.0, 0.25),
                    Vector3d(-1.0, 0.0, -0.25),
                    Vector3d(-1.0, -1.0, -0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, 0.0), Vector3d(-1.0, 1.0, 0.0),
                    Color.KOTLIN_PINK, Color.KOTLIN_ORANGE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(0.0, 1.0, -0.25),
                    Vector3d(0.0, 1.0, 0.25),
                    Vector3d(1.0, 1.0, 0.25),
                    Vector3d(1.0, 1.0, -0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, 1.0, 0.0), Vector3d(1.0, 1.0, 0.0),
                    Color.KOTLIN_PINK, Color.KOTLIN_ORANGE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(0.0, 0.0, 0.25),
                    Vector3d(0.0, 0.0, -0.25),
                    Vector3d(1.0, 1.0, -0.25),
                    Vector3d(1.0, 1.0, 0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, 0.0), Vector3d(1.0, 1.0, 0.0),
                    Color.KOTLIN_PINK, Color.KOTLIN_ORANGE
                )
            ),

            // Bottom triangle
            Face(
                points = listOf(
                    Vector3d(1.0, -1.0, 0.25),
                    Vector3d(0.0, 0.0, 0.25),
                    Vector3d(-1.0, -1.0, 0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, 0.25), Vector3d(0.0, 0.0, 0.25),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, -0.25),
                    Vector3d(0.0, 0.0, -0.25),
                    Vector3d(1.0, -1.0, -0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, -0.25), Vector3d(0.0, 0.0, -0.25),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(-1.0, -1.0, -0.25),
                    Vector3d(1.0, -1.0, -0.25),
                    Vector3d(1.0, -1.0, 0.25),
                    Vector3d(-1.0, -1.0, 0.25),
                ),
                color = FaceColor.Gradient(
                    Vector3d(-1.0, -1.0, 0.0), Vector3d(1.0, -1.0, 0.0),
                    Color.KOTLIN_BLUE, Color.KOTLIN_PURPLE
                )
            ),
            Face(
                points = listOf(
                    Vector3d(1.0, -1.0, -0.25),
                    Vector3d(0.0, 0.0, -0.25),
                    Vector3d(0.0, 0.0, 0.25),
                    Vector3d(1.0, -1.0, 0.25),
                ),
                color = FaceColor.Solid(Color.KOTLIN_PURPLE)
            ),
        )
    )
}
