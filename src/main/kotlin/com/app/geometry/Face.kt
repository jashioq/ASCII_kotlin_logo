package com.app.geometry

import org.joml.Vector3d
import kotlin.math.max
import kotlin.math.min

data class Face(
    val points: List<Vector3d>,
    val color: FaceColor = FaceColor.Solid(Color.KOTLIN_BLUE)
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
