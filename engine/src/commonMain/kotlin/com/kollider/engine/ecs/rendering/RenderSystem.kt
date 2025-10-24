package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position

/**
 * Iterates over drawable entities and issues drawing commands to the supplied [Renderer].
 *
 * ```kotlin
 * world.addSystem(RenderSystem(renderer))
 * world.createEntity().apply {
 *     add(Position(120f, 80f))
 *     add(Drawable.Rect(width = 32f, height = 32f, color = 0xFF00FF00.toInt()))
 * }
 * ```
 */
class RenderSystem(private val renderer: Renderer) : System() {
    private lateinit var renderView: EntityView

    /**
     * Caches an [EntityView] that tracks `Position + Drawable` entities for quick iteration.
     */
    override fun onAttach(world: World) {
        renderView = world.view(Position::class, Drawable::class)
    }

    /**
     * Clears the frame, draws each entity, then presents the composed image.
     *
     * ```kotlin
     * renderSystem.update(entities, deltaTime)
     * ```
     */
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Clear the screen.
        renderer.clear()

        // Iterate over all entities with both Position and Renderable components.
        renderView.forEach { entity ->
            val position = entity.get(Position::class)
            val drawable = entity.get(Drawable::class)
            if (position != null && drawable != null) {
                renderDrawable(drawable, position.x, position.y)
            }
        }

        // Present the rendered frame.
        renderer.present()
    }

    /**
     * Disposes the renderer when the system is removed from the world.
     */
    override fun dispose() {
        renderer.dispose()
    }

    /**
     * Forwards the resize event to the renderer so it can adjust internal buffers.
     *
     * ```kotlin
     * renderSystem.resize(width, height)
     * ```
     */
    override fun resize(width: Int, height: Int) {
        renderer.resize(width, height)
    }

    private fun renderDrawable(drawable: Drawable, baseX: Float, baseY: Float) {
        when (drawable) {
            is Drawable.Sprite -> {
                renderer.drawSprite(
                    drawable.spriteAsset,
                    drawable.width,
                    drawable.height,
                    baseX + drawable.offsetX,
                    baseY + drawable.offsetY,
                )
            }

            is Drawable.Rect -> {
                renderer.drawRect(
                    baseX + drawable.offsetX,
                    baseY + drawable.offsetY,
                    drawable.width,
                    drawable.height,
                    drawable.color
                )
            }

            is Drawable.Circle -> {
                renderer.drawCircle(
                    baseX + drawable.offsetX,
                    baseY + drawable.offsetY,
                    drawable.radius,
                    drawable.color
                )
            }

            is Drawable.Text -> {
                renderer.drawText(
                    drawable.text,
                    baseX + drawable.offsetX,
                    baseY + drawable.offsetY,
                    drawable.size,
                    drawable.color
                )
            }

            is Drawable.Composite -> {
                val compositeBaseX = baseX + drawable.offsetX
                val compositeBaseY = baseY + drawable.offsetY
                drawable.elements.forEach { child ->
                    renderDrawable(child, compositeBaseX, compositeBaseY)
                }
            }
        }
    }
}
