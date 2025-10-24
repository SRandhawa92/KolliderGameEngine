package com.kollider.engine.ecs.rendering

actual fun loadImageFromUrl(url: String): Any? {
    val imageUrl = java.net.URL(url)
    return javax.imageio.ImageIO.read(imageUrl)
}

actual fun getFrameFromSpriteSheet(image: Any?, rows: Int?, cols: Int?): List<Any>? {
    if (rows == null || cols == null) return null
    val bufferedImage = image as java.awt.image.BufferedImage
    val frameWidth = bufferedImage.width / cols
    val frameHeight = bufferedImage.height / rows
    val frames = mutableListOf<Any>()
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val frame = bufferedImage.getSubimage(
                col * frameWidth,
                row * frameHeight,
                frameWidth,
                frameHeight
            )
            frames.add(frame)
        }
    }
    return frames
}