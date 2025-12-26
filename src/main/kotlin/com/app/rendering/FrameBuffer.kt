package com.app.rendering

/**
 * Manages the 2D screen buffer with Z-buffering for depth testing.
 *
 * Z-buffer algorithm:
 * - For each pixel, store the depth (oneOverZ) of the closest surface
 * - When drawing, only update if the new surface is closer
 * - This solves the "which surface is visible" problem
 */
class FrameBuffer(private val width: Int, private val height: Int) {

    private val zBuffer = DoubleArray(width * height) { Double.NEGATIVE_INFINITY }
    private val displayBuffer = Array(width * height) { " " }

    /**
     * Attempt to set a pixel with Z-buffer testing.
     *
     * Only succeeds if this point is closer than what's already drawn.
     * Comparison: oneOverZ_new > oneOverZ_current means new point is CLOSER
     * (because oneOverZ = 1/z, so larger oneOverZ = smaller z = closer)
     */
    fun trySetPixel(x: Int, y: Int, depth: Double, content: String): Boolean {
        val index = x + y * width

        if (depth > zBuffer[index]) {
            zBuffer[index] = depth
            displayBuffer[index] = content
            return true
        }
        return false
    }

    fun clear() {
        zBuffer.fill(Double.NEGATIVE_INFINITY)
        displayBuffer.fill(" ")
    }

    fun render(): String {
        val output = StringBuilder()
        for (i in displayBuffer.indices) {
            if (i > 0 && i % width == 0) output.append('\n')
            output.append(displayBuffer[i])
        }
        return output.toString()
    }
}
