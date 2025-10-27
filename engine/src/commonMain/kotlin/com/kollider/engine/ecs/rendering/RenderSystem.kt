package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Vector2
import kotlin.math.min

/**
 * Iterates over drawable entities and issues drawing commands to the supplied [Renderer].
 *
 * ```kotlin
 * world.addSystem(RenderSystem(renderer, config))
 * world.createEntity().apply {
 *     add(Position(120f, 80f))
 *     add(Drawable.Rect(width = 32f, height = 32f, color = 0xFF00FF00.toInt()))
 * }
 * ```
 */
class RenderSystem(
    private val renderer: Renderer,
    private val config: GameConfig,
) : System() {
    override val runsWhilePaused: Boolean get() = true
    private lateinit var renderView: EntityView
    private var scale: Float = 1f
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var currentRenderWidth: Float = config.renderWidth.toFloat()
    private var currentRenderHeight: Float = config.renderHeight.toFloat()

    /**
     * Caches an [EntityView] that tracks `Position + Drawable` entities for quick iteration.
     */
    override fun onAttach(world: World) {
        renderView = world.view(Position::class, Drawable::class)
        currentRenderWidth = config.renderWidth.toFloat()
        currentRenderHeight = config.renderHeight.toFloat()
        recalculateScale()
        renderer.resize(currentRenderWidth.toInt(), currentRenderHeight.toInt())
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
        currentRenderWidth = width.toFloat()
        currentRenderHeight = height.toFloat()
        recalculateScale()
        renderer.resize(width, height)
    }

    private fun recalculateScale() {
        val targetWidth = config.width.toFloat()
        val targetHeight = config.height.toFloat()
        if (targetWidth <= 0f || targetHeight <= 0f) {
            scale = 1f
            offsetX = 0f
            offsetY = 0f
            return
        }

        val scaleCandidate = min(
            currentRenderWidth / targetWidth,
            currentRenderHeight / targetHeight,
        )
        scale = if (scaleCandidate.isFinite() && scaleCandidate > 0f) scaleCandidate else 1f
        offsetX = (currentRenderWidth - targetWidth * scale) * 0.5f
        offsetY = (currentRenderHeight - targetHeight * scale) * 0.5f
    }

    private fun renderDrawable(drawable: Drawable, baseX: Float, baseY: Float) {
        when (drawable) {
            is Drawable.Sprite -> {
                val drawX = offsetX + (baseX + drawable.offsetX) * scale
                val drawY = offsetY + (baseY + drawable.offsetY) * scale
                renderer.drawSprite(
                    drawable.spriteAsset,
                    drawable.width * scale,
                    drawable.height * scale,
                    drawX,
                    drawY,
                )
            }

            is Drawable.Rect -> {
                val drawX = offsetX + (baseX + drawable.offsetX) * scale
                val drawY = offsetY + (baseY + drawable.offsetY) * scale
                renderer.drawRect(
                    drawX,
                    drawY,
                    drawable.width * scale,
                    drawable.height * scale,
                    drawable.color
                )
            }

            is Drawable.Circle -> {
                val drawX = offsetX + (baseX + drawable.offsetX) * scale
                val drawY = offsetY + (baseY + drawable.offsetY) * scale
                renderer.drawCircle(
                    drawX,
                    drawY,
                    drawable.radius * scale,
                    drawable.color
                )
            }

            is Drawable.Line -> {
                val originX = baseX + drawable.offsetX
                val originY = baseY + drawable.offsetY
                val startX = offsetX + (originX + drawable.startX) * scale
                val startY = offsetY + (originY + drawable.startY) * scale
                val endX = offsetX + (originX + drawable.endX) * scale
                val endY = offsetY + (originY + drawable.endY) * scale
                renderer.drawLine(
                    startX,
                    startY,
                    endX,
                    endY,
                    drawable.thickness * scale,
                    drawable.color
                )
            }

            is Drawable.Polygon -> {
                val originX = baseX + drawable.offsetX
                val originY = baseY + drawable.offsetY
                val transformed = drawable.points.map { point ->
                    Vector2(
                        x = offsetX + (originX + point.x) * scale,
                        y = offsetY + (originY + point.y) * scale,
                    )
                }
                renderer.drawPolygon(transformed, drawable.color)
            }

            is Drawable.Text -> {
                val drawX = offsetX + (baseX + drawable.offsetX) * scale
                val drawY = offsetY + (baseY + drawable.offsetY) * scale
                renderer.drawText(
                    drawable.text,
                    drawX,
                    drawY,
                    drawable.size * scale,
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
