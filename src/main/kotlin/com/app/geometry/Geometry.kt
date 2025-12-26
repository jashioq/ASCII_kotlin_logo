package com.app.geometry

/**
 * Represents a 3D geometric shape as a collection of faces.
 * Used to define the structure of 3D objects that can be rendered.
 *
 * @property faces The list of polygonal faces that make up this geometry
 */
data class Geometry(val faces: List<Face>)
