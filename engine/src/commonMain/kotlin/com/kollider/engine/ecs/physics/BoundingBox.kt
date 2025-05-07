package com.kollider.engine.ecs.physics

/**
 * Represents an axis-aligned bounding box used for collision detection.
 */
data class BoundingBox(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {
    /**
     * Checks if this bounding box intersects with another.
     */
    fun intersects(other: BoundingBox): Boolean =
        x < other.x + other.width &&
        x + width > other.x &&
        y < other.y + other.height &&
        y + height > other.y
}