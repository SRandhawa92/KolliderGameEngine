package com.kollider.engine.ecs.rendering

import com.kollider.engine.assets.AssetHandle
import com.kollider.engine.core.GameConfig
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
    open val handle: AssetHandle<Any>? = null
}

class UrlSpriteSheetAsset(
    override val name: String,
    config: GameConfig,
    imageUrl: String,
    rows: Int? = null,
    cols: Int? = null,
) : SpriteAsset() {
    override var image: Any? = null
    override var frames: List<Any>? = null
    override val handle: AssetHandle<Any> =
        config.assets.load(name) {
            loadImageFromUrl(imageUrl)
                ?: throw IllegalStateException("Unable to load sprite asset: $imageUrl")
        }

    init {
        config.scope.launch(Dispatchers.Default) {
            try {
                val loaded = handle.awaitReady()
                image = loaded
                frames = getFrameFromSpriteSheet(loaded, rows, cols)
            } catch (_: Throwable) {
                frames = null
            }
        }
    }
}
