package com.kollider.engine.ecs.rendering

actual fun loadImageFromUrl(url: String): Any? {
    val bitmap = android.graphics.BitmapFactory.decodeStream(java.net.URL(url).openStream())
    return bitmap
}

actual fun getFrameFromSpriteSheet(image: Any?, rows: Int?, cols: Int?): List<Any>? {
    if (rows == null || cols == null) return null
    val bitmap = image as android.graphics.Bitmap
    val frameWidth = bitmap.width / cols
    val frameHeight = bitmap.height / rows
    val frames = mutableListOf<Any>()
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val frame = android.graphics.Bitmap.createBitmap(
                bitmap,
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