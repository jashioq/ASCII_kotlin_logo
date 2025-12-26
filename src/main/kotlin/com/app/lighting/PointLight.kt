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
     * 1. Get direction from surface to light: L = light_pos - surface_pos
     * 2. Normalize both the normal and light direction
     * 3. Compute dot product = cos(angle between them)
     * 4. Clamp to [0, 1] (surfaces facing away are dark)
     *
     * Formula: I = max(0, n̂ · L̂)
     */
    fun calculateDiffuseLighting(surfacePoint: Vector3d, surfaceNormal: Vector3d): Double {
        val toLightVector = Vector3d(position).sub(surfacePoint)
        val normalUnit = Vector3d(surfaceNormal).normalize()
        val lightUnit = toLightVector.normalize()
        return normalUnit.dot(lightUnit).coerceAtLeast(0.0)
    }
}
