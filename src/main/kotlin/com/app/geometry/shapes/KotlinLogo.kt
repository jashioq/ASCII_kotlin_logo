package com.app.geometry.shapes

import com.app.geometry.*
import org.joml.Vector3d

object KotlinLogo {
    val geometry = Geometry(
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
