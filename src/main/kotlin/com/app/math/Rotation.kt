package com.app.math

import org.joml.Matrix3d
import org.joml.Vector3d
import kotlin.math.cos
import kotlin.math.sin

/**
 * Performs 3D rotations using explicit rotation matrix definitions.
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
        // Step 1: Rotate around X-axis
        val afterXRotation = rotateAroundX(vector, angleX)

        // Step 2: Rotate result around Y-axis
        return rotateAroundY(afterXRotation, angleY)
    }

    /**
     * Rotate a vector around the X-axis by the given angle.
     *
     * Rotation matrix for X-axis:
     * [1,    0,           0          ]
     * [0,    cos(angle), -sin(angle) ]
     * [0,    sin(angle),  cos(angle) ]
     *
     * @param vector The vector to rotate
     * @param angle Rotation angle in radians
     * @return A new rotated vector
     */
    private fun rotateAroundX(vector: Vector3d, angle: Double): Vector3d {
        val cosAngle = cos(angle)
        val sinAngle = sin(angle)

        val rotationMatrix = Matrix3d(
            1.0, 0.0,      0.0,
            0.0, cosAngle, -sinAngle,
            0.0, sinAngle,  cosAngle
        )

        return rotationMatrix.transform(Vector3d(vector))
    }

    /**
     * Rotate a vector around the Y-axis by the given angle.
     *
     * Rotation matrix for Y-axis:
     * [cos(angle),  0,  sin(angle) ]
     * [0,           1,  0          ]
     * [-sin(angle), 0,  cos(angle) ]
     *
     * @param vector The vector to rotate
     * @param angle Rotation angle in radians
     * @return A new rotated vector
     */
    private fun rotateAroundY(vector: Vector3d, angle: Double): Vector3d {
        val cosAngle = cos(angle)
        val sinAngle = sin(angle)

        val rotationMatrix = Matrix3d(
            cosAngle,  0.0, sinAngle,
            0.0,       1.0, 0.0,
            -sinAngle, 0.0, cosAngle
        )

        return rotationMatrix.transform(Vector3d(vector))
    }
}
