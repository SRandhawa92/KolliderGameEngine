package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class RenderSystemTest {

    @Test
    fun `render system scales coordinates to render size`() {
        val renderer = RecordingRenderer()
        val config = GameConfig(width = 100, height = 100).apply {
            renderWidthOverride = 200
            renderHeightOverride = 200
        }
        val renderSystem = RenderSystem(renderer, config)
        val world = World()

        // entity with a rectangle drawable
        world.createEntity().apply {
            add(Position(10f, 20f))
            add(Rect(width = 30f, height = 40f, color = 0xFF00FF00.toInt()))
        }

        world.addSystem(renderSystem)

        world.update(0f)

        val rect = renderer.lastRect ?: error("Rectangle should have been rendered")
        // scale factor is 2.0 so expected x,y,width,height double
        assertEquals(20f, rect.x)
        assertEquals(40f, rect.y)
        assertEquals(60f, rect.width)
        assertEquals(80f, rect.height)

        // renderer should clear once per frame
        assertEquals(1, renderer.clears)
    }

    private class RecordingRenderer : Renderer {
        data class RectCall(val x: Float, val y: Float, val width: Float, val height: Float, val color: Int)

        var lastRect: RectCall? = null
        var clears: Int = 0

        override fun clear() { clears += 1 }

        override fun drawSprite(spriteAsset: SpriteAsset, width: Float, height: Float, x: Float, y: Float) {}

        override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {}

        override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
            lastRect = RectCall(x, y, width, height, color)
        }

        override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {}

        override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {}

        override fun drawPolygon(points: List<com.kollider.engine.ecs.physics.Vector2>, color: Int) {}

        override fun present() {}

        override fun resize(width: Int, height: Int) {}

        override fun dispose() {}
    }
}
