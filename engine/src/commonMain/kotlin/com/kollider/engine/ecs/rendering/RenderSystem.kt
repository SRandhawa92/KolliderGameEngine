package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.physics.Position

/**
 * The RenderSystem is responsible for drawing all entities that have rendering data.
 */
class RenderSystem(private val renderer: Renderer) : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Clear the screen.
        renderer.clear()

        // Iterate over all entities with both Position and Renderable components.
        entities.forEach { entity ->
            val position = entity.get(Position::class)
            val drawable = entity.get(Drawable::class)
            if (position != null && drawable != null) {

                // Draw the entity based on its drawable component.
                when (drawable) {
                    is Drawable.Sprite -> {
                        renderer.drawSprite(
                            drawable.spriteAsset,
                            position.x + drawable.offsetX,
                            position.y + drawable.offsetY
                        )
                    }

                    is Drawable.Rect -> {
                        renderer.drawRect(
                            position.x + drawable.offsetX,
                            position.y + drawable.offsetY,
                            drawable.width,
                            drawable.height,
                            drawable.color
                        )
                    }

                    is Drawable.Circle -> {
                        renderer.drawCircle(
                            position.x + drawable.offsetX,
                            position.y + drawable.offsetY,
                            drawable.radius,
                            drawable.color
                        )
                    }

                    is Drawable.Text -> {
                        renderer.drawText(
                            drawable.text,
                            position.x + drawable.offsetX,
                            position.y + drawable.offsetY,
                            drawable.size,
                            drawable.color
                        )
                    }
                }
            }
        }

        // Present the rendered frame.
        renderer.present()
    }

    override fun dispose() {
        renderer.dispose()
    }

    override fun resize(width: Int, height: Int) {
        renderer.resize(width, height)
    }
}
