package com.kollider.flappybird

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.kollider.engine.core.AppContext
import com.kollider.engine.core.GameHandle
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private var gameHandle: GameHandle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.apply { set(this@MainActivity) }

        val metrics = resources.displayMetrics
        val screenHeight = metrics.heightPixels
        val screenWidth = metrics.widthPixels
        val virtualWidth = (screenWidth / metrics.density).roundToInt().coerceAtLeast(1)
        val virtualHeight = (screenHeight / metrics.density).roundToInt().coerceAtLeast(1)

        gameHandle = FlappyBird.createGame(
            virtualWidth = virtualWidth,
            virtualHeight = virtualHeight,
            renderWidth = screenWidth,
            renderHeight = screenHeight,
        )
    }

    override fun onResume() {
        super.onResume()
        gameHandle?.resume()
    }

    override fun onPause() {
        gameHandle?.pause()
        super.onPause()
    }

    override fun onDestroy() {
        gameHandle?.stop()
        gameHandle = null
        super.onDestroy()
    }
}
