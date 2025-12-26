package com.app.lighting

import org.joml.Vector3d

/**
 * A point light source at a fixed position in 3D space.
 */
data class PointLight(val position: Vector3d) {

    /**
     * Calculate diffuse lighting intensity using Lambert's Cosine Law.
     *
     * Steps:
     * 1. Get direction from surface to light: lightVector = light_position - surface_position
     * 2. Normalize both the normal and light direction to unit vectors
     * 3. Compute dot product = cosine of the angle between them
     * 4. Clamp to [0, 1] (surfaces facing away from light are dark)
     *
     * Formula: intensity = max(0, normalUnit dot lightUnit)
     * where normalUnit and lightUnit are normalized vectors
     */
    fun calculateDiffuseLighting(surfacePoint: Vector3d, surfaceNormal: Vector3d): Double {
        val toLightVector = Vector3d(position).sub(surfacePoint)
        val normalUnit = Vector3d(surfaceNormal).normalize()
        val lightUnit = toLightVector.normalize()
        return normalUnit.dot(lightUnit).coerceAtLeast(0.0)
    }
}
