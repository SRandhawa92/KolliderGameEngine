package com.kollider.engine.ecs.rendering

import com.kollider.engine.assets.AssetHandle
import com.kollider.engine.core.GameConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Loads an image from a remote or local URL. Implemented per platform.
 */
expect fun loadImageFromUrl(url: String): Any?

/**
 * Extracts sprite sheet frames from [image] given row and column counts.
 */
expect fun getFrameFromSpriteSheet(image: Any?, rows: Int?, cols: Int?): List<Any>?

/**
 * Represents a renderer-ready sprite or sprite sheet.
 *
 * The underlying [image] type varies per platform (e.g., `BufferedImage` on JVM).
 *
 * ```kotlin
 * val sprite = object : SpriteAsset() {
 *     override val name = "player"
 *     override var image: Any? = loadFromDisk()
 * }
 * ```
 */
abstract class SpriteAsset {
    /**
     * Human-readable identifier used for caching and debugging.
     */
    abstract val name: String

    /**
     * Platform-specific image object, nullable until loading completes.
     */
    abstract var image: Any?

    /**
     * Optional list of sub-sprites if this is a sprite sheet.
     *
     * ```kotlin
     * sprite.frames?.forEach { frame -> animate(frame) }
     * ```
     */
    open var frames: List<Any>? = null

    /**
     * Duration for cycling frames in animated sprite sheets, in milliseconds.
     *
     * ```kotlin
     * sprite.frameDurationMs ?: 100L
     * ```
     */
    open val frameDurationMs: Long? = null

    /**
     * Associated asset handle, when the sprite is loaded asynchronously.
     *
     * ```kotlin
     * sprite.handle?.onReady(scope) { image -> cache(image) }
     * ```
     */
    open val handle: AssetHandle<Any>? = null
}

/**
 * Utility sprite asset that lazily loads a raster image from a URL and optionally slices it into frames.
 *
 * ```kotlin
 * val explosion = UrlSpriteSheetAsset(
 *     name = "explosion",
 *     config = config,
 *     imageUrl = "https://cdn.example.com/explosion.png",
 *     rows = 4,
 *     cols = 4
 * )
 * ```
 */
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
