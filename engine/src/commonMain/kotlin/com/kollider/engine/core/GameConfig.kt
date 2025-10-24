package com.kollider.engine.core

import com.kollider.engine.assets.AssetManager
import com.kollider.engine.ecs.input.InputRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

data class GameConfig(
    var width: Int = 800,
    var height: Int = 600,
    var title: String = "Kollider Game",
    var appContext: AppContext = AppContext,
    var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    val assets: AssetManager = AssetManager(scope),
    val inputRouter: InputRouter = InputRouter(),
) {
    val worldBounds: WorldBounds
        get() = WorldBounds(
            width = width.toFloat(),
            height = height.toFloat(),
        )
}
