package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.input.Shoot
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.PlayerPaddleComponent

fun SceneScope.playerPaddle(config: GameConfig) {
    val paddle = createEntity {}

    paddle.add(Position(config.width / 2f, config.height - 60f))
    paddle.add(Velocity(0f, 0f))
    paddle.add(Collider(100f, 10f))
    paddle.add(Drawable.Rect(100f, 10f, 0xFFFFFFFF.toInt()))
    paddle.add(InputComponent().apply {
        bindAction(Shoot)
    })
    paddle.add(PlayerPaddleComponent())

    config.inputRouter.routeAction(Shoot, paddle.id)
    config.inputRouter.routeMovement(paddle.id)
}
