package com.app

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
     * Rx(θ): [1, 0, 0; 0, cos(θ), -sin(θ); 0, sin(θ), cos(θ)]
     * Ry(θ): [cos(θ), 0, sin(θ); 0, 1, 0; -sin(θ), 0, cos(θ)]
     */
    fun rotateXY(v: Vector3d, angleX: Double, angleY: Double): Vector3d {
        return Vector3d(v)
            .rotateX(angleX)
            .rotateY(angleY)
    }
}
