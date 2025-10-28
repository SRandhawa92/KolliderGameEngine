package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.input.Shoot
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.engine.ecs.physics.Vector2
import com.kollider.flappybird.components.BirdAnimationComponent
import com.kollider.flappybird.components.BirdComponent

fun body() = Drawable.Circle(
    radius = 25f,
    color = 0xFFFFD54F.toInt(),
    offsetX = 25f,
    offsetY = 25f,
)

fun eye() = Drawable.Circle(
    radius = 6f,
    color = 0xFFFFFFFF.toInt(),
    offsetX = 34f,
    offsetY = 18f,
)

fun pupil() = Drawable.Circle(
    radius = 3f,
    color = 0xFF000000.toInt(),
    offsetX = 36f,
    offsetY = 18f,
)

fun beakPoints() = mutableListOf(
    Vector2(46f, 21f),
    Vector2(46f, 29f),
    Vector2(66f, 25f),
)

fun beak(points: MutableList<Vector2>) = Drawable.Polygon(
    points = points,
    color = 0xFFFFA726.toInt(),
)

fun wingPoints() = mutableListOf(
    Vector2(0f, 40f),
    Vector2(10f, 25f),
    Vector2(25f, 30f),
)

fun wing(points: MutableList<Vector2>) = Drawable.Polygon(
    points = points,
    color = 0xFFEF9A3A.toInt(),
)

fun SceneScope.bird(config: GameConfig): Entity {
    val bird = createEntity {
        add(BirdComponent())
        add(Position(100f, config.height / 2f))
        add(Velocity(0f, 0f))
        add(Collider(width = 50f, height = 50f))
        add(InputComponent().apply {
            bindAction(Shoot)
        })
        val body = body()
        val eye = eye()
        val pupil = pupil()
        val beakPoints = beakPoints()
        val beak = beak(beakPoints)

        val wingPoints = wingPoints()
        val wing = wing(wingPoints)
        val bodyParts = listOf(body, eye, pupil, beak, wing)
        add(Drawable.Composite(bodyParts))
        add(BirdAnimationComponent(wingPoints))
    }

    config.inputRouter.routeAction(Shoot, bird.id)
    config.inputRouter.routeMovement(bird.id)

    return bird
}
