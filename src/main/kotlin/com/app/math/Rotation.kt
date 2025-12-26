package com.app.math

import org.joml.Vector3d

/**
 * Performs 3D rotations using JOML's built-in rotation methods.
 */
object Rotation {

    /**
     * Apply both X and Y rotations in sequence.
     *
     * Order matters: we rotate around X first, then Y.
     * This creates the tumbling motion of the cube.
     *
     * Rotation matrices:
     * Rx(theta): [1, 0, 0; 0, cos(theta), -sin(theta); 0, sin(theta), cos(theta)]
     * Ry(theta): [cos(theta), 0, sin(theta); 0, 1, 0; -sin(theta), 0, cos(theta)]
     */
    fun rotateXY(vector: Vector3d, angleX: Double, angleY: Double): Vector3d {
        return Vector3d(vector)
            .rotateX(angleX)
            .rotateY(angleY)
    }
}
