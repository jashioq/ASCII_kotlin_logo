package com.app.geometry

import org.joml.Vector3d
import kotlin.math.max
import kotlin.math.min

/**
 * Represents a polygonal face in 3D space defined by a list of points.
 *
 * To efficiently test if a 2D coordinate maps to a point inside this face,
 * all 3D vertices are projected onto a local 2D coordinate system that lies
 * flat on the face's plane. This allows us to use simple 2D point-in-polygon
 * algorithms instead of complex 3D geometry tests.
 *
 * @property points The vertices of the face in 3D space (minimum 3 points)
 * @property color The coloring scheme for the face
 */
data class Face(
    val points: List<Vector3d>,
    val color: FaceColor = FaceColor.Solid(Color.KOTLIN_BLUE)
) {
    // The surface normal vector, computed from the first three points
    val normal = Vector3d(points[1]).sub(points[0]).cross(Vector3d(points[2]).sub(points[0])).normalize()

    // The origin point used as the reference for the 2D coordinate system
    private val origin = points[0]

    // The first basis vector (horizontal axis) of the 2D coordinate system on the face plane.
    // Created perpendicular to the normal vector to lie flat on the face's surface.
    private val firstBasisVector = run {
        val arbitrary = if (kotlin.math.abs(normal.x) > 0.5) Vector3d(0.0, 1.0, 0.0) else Vector3d(1.0, 0.0, 0.0)
        Vector3d(arbitrary).sub(Vector3d(normal).mul(arbitrary.dot(normal))).normalize()
    }

    // The second basis vector (vertical axis) of the 2D coordinate system on the face plane.
    // Perpendicular to both the normal and the first basis vector, completing the orthogonal basis.
    private val secondBasisVector = Vector3d(normal).cross(firstBasisVector).normalize()

    // All face points projected onto the 2D coordinate system
    private val projected2D = points.map { point ->
        val relativePosition = Vector3d(point).sub(origin)
        Vector2d(relativePosition.dot(firstBasisVector), relativePosition.dot(secondBasisVector))
    }

    // Bounding box minimum X coordinate in the 2D projection
    private val minX = projected2D.minOf { it.x }
    // Bounding box maximum X coordinate in the 2D projection
    private val maxX = projected2D.maxOf { it.x }
    // Bounding box minimum Y coordinate in the 2D projection
    private val minY = projected2D.minOf { it.y }
    // Bounding box maximum Y coordinate in the 2D projection
    private val maxY = projected2D.maxOf { it.y }

    /**
     * Maps normalized coordinates to a 3D point on the face, if it lies within the face polygon.
     *
     * This method:
     * 1. Converts normalized coordinates [-1, 1] to the face's local 2D coordinate system
     * 2. Checks if the resulting point lies inside the face's polygon boundary
     * 3. If inside, reconstructs the full 3D point by combining the origin with scaled basis vectors
     *
     * @param normalizedX X coordinate in the range [-1, 1]
     * @param normalizedY Y coordinate in the range [-1, 1]
     * @return The 3D point on the face, or null if the normalized coordinates map outside the polygon
     */
    fun getPoint(normalizedX: Double, normalizedY: Double): Vector3d? {
        // Map from normalized [-1,1] to actual face coordinate range
        val faceX = minX + (normalizedX + 1.0) * (maxX - minX) / 2.0
        val faceY = minY + (normalizedY + 1.0) * (maxY - minY) / 2.0

        if (!pointInPolygon(faceX, faceY)) return null

        return Vector3d(origin)
            .add(Vector3d(firstBasisVector).mul(faceX))
            .add(Vector3d(secondBasisVector).mul(faceY))
    }

    /**
     * Tests whether a 2D point lies inside the projected polygon using the ray casting algorithm.
     *
     * Algorithm: Cast a horizontal ray from the test point to the left (negative X direction).
     * Count how many polygon edges this ray crosses. If the count is odd, the point is inside.
     * If even, it's outside.
     *
     * For each edge of the polygon:
     * 1. Skip horizontal edges (they don't cross a horizontal ray)
     * 2. Skip edges that don't span the test point's Y coordinate
     * 3. Calculate where the ray intersects the edge's X coordinate
     * 4. Count intersections that occur to the left of the test point
     *
     * @param testX X coordinate in the 2D projection
     * @param testY Y coordinate in the 2D projection
     * @return True if the point is inside the polygon, false otherwise
     */
    private fun pointInPolygon(testX: Double, testY: Double): Boolean {
        var intersections = 0
        for (i in projected2D.indices) {
            val vertex1 = projected2D[i]
            val vertex2 = projected2D[(i + 1) % projected2D.size]

            if (vertex1.y == vertex2.y) continue
            if (testY < min(vertex1.y, vertex2.y) || testY >= max(vertex1.y, vertex2.y)) continue

            val xIntersect = vertex1.x + (testY - vertex1.y) * (vertex2.x - vertex1.x) / (vertex2.y - vertex1.y)
            if (testX < xIntersect) intersections++
        }
        return intersections % 2 == 1
    }
}
