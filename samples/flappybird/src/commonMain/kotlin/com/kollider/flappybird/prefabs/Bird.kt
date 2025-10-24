package com.kollider.flappybird.prefabs

import com.kollider.engine.assets.onReady
import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.input.Shoot
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.engine.ecs.rendering.UrlSpriteSheetAsset
import com.kollider.flappybird.components.BirdComponent

private const val BIRD_SPRITE_URL = "https://www.pikpng.com/pngl/b/305-3050375_this-free-icons-png-design-of-flying-game.png"

fun World.bird(config: GameConfig) {
    val birdSprite = UrlSpriteSheetAsset(
        name = "birdSprite",
        config = config,
        imageUrl = BIRD_SPRITE_URL,
        rows = 2,
        cols = 4
    )

    val bird = createEntity()

    bird.add(BirdComponent())
    bird.add(Position(100f, config.height / 2f))
    bird.add(Velocity(0f, 0f))
    bird.add(Collider(width = 50f, height = 50f))
    val placeholder = Drawable.Rect(50f, 50f, 0xFFFFFFFF.toInt())
    bird.add(placeholder)
    bird.add(InputComponent().apply {
        bindAction(Shoot)
    })

    config.inputRouter.routeAction(Shoot, bird.id)
    config.inputRouter.routeMovement(bird.id)

    birdSprite.handle.onReady(config.scope) {
        bird.remove(placeholder::class)
        bird.add(
            Drawable.Sprite(
                spriteAsset = birdSprite,
                width = 50f,
                height = 50f,
                offsetX = 0f,
                offsetY = 0f,
            )
        )
    }
}
