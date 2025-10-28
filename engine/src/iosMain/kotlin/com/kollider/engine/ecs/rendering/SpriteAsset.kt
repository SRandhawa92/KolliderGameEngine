package com.kollider.engine.ecs.rendering

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRect
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage

actual fun loadImageFromUrl(url: String): Any? {
    val nsUrl = NSURL.URLWithString(url) ?: return null
    val data = NSData.dataWithContentsOfURL(nsUrl) ?: return null
    return UIImage(data = data)
}

@OptIn(ExperimentalForeignApi::class)
actual fun getFrameFromSpriteSheet(image: Any?, rows: Int?, cols: Int?): List<Any>? {
    if (rows == null || cols == null) return null
    val baseImage = image as? UIImage ?: return null
    val cgImage = baseImage.CGImage ?: return null

    val imageWidth = CGImageGetWidth(cgImage).toDouble()
    val imageHeight = CGImageGetHeight(cgImage).toDouble()
    val frameWidth = imageWidth / cols.toDouble()
    val frameHeight = imageHeight / rows.toDouble()
    val frames = mutableListOf<Any>()

    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val rect = cValue<CGRect> {
                origin.x = frameWidth * col
                origin.y = frameHeight * row
                size.width = frameWidth
                size.height = frameHeight
            }
            val cropped = CGImageCreateWithImageInRect(cgImage, rect) ?: continue
            frames.add(
                UIImage(
                    cGImage = cropped,
                    scale = baseImage.scale,
                    orientation = baseImage.imageOrientation,
                )
            )
        }
    }

    return frames
}
