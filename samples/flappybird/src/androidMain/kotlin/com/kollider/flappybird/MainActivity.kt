package com.kollider.flappybird

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.kollider.engine.core.AppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the context to the AppContext for the game
        AppContext.apply { set(this@MainActivity) }

        // calculate the screen height and width
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels

        // start the game
        FlappyBird.createGame(screenHeight = screenHeight, screenWidth = screenWidth)
    }
}