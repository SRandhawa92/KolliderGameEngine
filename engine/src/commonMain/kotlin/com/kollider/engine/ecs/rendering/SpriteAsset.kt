package com.kollider.engine.ecs.rendering

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

expect fun loadImageFromUrl(url: String): Any?
expect fun getFrameFromSpriteSheet(image: Any?, rows: Int?, cols: Int?): List<Any>?

/**
 * Represents a 2D spriteAsset used by the Renderer.
 * The [image] property is platform-specific.
 */
abstract class SpriteAsset {
    abstract val name: String
    abstract var image: Any?

    /**
     * Optional list of sub-sprites if this is a sprite sheet.
     */
    open var frames: List<Any>? = null
    open val frameDurationMs: Long? = null
}

class UrlSpriteSheetAsset(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    scope: CoroutineScope = CoroutineScope(dispatcher),
    override val name: String,
    imageUrl: String,
    rows: Int? = null,
    cols: Int? = null,
) : SpriteAsset() {
    override var image: Any? = null
    override var frames: List<Any>? = null

    init {
        scope.launch(dispatcher) {
            image = loadImageFromUrl(imageUrl)
            frames = getFrameFromSpriteSheet(image, rows, cols)
        }
    }
}

