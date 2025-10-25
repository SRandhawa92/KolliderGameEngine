package com.kollider.flappybird

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.kollider.engine.core.AppContext
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the context to the AppContext for the game
        AppContext.apply { set(this@MainActivity) }

        // calculate the screen height and width
        val metrics = resources.displayMetrics
        val screenHeight = metrics.heightPixels
        val screenWidth = metrics.widthPixels
        val virtualWidth = (screenWidth / metrics.density).roundToInt().coerceAtLeast(1)
        val virtualHeight = (screenHeight / metrics.density).roundToInt().coerceAtLeast(1)

        // start the game
        FlappyBird.createGame(
            virtualWidth = virtualWidth,
            virtualHeight = virtualHeight,
            renderWidth = screenWidth,
            renderHeight = screenHeight,
        )
    }
}
