package com.kollider.pong.scenes

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.Scene
import com.kollider.engine.core.SceneScope
import com.kollider.pong.levels.pongLevel
import com.kollider.pong.levels.startMenuLevel

class PongStartMenuScene(
    private val config: GameConfig
) : Scene {
    override fun onEnter(scope: SceneScope) {
        val engine = scope.engine
        scope.startMenuLevel(config) {
            engine.replaceScene(PongGameplayScene(config))
        }
    }
}

class PongGameplayScene(
    private val config: GameConfig
) : Scene {
    override fun onEnter(scope: SceneScope) {
        scope.pongLevel(config)
    }
}
